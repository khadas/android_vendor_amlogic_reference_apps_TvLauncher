package com.droidlogic.launcher.main;


import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.media.tv.TvInputManager;
import android.media.tv.TvView;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageManager;
import android.provider.BaseColumns;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.DiffCallback;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.tvprovider.media.tv.TvContractCompat;

import com.droidlogic.app.tv.DroidLogicTvUtils;
import com.droidlogic.launcher.R;
import com.droidlogic.launcher.api.ZeasnApiService;
import com.droidlogic.launcher.app.AppModel;
import com.droidlogic.launcher.app.AppMoreModel;
import com.droidlogic.launcher.app.AppRow;
import com.droidlogic.launcher.app.ShortcutModel;
import com.droidlogic.launcher.app.gallery.AppGalleryActivity;
import com.droidlogic.launcher.base.LeanbackActivity;
import com.droidlogic.launcher.function.FunctionModel;
import com.droidlogic.launcher.input.InputModel;
import com.droidlogic.launcher.input.InputSourceManager;
import com.droidlogic.launcher.leanback.listrow.TvHeaderListRow;
import com.droidlogic.launcher.leanback.listrow.TvRecommendListRow;
import com.droidlogic.launcher.leanback.presenter.MainPresenterSelector;
import com.droidlogic.launcher.leanback.presenter.OnItemClickListener;
import com.droidlogic.launcher.leanback.presenter.content.AppCardPresenter;
import com.droidlogic.launcher.leanback.presenter.content.SearchPreviewProgramPresenter;
import com.droidlogic.launcher.livetv.Channel;
import com.droidlogic.launcher.livetv.MediaModel;
import com.droidlogic.launcher.livetv.PreviewProgram;
import com.droidlogic.launcher.livetv.TvConfig;
import com.droidlogic.launcher.livetv.TvControl;
import com.droidlogic.launcher.livetv.TvRow;
import com.droidlogic.launcher.model.TvViewModel;
import com.droidlogic.launcher.model.ZeasnColumn;
import com.droidlogic.launcher.model.ZeasnColumnContent;
import com.droidlogic.launcher.recommend.AppPreviewProgramModel;
import com.droidlogic.launcher.recommend.RecChannelGroupLoader;
import com.droidlogic.launcher.recommend.RecommendChannelLoader;
import com.droidlogic.launcher.search.loader.PreviewProgramLoader;
import com.droidlogic.launcher.util.AppStateChangeListener;
import com.droidlogic.launcher.util.AppUtils;
import com.droidlogic.launcher.util.DensityTool;
import com.droidlogic.launcher.util.ImageTool;
import com.droidlogic.launcher.util.Logger;
import com.droidlogic.launcher.util.PackageUtil;
import com.droidlogic.launcher.util.StorageManagerUtil;
import com.droidlogic.launcher.util.Tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import me.jessyan.autosize.utils.AutoSizeUtils;
import me.jessyan.autosize.utils.ScreenUtils;
import me.jessyan.autosize.AutoSizeConfig;
import static android.content.Intent.URI_INTENT_SCHEME;
import static androidx.tvprovider.media.tv.ChannelLogoUtils.storeChannelLogo;
import static com.droidlogic.launcher.function.FunctionModel.PKG_NAME_MIRACAST;
import static com.droidlogic.launcher.function.FunctionModel.PKG_NAME_TVCAST;
import static com.droidlogic.launcher.function.FunctionModel.PKG_NAME_ZEASN_MARKET;

public class MainFragment extends Fragment implements StorageManagerUtil.Listener {

    private final String TAG = "MainFragment";

    private static final String PACKAGE_LIVE_TV = "com.droidlogic.android.tv";
    private static final String INTENT_MARKET = "market://";

    private final int invalidPosition = -1;
    private View defaultFocusView;
    private int tvHolderViewDefaultLocation = invalidPosition;
    private static final int TYPE_MARKET_CHANNEL = 1000;

    private static final int MSG_LOAD_DATA = 100;
    private static final int MSG_LOAD_APP = 200;

    private Disposable disposableColumn;

    private TimeDisplay mTimeDisplay;
    private AppRow mAppRow;

    //===this is for live tv===========
    private TvControl mTvControl;
    private InputSourceManager mInputSource;

    //page top area : TvHeader
    private TvHeaderListRow tvHeaderListRow;

    private VerticalGridView verticalGridView;
    private ArrayObjectAdapter mRowsAdapter;

    private AppStateChangeListener appListener;

    private boolean broadcastsRegisteredNetwork = false;

    private ImageView imgNetWork;

    private ImageView imgTfCard;

    private ImageView imgUsbDevice;

    private TextView tvMemory;

    private ProgressBar pbMemory;

    private TvView tvView;

    private ViewGroup tvViewParent;

    private TextView tvPrompt;

    private boolean needPreviewFeature;

    public MainFragment() {
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        needPreviewFeature = new TvConfig(getContext()).needPreviewFeature();
        fetchMarketData();
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
        tvHolderViewDefaultLocation = DensityTool.dp2px(59);
    }

    @Override
    public void onResume() {
        super.onResume();
        initTVControl();
        if (checkBootToTvApp()) {
            if (mTvControl != null) {
                mTvControl.resume();
            }
            return;
        }
        initLauncher();
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
        if (mTimeDisplay != null) {
            mTimeDisplay.unInit();
        }
        //===this is for live tv
        if (mTvControl != null) {
            mTvControl.pause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //===this is for live tv=======
        if (mTvControl != null) {
            mTvControl.stop();
        }
        //==============================
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposableColumn != null) {
            disposableColumn.dispose();
            disposableColumn = null;
        }
        unregisterReceiver();
        stopTimer();
        stopMemoryAnim();
        disposableRecLoad();
        if (mLoadHandler != null) {
            mLoadHandler.removeCallbacksAndMessages(null);
            mLoadHandler = null;
        }
        if (storageManagerUtil != null) {
            storageManagerUtil.unRegisterListener();
        }
    }

    private boolean mInitLauncher = false;

    private void initLauncher() {
        if (!mInitLauncher) {
            mInitLauncher = true;
            tvHeaderListRow = new TvHeaderListRow(new ArrayObjectAdapter());
            mInputSource = new InputSourceManager(getActivity(), new SourceStatusListener(), mLoadHandler);
            mLoadHandler.sendEmptyMessageDelayed(MSG_LOAD_DATA, 1000);
            mLoadHandler.sendEmptyMessageDelayed(MSG_LOAD_APP, 1000);

            initView(getView());
            prepareBackgroundManager();
            initStorage();

            DroidLogicTvUtils.setCurrentInputId(getContext(), mInputSource.getInputList().get(0).getId());
        }
    }

    private StorageManagerUtil storageManagerUtil;

    private void initStorage() {
        Context context = getContext();
        if (storageManagerUtil == null && context != null) {
            storageManagerUtil = new StorageManagerUtil(context.getSystemService(StorageManager.class), MainFragment.this);
            storageManagerUtil.registerListener();
        }
    }

    private Disposable disposableRec;

    private void disposableRecLoad() {
        if (disposableRec != null) {
            disposableRec.dispose();
            disposableRec = null;
        }
    }

    private void channelRowRecycleMark() {
        if (mRowsAdapter != null) {
            for (int i = 0; i < mRowsAdapter.size(); i++) {
                Object objRow = mRowsAdapter.get(i);
                if (objRow instanceof TvRecommendListRow) {
                    TvRecommendListRow tvRecommendListRow = (TvRecommendListRow) objRow;
                    tvRecommendListRow.setRecycleMark(true);
                }
            }
        }
    }

    private void channelRowRecycle() {
        if (mRowsAdapter != null) {
            for (int index = mRowsAdapter.size() - 1; index >= 0; index--) {
                Object objRow = mRowsAdapter.get(index);
                if (objRow instanceof TvRecommendListRow) {
                    TvRecommendListRow tvRecommendListRow = (TvRecommendListRow) objRow;
                    if (tvRecommendListRow.isRecycleMark()) {
                        mRowsAdapter.remove(objRow);
                    }
                }
            }
        }
    }

    private boolean channelFilter(String pkgName) {
        Context context = getContext();
        //market not install
        return context == null || (!context.getPackageName().equals(pkgName) || PackageUtil.isPkgInstalled(context, PKG_NAME_ZEASN_MARKET));
    }

    private void loadRecommend() {
        disposableRecLoad();
        channelRowRecycleMark();
        final Context context = getContext();
        if (context == null) return;
        disposableRec = Observable.create((ObservableOnSubscribe<AppPreviewProgramModel>) emitter -> {
            List<Channel> groupChannels = new RecChannelGroupLoader(getContext()).getDataList();
            if (groupChannels != null) {
                for (Channel groupByChannel : groupChannels) {
                    ApplicationInfo applicationInfo = PackageUtil.getApplicationInfoByPkgName(context, groupByChannel.getPackageName());
                    List<Channel> channels = new RecommendChannelLoader(getContext(), groupByChannel.getPackageName()).getDataList();
                    if (channels != null && applicationInfo != null && channelFilter(groupByChannel.getPackageName())) {
                        for (Channel channel : channels) {
                            List<PreviewProgram> programs = new PreviewProgramLoader(getContext(), "", channel.getPackageName(), channel.getId()).getDataList();
                            if (programs != null && programs.size() > 0) {
                                //String channelName = applicationInfo.loadLabel(getContext().getPackageManager()) + " (" + channel.getDisplayName() + ")";
                                AppPreviewProgramModel appPreviewProgramModel = new AppPreviewProgramModel(channel.getDisplayName(), channel, programs);
                                emitter.onNext(appPreviewProgramModel);
                                //break;
                            }
                        }
                    }
                }
            }
            emitter.onComplete();
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(appPreviewProgramModel -> {
            if (mRowsAdapter != null) {
                for (int i = 0; i < mRowsAdapter.size(); i++) {
                    Object objRow = mRowsAdapter.get(i);
                    if (objRow instanceof TvRecommendListRow) {
                        TvRecommendListRow recommendListRow = (TvRecommendListRow) objRow;
                        AppPreviewProgramModel rowPreviewModel = recommendListRow.getPreviewProgramModel();
                        if (rowPreviewModel != null && rowPreviewModel.getChannelId() == appPreviewProgramModel.getChannelId()) {
                            recommendListRow.setRecycleMark(false);
                            //already add , refresh data
                            ObjectAdapter objectAdapter = recommendListRow.getAdapter();
                            if (objectAdapter instanceof ArrayObjectAdapter) {
                                ArrayObjectAdapter arrayObjectAdapter = (ArrayObjectAdapter) objectAdapter;
                                arrayObjectAdapter.setItems(appPreviewProgramModel.getPreviewPrograms(), new DiffCallback<PreviewProgram>() {
                                    @Override
                                    public boolean areItemsTheSame(@NonNull PreviewProgram o1, @NonNull PreviewProgram o2) {
                                        return o1.getChannelId() == o2.getChannelId();
                                    }

                                    @Override
                                    public boolean areContentsTheSame(@NonNull PreviewProgram o1, @NonNull PreviewProgram o2) {
                                        return o1.getChannelId() == o2.getChannelId();
                                    }
                                });
                            }
                            return;
                        }
                    }
                }
                //new row
                ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new SearchPreviewProgramPresenter());
                arrayObjectAdapter.addAll(0, appPreviewProgramModel.getPreviewPrograms());
                ListRow listRow = new TvRecommendListRow(new HeaderItem(appPreviewProgramModel.getChannelName()), arrayObjectAdapter, appPreviewProgramModel);
                if (context.getPackageName().equals(appPreviewProgramModel.getChannelPkgName())) {
                    mRowsAdapter.add(listRow);
                } else {
                    mRowsAdapter.add(listRow);
                }
            }
        }, throwable -> Logger.i("loadRecChannel--throwable:" + throwable), () -> {
            Logger.i("loadRecChannel--onComplete");
            channelRowRecycle();
        });
    }

    //=======this is for live tv
    private void startTvApp(long id, String inputId, String type) {
        if (id == -1 && inputId == null) {
            mInputSource.switchInput(inputId, null);
            mInputSource.startInputAPP(inputId);
        } else {
            String name = mInputSource.getInputName(inputId);
            if (name == null) {
                name = mInputSource.getATVInputName();
            }
            if (!type.equals("TYPE_PAL") && !type.equals("TYPE_NTSC") && !type.equals("TYPE_SECAM")) {
                name = mInputSource.getDTVInputName();
            }
            if (name.equals(mInputSource.getATVInputName()) && !mInputSource.isAtvSearch()) {
                killTvApp(); // for atv no channel
            }
            mInputSource.setSearchType(name);
            if (mTvControl != null) {
                mTvControl.launchTvApp(id);
            }
        }
    }

    private void startPlayInputSource(String inputId, String name) {
        if (name.equals(mInputSource.getATVInputName()) && !mInputSource.isAtvSearch()) {
            killTvApp(); // for atv no channel
        }
        if (mTvControl != null) {
            mTvControl.releasePlayingTv();
        }
        mInputSource.switchInput(inputId, name);
        if (mTvControl != null) {
            mTvControl.play(inputId);
        }
    }

    private void initTime() {
        if (mTimeDisplay == null) {
            TextView view = (TextView) getView().findViewById(R.id.tv_date);
            mTimeDisplay = new TimeDisplay(getActivity(), view);
        }
        mTimeDisplay.init();
        mTimeDisplay.update();
    }

    private void prepareBackgroundManager() {
        View view = getView();
        if (view == null) return;
        AutoSizeConfig.getInstance().setExcludeFontScale(true);
        int level = (int) (1.0f * AutoSizeUtils.dp2px(view.getContext(), 70) / ScreenUtils.getScreenSize(getContext())[1] * 10000);
        ImageView statusBg = (ImageView) view.findViewById(R.id.status_bg);
        ClipDrawable drawable = (ClipDrawable) statusBg.getBackground();
        drawable.setLevel(level);
    }

    abstract static class TvViewRunnable implements Runnable {
        protected boolean resize = false;
        protected int y = 0;

        public void updateY(int y) {
            this.y = y + DensityTool.dp2px(2);
        }
    }


    private View getTvHolderView() {
        View view = getView();
        if (view != null) {
            return view.findViewById(R.id.border_view_holder);
        }
        return null;
    }

    private final TvViewRunnable resizeTvView = new TvViewRunnable() {
        @Override
        public void run() {
            if (!needPreviewFeature || tvViewParent == null || getContext() == null) return;
            int width = AutoSizeUtils.dp2px(getContext(), 528);
            int height = AutoSizeUtils.dp2px(getContext(), 297);
            int topMargin = AutoSizeUtils.dp2px(getContext(), 80);
            int smallWindowsHeight = AutoSizeUtils.dp2px(getContext(), 160);
            int smallWindowsWidth = smallWindowsHeight * 16 / 9;
            int startMargin = AutoSizeUtils.dp2px(getContext(), 62);
            //int startMarginOrg = AutoSizeUtils.dp2px(getContext(), 62);
            int smallWindowStartMargin = AutoSizeUtils.dp2px(getContext(), 56);
            FrameLayout.LayoutParams pms = (FrameLayout.LayoutParams) tvViewParent.getLayoutParams();
            int tvPromptHeight = tvPrompt.getHeight();
            resize = (y > tvPromptHeight) && (tvPromptHeight > 0);
            Log.i(TAG, "yScroll:run:" + resize + "-width:" + width + "-height:" + height + "-tvPromptHeight:" + tvPromptHeight + "--topMargin:" + topMargin + "--scrollY:" + y);
            if (resize) {
                pms.width = smallWindowsWidth;
                pms.height = smallWindowsHeight;
                pms.topMargin = topMargin;
                pms.leftMargin = smallWindowStartMargin;
            } else {
                pms.width = width;
                pms.height = height;
                pms.leftMargin = startMargin;
                if (y > 0) {
                    int margin = topMargin - y;
                    //pms.leftMargin = startMargin;
                    pms.topMargin = margin;
                }
                pms.topMargin = y;
            }
            tvViewParent.setLayoutParams(pms);
        }
    };

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
        verticalGridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            int yScroll = 0;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                yScroll += dy;
                //resizeTvView.scrollY = yScroll;
                //Log.i("onScrolled", "onScrolled:" + yScroll);
                resetTvWindowLocation();
            }
        });
        verticalGridView.setVerticalSpacing((int) getResources().getDimension(R.dimen.main_page_vtl_space));
        mRowsAdapter = new ArrayObjectAdapter(new MainPresenterSelector(mInputSource, new OnItemClickListener() {
            @Override
            public void onPresenterItemClick(View view, Object item) {
                Logger.i("onPresenterItemClick:"+item);
                if (item instanceof MediaModel) {
                    MediaModel model = (MediaModel) item;
                    startTvApp(model.getId(), model.getInputId(), model.getType());
                } else if (item instanceof AppModel) {
                    AppModel appBean = (AppModel) item;
                    appBean.onClickModel(view);
                } else if (item instanceof AppMoreModel) {
                    startActivity(new Intent(getContext(), AppGalleryActivity.class));
                } else if (item instanceof ShortcutModel) {
                    ShortcutModel shortcutModel = (ShortcutModel) item;
                    Intent intent = intent = new Intent();
                    switch (shortcutModel.getIntent()) {
                        case NETWORK:
                            intent.setAction(Settings.ACTION_WIFI_SETTINGS);
                            break;
                        case LANGUAGE:
                            intent.setAction(Settings.ACTION_LOCALE_SETTINGS);
                            break;
                        case TIME:
                            intent.setAction(Settings.ACTION_DATE_SETTINGS);
                            break;
                        default:
                            return;
                    }
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (item instanceof PreviewProgram) {
                    startPreviewProgram((PreviewProgram) item);
                } else if (item instanceof FunctionModel) {
                    FunctionModel model = (FunctionModel) item;
                    Intent intent = model.getIntent();
                    if (intent == null && !TextUtils.isEmpty(model.getPackageName()) && getActivity() != null) {
                        String packageName = model.getPackageName();
                        PackageManager packageManager = getActivity().getPackageManager();
                        intent = packageManager.getLaunchIntentForPackage(packageName);
                        if (PKG_NAME_TVCAST.equals(packageName) && intent == null) {
                            intent = packageManager.getLaunchIntentForPackage(PKG_NAME_MIRACAST);
                        }
                        model.setIntent(intent);
                    }
                    if (intent != null) {
                        try {
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (item instanceof InputModel) {
                    InputModel model = (InputModel) item;
                    startPlayInputSource(model.getId(), model.getName());
                } else if (item instanceof TvViewModel) {
                    if (mTvControl != null) {
                        mTvControl.releasePlayingTv();
                    }
                    mInputSource.startInputAPP(null);
                }
            }
        }));

        //add function row
        addTvHeaderView();
        addAppRow();
        addShortcutRow();

        verticalGridView.setAdapter(new ItemBridgeAdapter(mRowsAdapter));
        verticalGridView.post(new Runnable() {
            @Override
            public void run() {
                //default focus view
                defaultFocusView = verticalGridView.findViewById(R.id.border_view_holder);
                if (defaultFocusView != null) {
                    defaultFocusView.requestFocus();
                    defaultFocusView.setNextFocusUpId(R.id.fun_content_search);

                    defaultFocusView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (defaultFocusView.hasFocus()) {
                                final int[] location = new int[2];
                                defaultFocusView.getLocationOnScreen(location);
                                Log.d("setOnFocusChangeListener", String.valueOf(location[1]));
                                Log.i(TAG, Arrays.toString(location));
                                if (location[1] > 0) {
                                    resetTvWindowLocation();
                                } else if (defaultFocusView != null) {
                                    defaultFocusView.postDelayed(this, 100);
                                }
                            }

                        }
                    }, 100);
                    defaultFocusView.setOnFocusChangeListener((v, hasFocus) -> {
                        if (hasFocus) {

                        }
                    });
                }
                //initial tvControl
                initTVControl();
            }
        });
        mRowsAdapter.registerObserver(new ObjectAdapter.DataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                resetTvWindowLocation();
            }
        });
    }

    private void fetchMarketData() {
        String mac = Tools.getMacAddress(Tools.ETHERNET0);
        if (TextUtils.isEmpty(mac)) return;
        //mac = "02:ad:38:01:42:13";
        disposableColumn = ZeasnApiService.INSTANCE.fetColumnContent(mac)
                .map(pairs -> {
                    dealMarketChannelData(pairs);
                    return true;
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bool -> Logger.i("fetchMarketData", "subscribe"), throwable -> loadRecommend(), this::loadRecommend);
    }

    private void dealMarketChannelData(List<Pair<ZeasnColumn.DataBean.ChildrenBean, ZeasnColumnContent>> pairs) {
        eraseMarketChannelData();
        for (Pair<ZeasnColumn.DataBean.ChildrenBean, ZeasnColumnContent> pair : pairs) {
            ZeasnColumnContent columnContent = pair.second;
            List<ZeasnColumnContent.DataBean> contents = columnContent.getData();
            if (contents != null) {
                for (ZeasnColumnContent.DataBean content : contents) {
                    ZeasnColumnContent.DataBean.ContentBean contentBean = content.getContent();
                    if (contentBean != null) {
                        createMarketChannel(contentBean.getName(), contentBean.getDataList());
                    }
                }
            }
        }
    }

    private void eraseMarketChannelData() {
        Activity context = getActivity();
        if (context == null) return;
        String where = TvContractCompat.Channels.COLUMN_PACKAGE_NAME + " = ? and " + TvContractCompat.Channels.COLUMN_APP_LINK_INTENT_URI + " = ?";
        String[] selectionArgs = new String[]{context.getPackageName(), INTENT_MARKET};
        Cursor cursor = context.getContentResolver().query(TvContractCompat.Channels.CONTENT_URI, new String[]{BaseColumns._ID}, where, selectionArgs, null);
        if (cursor == null || cursor.getCount() == 0) {
            return;
        }
        List<Long> ids = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                int index = 0;
                if (!cursor.isNull(index)) {
                    ids.add(cursor.getLong(index));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        if (ids.size() > 0) {
            context.getContentResolver().delete(TvContractCompat.Channels.CONTENT_URI, where, selectionArgs);
        }
        for (Long id : ids) {
            context.getContentResolver().delete(TvContractCompat.PreviewPrograms.CONTENT_URI, "channel_id = ?", new String[]{String.valueOf(id)});
        }
    }

    private void createMarketChannel(String name, List<ZeasnColumnContent.DataBean.ContentBean.DataListBean> dataList) {
        Activity activity = getActivity();
        if (dataList == null || dataList.size() == 0 || activity == null) return;
        Channel.Builder builder = new Channel.Builder();
        builder.setType(TvContractCompat.Channels.TYPE_PREVIEW)
                .setDisplayName(name)
                .setOriginalNetworkId(0)
                .setBrowsable(true)
                .setSearchable(true)
                .setAppLinkIntentUri(INTENT_MARKET);
        Uri channelUri = activity.getContentResolver().insert(
                TvContractCompat.Channels.CONTENT_URI, builder.build().toContentValues());
        long channelId = ContentUris.parseId(channelUri);
        Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.icon_market);
        if (drawable != null) {
            storeChannelLogo(activity, channelId, ImageTool.drawableToBitmap(drawable));
        }
        TvContractCompat.requestChannelBrowsable(getContext(), channelId);
        addPrograms(channelId, dataList);
    }

    private void addPrograms(long channelId, List<ZeasnColumnContent.DataBean.ContentBean.DataListBean> dataList) {
        if (dataList == null) return;
        for (ZeasnColumnContent.DataBean.ContentBean.DataListBean content : dataList) {
            androidx.tvprovider.media.tv.PreviewProgram.Builder builder = new androidx.tvprovider.media.tv.PreviewProgram.Builder();
            builder.setChannelId(channelId)
                    .setType(TYPE_MARKET_CHANNEL)
                    .setDescription(content.getBriefDesc())
                    .setInternalProviderId(content.getPkg())
                    .setIntentUri(Uri.parse(INTENT_MARKET))
                    .setPosterArtUri(Uri.parse(content.getIcon()))
                    .setTitle(content.getName());
            Uri programUri = getContext().getContentResolver().insert(TvContractCompat.PreviewPrograms.CONTENT_URI,
                    builder.build().toContentValues());
        }
    }

    private void enableMainLayout() {
        FrameLayout black = (FrameLayout) getView().findViewById(R.id.layout_black);
        if (black != null) {
            black.setVisibility(View.GONE);
        }
        FrameLayout main = (FrameLayout) getView().findViewById(R.id.layout_main);
        if (main != null) {
            main.setVisibility(View.VISIBLE);
        }
    }

    //check if boot to tvapp when power on
    private boolean checkBootToTvApp() {
        if (!new TvConfig(getActivity()).checkNeedStartTvApp(false, false)) {
            Log.d(TAG, "start launch");
            enableMainLayout();
            return false;
        } else {
            Log.d(TAG, "start tv");
            return true;
        }
    }

    private void initTVControl() {
        //initial tvControl
        if (mTvControl == null) {
            tvViewParent = (ViewGroup) getActivity().findViewById(R.id.tv_view_parent);
            if (needPreviewFeature) {
                tvView = (TvView) getActivity().findViewById(R.id.tv_view);
                tvViewParent.setVisibility(View.VISIBLE);
            } else {
                tvViewParent.setVisibility(View.GONE);
            }
            tvPrompt = (TextView) getActivity().findViewById(R.id.tx_tv_prompt);
            mTvControl = new TvControl(getActivity(), tvView, tvPrompt);
        }
    }

    private void resetTvWindowLocation() {
        if (!needPreviewFeature) return;
        defaultFocusView = getTvHolderView();
        int holderDefaultY = tvHolderViewDefaultLocation;
        if (defaultFocusView != null && holderDefaultY != invalidPosition) {
            int[] location = new int[2];
            defaultFocusView.getLocationInWindow(location);
            resizeTvView.updateY(location[1]);
            Log.i(TAG, tvHolderViewDefaultLocation + "--y:" + location[1]);
            //Log.i("onScrolled", "resizeTvView.1scrollY:" + resizeTvView.scrollY);
            resizeTvView.run();
        } else if (getContext() != null && defaultFocusView == null && verticalGridView.getSelectedPosition() > 1) {
            resizeTvView.y = Integer.MAX_VALUE;
            //Log.i("onScrolled", "resizeTvView.2scrollY:" + resizeTvView.scrollY);
            resizeTvView.run();
        }
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

    private void addShortcutRow() {
        ArrayObjectAdapter settingRowAdapter = new ArrayObjectAdapter(new AppCardPresenter());
        settingRowAdapter.add(new ShortcutModel(getString(R.string.fun_shortcut_network), R.drawable.icon_shortcut_network, ShortcutModel.INTENT.NETWORK));
        settingRowAdapter.add(new ShortcutModel(getString(R.string.fun_shortcut_language), R.drawable.icon_shortcut_language, ShortcutModel.INTENT.LANGUAGE));
        settingRowAdapter.add(new ShortcutModel(getString(R.string.fun_shortcut_time), R.drawable.icon_shortcut_time, ShortcutModel.INTENT.TIME));
        String headerName = getResources().getString(R.string.app_header_settings);
        HeaderItem header = new HeaderItem(headerName);
        mRowsAdapter.add(new ListRow(header, settingRowAdapter));
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

    private void startRecommendApp(PreviewProgram program) {
        PackageManager manager = getActivity().getPackageManager();
        String packageName = program.getmProviderId();
        Intent intent = manager.getLeanbackLaunchIntentForPackage(packageName);
        if (intent == null) {
            intent = manager.getLaunchIntentForPackage(packageName);
        }
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            //no package, start google play store to install it
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.android.vending");
            intent.setData(Uri.parse(program.getmProviderData()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void startPreviewProgram(PreviewProgram program) {
        try {
            String intentUri = program.getIntentUri();
            if (TextUtils.isEmpty(intentUri)) {
                startRecommendApp(program);
            } else if (INTENT_MARKET.equals(intentUri)) {
                PackageUtil.clickRecommendApp(getContext(), program.getmProviderId(), FunctionModel.PKG_NAME_ZEASN_MARKET);
            } else {
                Intent intent = Intent.parseUri(intentUri, URI_INTENT_SCHEME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (tvMemory != null) {
            tvMemory.setText(memory);
        }
        if (pbMemory != null) {
            pbMemory.setProgress(avail * 100 / total);
        }
    }

    private void registerReceiver() {
        if (appListener == null) {
            appListener = new AppStateChangeListener(getContext(), new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    updateAppList(intent);
                    loadRecommend();
                }
            });
        }
        appListener.registerReceiver();

        if (!broadcastsRegisteredNetwork) {
            IntentFilter filter = new IntentFilter();
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
            if (imgNetWork == null) {
                return;
            }
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
        if (imgTfCard != null) {
            imgTfCard.setVisibility(isMount ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onUsbDeviceMountState(boolean isMount) {
        if (imgUsbDevice != null) {
            imgUsbDevice.setVisibility(isMount ? View.VISIBLE : View.GONE);
        }
    }

    private int mLoadCount = 0;
    private int mAppLoadCount = 0;

    @SuppressLint("HandlerLeak")
    private Handler mLoadHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (mLoadCount < 10) {
                        mLoadCount++;
                        tvHeaderListRow.signalUpdate();
                        mLoadHandler.sendEmptyMessageDelayed(MSG_LOAD_DATA, 1000);
                    }
                    break;
                case MSG_LOAD_APP:
                    if (mAppRow == null || mAppRow.getAdapterSize() <= 2) {
                        if (mAppRow != null) {
                            mAppRow.update();
                        }
                        Logger.i("MSG_LOAD_APP:" + mAppLoadCount + "--mAppRow:" + mAppRow);
                        if (mAppLoadCount++ < 10) {
                            mLoadHandler.sendEmptyMessageDelayed(MSG_LOAD_APP, 1000);
                        }
                    }
                    break;
            }
        }
    };

    //====this is for live tv===========
    private class SourceStatusListener extends TvInputManager.TvInputCallback {
        public void onInputStateChanged(String inputId, int state) {
            Logger.d(TAG, "source :" + inputId + " connect:" + state);
            if (tvHeaderListRow != null) {
                tvHeaderListRow.signalUpdate();
            }
        }
    }

}
