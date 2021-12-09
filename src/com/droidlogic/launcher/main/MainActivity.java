
package com.droidlogic.launcher.main;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.KeyEvent;
import android.widget.TextView;

import com.droidlogic.app.tv.TvControlManager;
import com.droidlogic.launcher.R;
import com.droidlogic.launcher.app.AppModel;
import com.droidlogic.launcher.app.AppRow;
import com.droidlogic.launcher.function.FunctionRow;
import com.droidlogic.launcher.input.InputModel;
import com.droidlogic.launcher.input.InputRow;
import com.droidlogic.launcher.input.InputSourceManager;
import com.droidlogic.launcher.livetv.MediaModel;
import com.droidlogic.launcher.function.FunctionModel;
import com.droidlogic.launcher.livetv.TvControl;
import com.droidlogic.launcher.livetv.TvRow;
import com.droidlogic.launcher.recommend.RecommendRow;

public class MainActivity extends Activity {
    private static final String TAG = "MainLaunch";
    private static final String TV_USER_SETUP_COMPLETE = "tv_user_setup_complete";
    private static final String PACKAGE_LIVE_TV        = "com.droidlogic.android.tv";

    private final int MSG_LOAD_DATA = 100;

    private BrowseFragment mBrowseFragment;
    private ArrayObjectAdapter mRowsAdapter;
    private BackgroundManager mBackgroundManager;
    private DisplayMetrics mMetrics;
    private Context mContext;
    private TimeDisplay mTimeDisplay;
    private boolean mActivityResumed = false;
    private boolean mBroadcastsRegistered = false;

    private AppRow       mAppRow;
    private RecommendRow mRecommendRow;
    private FunctionRow  mFunctionRow;

    //===this is for live tv===========
    private TvRow     mTvRow;
    private InputRow  mInputRow;
    private TvControl mTvControl;
    private InputSourceManager mInputSource;
    //================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        skipUserSetup();

        mContext = this;
        mBrowseFragment = (BrowseFragment) getFragmentManager().findFragmentById(R.id.browse_fragment);
        mBrowseFragment.setHeadersState(BrowseFragment.HEADERS_DISABLED);

        //===this is for live tv=================
        mTvControl   = new TvControl(this);
        mInputSource = new InputSourceManager(this, new SourceConnectListener());
        //=======================================

        prepareBackgroundManager();
        buildRowsAdapter();
        mLoadHandler.sendEmptyMessageDelayed(MSG_LOAD_DATA, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mActivityResumed)
            return;

        initTime();
        registerAppReceiver();

        //===this is for live tv
        updateVideo();
        updateInput();
        mTvControl.resume();
        //=====================

        mActivityResumed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTvControl.pause();   //===this is for live tv
        mActivityResumed = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTimeDisplay.unInit();
        unregisterAppReceiver();

        //===this is for live tv=======
        mTvControl.stop();
        //==============================
    }

    //=======this is for live tv
    private void startTvApp(long id, String inputId, String type) {
        if (id == -1 && inputId == null) {
            mInputSource.switchInput(inputId, null);
            mInputSource.startInputAPP(inputId);
        } else {
            String name = mInputSource.getInputName(inputId);
            if (name == null){
                name = "ATV";
            }
            if (!type.equals("TYPE_PAL") && !type.equals("TYPE_NTSC") && !type.equals("TYPE_SECAM")) {
                name = "DTV";
            }
            if (name.equals("ATV")&& !mInputSource.isAtvSearch()) {
                killTvApp(); // for atv no channel
            }
            mInputSource.setSearchType(name);
            mTvControl.launchTvApp(id);
        }
    }

    private void startInputSourceApp(String inputId, String name){
        if (name.equals("ATV") && !mInputSource.isAtvSearch()) {
            killTvApp(); // for atv no channel
        }
        mTvControl.releasePlayingTv();
        mInputSource.switchInput(inputId, name);
        mInputSource.startInputAPP(inputId);
    }
    //==========================


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //we have not user setup APK, force skip it , or we can't use home key
    private void skipUserSetup() {
        if (Settings.Secure.getInt(getContentResolver(), TV_USER_SETUP_COMPLETE, 0) == 0) {
            Log.d(TAG, "force skip user setup");
            Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
            Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);
            Settings.Secure.putInt(getContentResolver(), TV_USER_SETUP_COMPLETE, 1);
        }
    }

    private void initTime() {
        TextView view = (TextView) findViewById(R.id.tx_date);
        mTimeDisplay = new TimeDisplay(this, view);
        mTimeDisplay.init();
        mTimeDisplay.update();
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(this);
        mBackgroundManager.attach(this.getWindow());
        mMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        mBackgroundManager.setThemeDrawableResourceId(R.drawable.bg1);
    }

    private void buildRowsAdapter() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        addVideoRow();
        addAppRow();
        addInputRow();
        //addRecommendRow();
        addFunctionRow();

        mBrowseFragment.setAdapter(mRowsAdapter);
        mBrowseFragment.setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                if (item instanceof MediaModel) {
                    MediaModel model = (MediaModel) item;
                    startTvApp(model.getId(), model.getInputId(), model.getType());
                } else if (item instanceof AppModel) {
                    AppModel appBean = (AppModel) item;
                    Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(appBean.getPackageName());
                    if (launchIntent != null) {
                        mContext.startActivity(launchIntent);
                    }
                } else if (item instanceof FunctionModel) {
                    FunctionModel model = (FunctionModel) item;
                    Intent intent = model.getIntent();
                    if (intent != null) {
                        startActivity(intent);
                    }
                } else if (item instanceof InputModel) {
                    InputModel model = (InputModel) item;
                    //====this is for live tv===========
                    startInputSourceApp(model.getId(), model.getName());
                    //===================================
                }
            }
        });

        mBrowseFragment.setOnItemViewSelectedListener(new OnItemViewSelectedListener() {
            @Override
            public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                if (item instanceof MediaModel) {
                    //MediaModel mediaModel = (MediaModel) item;
                }
            }
        });
    }

    private void addVideoRow() {
        String headerName = getResources().getString(R.string.app_header_video_name);
        mTvRow = new TvRow(this, headerName, mRowsAdapter);
    }

    private void addAppRow() {
        String headerName = getResources().getString(R.string.app_header_app_name);
        mAppRow = new AppRow(this, headerName, mRowsAdapter);
    }

    private void addInputRow() {
        String headerName = getResources().getString(R.string.app_header_input_name);
        mInputRow = new InputRow(this, headerName, mRowsAdapter, mInputSource);
    }

    private void addRecommendRow(){
        mRecommendRow = new RecommendRow(this, mRowsAdapter);
    }


    private void addFunctionRow() {
        String headerName = getResources().getString(R.string.app_header_function_name);
        mFunctionRow = new FunctionRow (this, headerName, mRowsAdapter);
    }

    private void registerAppReceiver() {
        if (!mBroadcastsRegistered) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
            filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
            filter.addDataScheme("package");
            registerReceiver(appReceiver, filter);
            mBroadcastsRegistered = true;
        }
    }

    private void unregisterAppReceiver() {
        if (mBroadcastsRegistered) {
            unregisterReceiver(appReceiver);
            mBroadcastsRegistered = false;
        }
    }

    private void updateVideo() {
        mTvRow.update();
    }

    private void updateInput() {
        mInputRow.update();
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

    private void killTvApp(){
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(PACKAGE_LIVE_TV);
    }


    private BroadcastReceiver appReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            //Log.d(TAG, "appReceiver receive " + action);
            if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
                    || Intent.ACTION_PACKAGE_REMOVED.equals(action)
                    || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                updateAppList(intent);
            }
        }
    };

    private int mLoadCount = 0;
    private Handler mLoadHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (mLoadCount < 10) {
                        mLoadCount++;
                        updateVideo();
                        updateInput();
                        mLoadHandler.sendEmptyMessageDelayed(MSG_LOAD_DATA, 1000);
                    }
                    break;
            }
        }
    };

    //====this is for live tv===========
    private class SourceConnectListener implements TvControlManager.StatusSourceConnectListener {
        public void onSourceConnectChange(TvControlManager.SourceInput source, int connectionState) {
            //Log.d(TAG, "source " + source.name() + " connect:" + connectionState);
            updateInput();
        }
    }
    //==================================
}
