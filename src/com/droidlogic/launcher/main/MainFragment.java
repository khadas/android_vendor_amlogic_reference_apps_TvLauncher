package com.droidlogic.launcher.main;


import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.tv.TvInputManager;
import android.media.tv.TvView;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.VerticalGridView;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.app.AppModel;
import com.droidlogic.launcher.app.AppMoreModel;
import com.droidlogic.launcher.app.AppRow;
import com.droidlogic.launcher.app.gallery.AppGalleryActivity;
import com.droidlogic.launcher.base.LeanbackActivity;
import com.droidlogic.launcher.function.FunctionModel;
import com.droidlogic.launcher.input.InputModel;
import com.droidlogic.launcher.input.InputSourceManager;
import com.droidlogic.launcher.leanback.listrow.TvHeaderListRow;
import com.droidlogic.launcher.leanback.presenter.MainPresenterSelector;
import com.droidlogic.launcher.leanback.presenter.OnItemClickListener;
import com.droidlogic.launcher.livetv.MediaModel;
import com.droidlogic.launcher.livetv.TvControl;
import com.droidlogic.launcher.livetv.TvRow;
import com.droidlogic.launcher.model.TvViewModel;
import com.droidlogic.launcher.recommend.RecommendRow;
import com.droidlogic.launcher.util.AppStateChangeListener;
import com.droidlogic.launcher.util.AppUtils;
import com.droidlogic.launcher.util.Logger;
import com.droidlogic.launcher.util.StorageManagerUtil;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainFragment extends Fragment implements StorageManagerUtil.Listener {

    private final String TAG = MainFragment.class.getName();

    private static final String PACKAGE_LIVE_TV = "com.droidlogic.android.tv";

    private static final int MSG_LOAD_DATA = 100;

    private TimeDisplay mTimeDisplay;
    private DisplayMetrics mMetrics;
    private BackgroundManager mBackgroundManager;

    private AppRow mAppRow;
    private RecommendRow mRecommendRow;

    //===this is for live tv===========
    private TvControl mTvControl;
    private InputSourceManager mInputSource;

    //page top area : TvHeader
    private final TvHeaderListRow tvHeaderListRow = new TvHeaderListRow(new ArrayObjectAdapter());

    private VerticalGridView verticalGridView;
    private ArrayObjectAdapter mRowsAdapter;

    private AppStateChangeListener appListener;

    private boolean broadcastsRegisteredNetwork = false;

    private ImageView imgNetWork;

    private ImageView imgTfCard;

    private ImageView imgUsbDevice;

    private TextView tvMemory;

    private ProgressBar pbMemory;

    public MainFragment() {
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();
        startTimer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //===this is for live tv=================
        mInputSource = new InputSourceManager(getActivity(), new SourceStatusListener(), mLoadHandler);
        //=======================================
        mLoadHandler.sendEmptyMessageDelayed(MSG_LOAD_DATA, 1000);
        prepareBackgroundManager();
        initView(getView());
        initStorage();
    }

    @Override
    public void onResume() {
        super.onResume();
        initTime();
        //===this is for live tv and input Source
        tvHeaderListRow.signalUpdate();
        if (mTvControl != null) {
            mTvControl.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //===this is for live tv
        if (mTvControl != null) {
            mTvControl.pause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mTimeDisplay.unInit();
        //===this is for live tv=======
        if (mTvControl != null) {
            mTvControl.stop();
        }
        //==============================
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
        stopTimer();
        stopMemoryAnim();
        if (mLoadHandler != null) {
            mLoadHandler.removeCallbacksAndMessages(null);
            mLoadHandler = null;
        }
        if (storageManagerUtil != null) {
            storageManagerUtil.unRegisterListener();
        }
    }

    private StorageManagerUtil storageManagerUtil;

    private void initStorage() {
        if (storageManagerUtil == null) {
            storageManagerUtil = new StorageManagerUtil(getContext().getSystemService(StorageManager.class), MainFragment.this);
            storageManagerUtil.registerListener();
        }
    }

    //=======this is for live tv
    private void startTvApp(long id, String inputId, String type) {
        if (id == -1 && inputId == null) {
            mInputSource.switchInput(inputId, null);
            mInputSource.startInputAPP(inputId);
        } else {
            String name = mInputSource.getInputName(inputId);
            if (name == null) {
                name = "ATV";
            }
            if (!type.equals("TYPE_PAL") && !type.equals("TYPE_NTSC") && !type.equals("TYPE_SECAM")) {
                name = "DTV";
            }
            if (name.equals("ATV") && !mInputSource.isAtvSearch()) {
                killTvApp(); // for atv no channel
            }
            mInputSource.setSearchType(name);
            mTvControl.launchTvApp(id);
        }
    }

    private void startInputSourceApp(String inputId, String name) {
        if (name.equals("ATV") && !mInputSource.isAtvSearch()) {
            killTvApp(); // for atv no channel
        }
        mTvControl.releasePlayingTv();
        mInputSource.switchInput(inputId, name);
        mInputSource.startInputAPP(inputId);
    }

    private void startPlayInputSource(String inputId, String name) {
        if (name.equals("ATV") && !mInputSource.isAtvSearch()) {
            killTvApp(); // for atv no channel
        }
        mTvControl.releasePlayingTv();
        mInputSource.switchInput(inputId, name);
        mTvControl.play(inputId);
    }

    private void initTime() {
        TextView view = (TextView) getView().findViewById(R.id.tv_date);
        mTimeDisplay = new TimeDisplay(getActivity(), view);
        mTimeDisplay.init();
        mTimeDisplay.update();
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(this.getActivity().getWindow());
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        mBackgroundManager.setThemeDrawableResourceId(R.drawable.bg);
    }

    private void initView(View view) {
        if (view == null) return;
        view.findViewById(R.id.fun_content_search).setOnClickListener(view1 -> {
            if (getActivity() instanceof LeanbackActivity) {
                LeanbackActivity activity = (LeanbackActivity) getActivity();
                activity.onSearchRequested();
            }
        });
        view.findViewById(R.id.fun_memory_clean).setOnClickListener(view12 -> {
            if (isMemoryAnimRunning()) return;
            ActivityManager.MemoryInfo memoryInfo = AppUtils.getMemoryInfo(getContext());
            float total = memoryInfo.totalMem;
            float beforeMemory = memoryInfo.availMem;
            AppUtils.killRunningProcesses(getContext());
            float after = AppUtils.getAvailMemory(getContext());
            //Logger.i("collectionMemory:" + collectionMemory);
            reStartMemoryAnim(beforeMemory, after, total);
            Toast.makeText(getContext(), R.string.clean_memory_notice, Toast.LENGTH_SHORT).show();
        });
        pbMemory = (ProgressBar) view.findViewById(R.id.pb_status_bar_memory);
        tvMemory = (TextView) view.findViewById(R.id.tv_status_bar_memory);
        imgNetWork = (ImageView) view.findViewById(R.id.iv_network);
        imgTfCard = (ImageView) view.findViewById(R.id.iv_tf_card);
        imgUsbDevice = (ImageView) view.findViewById(R.id.iv_usb_device);

        verticalGridView = (VerticalGridView) view.findViewById(R.id.vtl_grid_view);
        verticalGridView.setVerticalSpacing((int) getResources().getDimension(R.dimen.main_page_vtl_space));
        mRowsAdapter = new ArrayObjectAdapter(new MainPresenterSelector(mInputSource, new OnItemClickListener() {
            @Override
            public void onPresenterItemClick(View view, Object item) {
                if (item instanceof MediaModel) {
                    MediaModel model = (MediaModel) item;
                    startTvApp(model.getId(), model.getInputId(), model.getType());
                } else if (item instanceof AppModel) {
                    AppModel appBean = (AppModel) item;
                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(appBean.getPackageName());
                    if (launchIntent != null) {
                        startActivity(launchIntent);
                    }
                } else if (item instanceof AppMoreModel) {
                    startActivity(new Intent(getContext(), AppGalleryActivity.class));
                } else if (item instanceof FunctionModel) {
                    FunctionModel model = (FunctionModel) item;
                    Intent intent = model.getIntent();
                    if (intent == null && !TextUtils.isEmpty(model.getPackageName())) {
                        intent = getActivity().getPackageManager().getLaunchIntentForPackage(model.getPackageName());
                        model.setIntent(intent);
                    }
                    if (intent != null) {
                        startActivity(intent);
                    }
                } else if (item instanceof InputModel) {
                    InputModel model = (InputModel) item;
                    //startInputSourceApp(model.getId(), model.getName());
                    startPlayInputSource(model.getId(), model.getName());
                } else if (item instanceof TvViewModel) {
                    //((TvViewModel) item).lunch(getContext());
                    mTvControl.releasePlayingTv();
                    mInputSource.startInputAPP(null);
                }
            }
        }));

        //add function row
        addTvHeaderView();
        addAppRow();
        //addVideoRow();
        verticalGridView.setAdapter(new ItemBridgeAdapter(mRowsAdapter));

        verticalGridView.post(new Runnable() {
            @Override
            public void run() {
                //default focus view
                View defaultFocusView = verticalGridView.findViewById(R.id.border_view_holder);
                if (defaultFocusView != null) {
                    defaultFocusView.requestFocus();
                    defaultFocusView.setNextFocusUpId(R.id.fun_content_search);
                }
                //initial tvControl
                TvView tvView = (TvView) verticalGridView.findViewById(R.id.tv_view);
                TextView tvPrompt = (TextView) getActivity().findViewById(R.id.tx_tv_prompt);
                if (tvView != null) {
                    mTvControl = new TvControl(getActivity(), tvView, tvPrompt);
                    mTvControl.resume();
                }
            }
        });
    }

    private void addTvHeaderView() {
        mRowsAdapter.add(tvHeaderListRow);
    }

    private void addVideoRow() {
        String headerName = getResources().getString(R.string.app_header_video_name);
        TvRow mTvRow = new TvRow(getActivity());
    }

    private void addAppRow() {
        String headerName = getResources().getString(R.string.app_header_app_name);
        mAppRow = new AppRow(getActivity(), headerName, mRowsAdapter);
    }

    private void addRecommendRow() {
        mRecommendRow = new RecommendRow(getActivity(), mRowsAdapter);
    }

    private ValueAnimator memoryAnimator;

    private void stopMemoryAnim() {
        if (memoryAnimator != null) {
            memoryAnimator.cancel();
            memoryAnimator = null;
        }
    }

    private boolean isMemoryAnimRunning() {
        return memoryAnimator != null && memoryAnimator.isRunning();
    }

    private void reStartMemoryAnim(float before, float after, final float total) {
        stopMemoryAnim();
        memoryAnimator = ValueAnimator.ofFloat(before, 0, after);
        memoryAnimator.setDuration(1500);
        memoryAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        memoryAnimator.setDuration(1500);
        memoryAnimator.addUpdateListener(animation -> {
            float current = (float) animation.getAnimatedValue();
            updateMemoryView(current, total);
        });
        memoryAnimator.start();
    }

    private Timer memoryTrackTimer;

    private void stopTimer() {
        if (memoryTrackTimer != null) {
            memoryTrackTimer.cancel();
            memoryTrackTimer = null;
        }
    }

    private void startTimer() {
        stopTimer();
        memoryTrackTimer = new Timer();
        memoryTrackTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //Log.i("startTimer", "" + Thread.currentThread());
                Activity context = getActivity();
                if (context == null) return;
                final ActivityManager.MemoryInfo memoryInfo = AppUtils.getMemoryInfo(context);
                context.runOnUiThread(() -> {
                    if (isMemoryAnimRunning()) return;
                    updateMemoryView(memoryInfo.availMem, memoryInfo.totalMem);
                });
            }
        }, 500, 1000);
    }

    private void updateMemoryView(float availMem, float totalMem) {
        int avail = (int) (availMem / 1024f / 1024f);
        int total = (int) (totalMem / 1024f / 1024f);
        String memory = String.format(Locale.getDefault(), "%dMB / %d MB", avail, total);
        tvMemory.setText(memory);
        pbMemory.setProgress(avail * 100 / total);
    }

    private void registerReceiver() {
        if (appListener == null) {
            appListener = new AppStateChangeListener(getContext(), new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    updateAppList(intent);
                }
            });
        }
        appListener.registerReceiver();

        if (!broadcastsRegisteredNetwork) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            getActivity().registerReceiver(networkReceiver, filter);
            broadcastsRegisteredNetwork = true;
        }

    }

    private void unregisterReceiver() {
        appListener.unregisterReceiver();
        if (broadcastsRegisteredNetwork) {
            getActivity().unregisterReceiver(networkReceiver);
            broadcastsRegisteredNetwork = false;
        }
    }

    private void updateAppList(Intent intent) {
        String packageName = null;
        String action = intent.getAction();

        if (intent.getData() != null) {
            packageName = intent.getData().getSchemeSpecificPart();
        }

        if (packageName == null || packageName.length() == 0) {
            return;
        }

        if (packageName.equals("com.android.provision")) {
            return;
        }

        Logger.i(TAG, "---update app:" + packageName + "\t" + intent.getAction());
        mAppRow.update();
    }

    private void killTvApp() {
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(PACKAGE_LIVE_TV);
    }

    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                switch (networkInfo.getType()) {
                    case ConnectivityManager.TYPE_ETHERNET:
                        imgNetWork.setImageResource(R.drawable.status_bar_ethernet);
                        break;
                    case ConnectivityManager.TYPE_WIFI:
                        imgNetWork.setImageResource(R.drawable.status_bar_wifi);
                        break;
                    default:
                        break;
                }
            } else {
                imgNetWork.setImageResource(R.drawable.status_bar_no_net);
            }
        }
    };

    @Override
    public void onTFCardMountState(boolean isMount) {
        imgTfCard.setVisibility(isMount ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onUsbDeviceMountState(boolean isMount) {
        imgUsbDevice.setVisibility(isMount ? View.VISIBLE : View.GONE);
    }

    private int mLoadCount = 0;
    @SuppressLint("HandlerLeak")
    private Handler mLoadHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (mLoadCount < 10) {
                        mLoadCount++;
                        tvHeaderListRow.signalUpdate();
                        mLoadHandler.sendEmptyMessageDelayed(MSG_LOAD_DATA, 1000);
                    }
                    break;
            }
        }
    };

    //====this is for live tv===========
    private class SourceStatusListener extends TvInputManager.TvInputCallback {
        public void onInputStateChanged(String inputId, int state) {
            Logger.d(TAG, "source :" + inputId + " connect:" + state);
            tvHeaderListRow.signalUpdate();
        }
    }

}