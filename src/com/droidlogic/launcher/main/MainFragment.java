package com.droidlogic.launcher.main;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.tv.TvView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.VerticalGridView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droidlogic.app.tv.TvControlManager;
import com.droidlogic.launcher.R;
import com.droidlogic.launcher.app.AppModel;
import com.droidlogic.launcher.app.AppRow;
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

import java.util.List;

public class MainFragment extends Fragment {

    private final String TAG=MainFragment.class.getName();

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

    private boolean mBroadcastsRegistered = false;

    public MainFragment() {
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //===this is for live tv=================
        mInputSource = new InputSourceManager(getActivity(), new SourceConnectListener());
        //=======================================
        mLoadHandler.sendEmptyMessageDelayed(MSG_LOAD_DATA, 1000);
        prepareBackgroundManager();
        initView(getView());

        test();
    }

    @Override
    public void onResume() {
        super.onResume();
        initTime();
        registerAppReceiver();
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
        unregisterAppReceiver();
        //===this is for live tv=======
        if (mTvControl != null) {
            mTvControl.stop();
        }
        //==============================
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadHandler != null) {
            mLoadHandler.removeCallbacksAndMessages(null);
            mLoadHandler = null;
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

    private void initTime() {
        TextView view = (TextView) getView().findViewById(R.id.tx_date);
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
                } else if (item instanceof FunctionModel) {
                    FunctionModel model = (FunctionModel) item;
                    Intent intent = model.getIntent();
                    if (intent != null) {
                        startActivity(intent);
                    }
                } else if (item instanceof InputModel) {
                    InputModel model = (InputModel) item;
                    startInputSourceApp(model.getId(), model.getName());
                } else if (item instanceof TvViewModel) {
                    ((TvViewModel) item).lunch(getContext());
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

    private void registerAppReceiver() {
        if (!mBroadcastsRegistered) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
            filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
            filter.addDataScheme("package");
            getActivity().registerReceiver(appReceiver, filter);
            mBroadcastsRegistered = true;
        }
    }

    private void unregisterAppReceiver() {
        if (mBroadcastsRegistered) {
            getActivity().unregisterReceiver(appReceiver);
            mBroadcastsRegistered = false;
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

        Log.d(TAG, "---update app:" + packageName);
        if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            mAppRow.remove(packageName);
        } else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            mAppRow.add(packageName);
        } else if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
            mAppRow.update(packageName);
        }
    }

    private void killTvApp() {
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(PACKAGE_LIVE_TV);
    }

    private final BroadcastReceiver appReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "appReceiver receive " + action);
            if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
                    || Intent.ACTION_PACKAGE_REMOVED.equals(action)
                    || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                updateAppList(intent);
            }
        }
    };

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
    private class SourceConnectListener implements TvControlManager.StatusSourceConnectListener {
        public void onSourceConnectChange(TvControlManager.SourceInput source, int connectionState) {
            Log.d(TAG, "source " + source.name() + " connect:" + connectionState);
            tvHeaderListRow.signalUpdate();
        }
    }

    private Handler handler=new Handler();

    private void test(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dump();
                test();
            }
        },5000);
    }

    private void dump() {
        ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList = am.getRunningTasks(1);
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfoList) {
            log("id: " + runningTaskInfo.id);
            log("description: " + runningTaskInfo.description);
            log("number of activities: " + runningTaskInfo.numActivities);
            log("topActivity: " + runningTaskInfo.topActivity);
            log("baseActivity: " + runningTaskInfo.baseActivity.toString());
        }
    }

    private void log(String msg) {
        Log.i("MAIN_ACTIVITY", msg);
    }

}