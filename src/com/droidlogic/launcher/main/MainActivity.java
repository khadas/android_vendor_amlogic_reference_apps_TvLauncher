
package com.droidlogic.launcher.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
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
import android.widget.Toast;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.app.AppCardPresenter;
import com.droidlogic.launcher.app.AppDataManage;
import com.droidlogic.launcher.app.AppModel;
import com.droidlogic.launcher.input.InputCardPresenter;
import com.droidlogic.launcher.input.InputModel;
import com.droidlogic.launcher.input.InputSourceManager;
import com.droidlogic.launcher.livetv.ChannelObserver;
import com.droidlogic.launcher.livetv.TvCardPresenter;
import com.droidlogic.launcher.livetv.MediaModel;
import com.droidlogic.launcher.function.FunctionCardPresenter;
import com.droidlogic.launcher.function.FunctionModel;
import com.droidlogic.launcher.livetv.TvControl;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private String TAG = "MainLaunch";

    private BrowseFragment mBrowseFragment;
    private ArrayObjectAdapter rowsAdapter;
    private BackgroundManager mBackgroundManager;
    private DisplayMetrics mMetrics;
    private Context mContext;
    private TimeDisplay mTimeDisplay;

    private ArrayObjectAdapter mAppListRowAdapter = new ArrayObjectAdapter(new AppCardPresenter());

    //===this is for live tv===========
    private TvControl     mTvControl;
    private InputSourceManager mInputSource;
    private ArrayObjectAdapter mTvListRowAdapter = new ArrayObjectAdapter(new TvCardPresenter());
    private ArrayObjectAdapter mInputListRowAdapter  = new ArrayObjectAdapter(new InputCardPresenter());
    private ChannelObserver mChannelObserver;
    //================================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mBrowseFragment = (BrowseFragment) getFragmentManager().findFragmentById(R.id.browse_fragment);
        mBrowseFragment.setHeadersState(BrowseFragment.HEADERS_DISABLED);

        //===this is for live tv=================
        mTvControl   = new TvControl(this);
        mInputSource = new InputSourceManager(this);
        mChannelObserver = new ChannelObserver();
        getContentResolver().registerContentObserver(TvContract.Channels.CONTENT_URI, true, mChannelObserver);
        //=======================================

        prepareBackgroundManager();
        buildRowsAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTime();
        registerAppReceiver();
        mLoadHandler.sendEmptyMessageDelayed(100, 1000);
        Log.d(TAG, "--onresume");
        //===this is for live tv
        updateVideo();
        mTvControl.resume();
        //=====================
    }


    @Override
    protected void onPause() {
        super.onPause();
        mTvControl.pause();   //===this is for live tv
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTimeDisplay.unInit();
        unregisterAppReceiver();

        //===this is for live tv=======
        mTvControl.stop();
        getContentResolver().unregisterContentObserver(mChannelObserver);
        //==============================
    }

    private void startTvApp(long id){
        mTvControl.launchTvApp(id);  //=======this is for live tv
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void  initTime(){
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
        mBackgroundManager.setThemeDrawableResourceId (R.drawable.bg1);
    }

    private void buildRowsAdapter() {
        rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        addVideoRow();
        addAppRow();
        addInputRow();
        addFunctionRow();

        mBrowseFragment.setAdapter(rowsAdapter);
        mBrowseFragment.setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                if (item instanceof MediaModel) {
                    MediaModel model = (MediaModel) item;
                    startTvApp(model.getId());
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
                    mTvControl.releasePlayingTv();
                    mInputSource.switchInput(model.getId(), model.getName());
                    //===================================
                }
            }
        });

        mBrowseFragment.setOnItemViewSelectedListener(new OnItemViewSelectedListener() {
            @Override
            public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                if (item instanceof MediaModel) {
                    //MediaModel mediaModel = (MediaModel) item;
                    //Log.d(TAG, "channel id: " + mediaModel.getId());
                    //mTvControl.play(mediaModel.getId());
                }
            }
        });
    }

    private void updateVideo(){
        int i;
        boolean update = false;
        List<MediaModel> list = MediaModel.getDTVModels(mContext);
        Log.d(TAG, "video :" + list.size());

        int curSize = mTvListRowAdapter.size();
        int newSize = list.size();
        if (curSize != newSize){
            mTvListRowAdapter.clear();
            for (i = 0; i < newSize; i++) {
                MediaModel model2 = (MediaModel) list.get(i);
                mTvListRowAdapter.add(model2);
            }
            update = true;
        }
        else {
            for (i = 0; i < newSize; i++) {
                MediaModel model1 = (MediaModel) mTvListRowAdapter.get(i);
                MediaModel model2 = (MediaModel) list.get(i);
                if (model1.getId() != model2.getId()) {
                    mTvListRowAdapter.replace(i, model2);
                    update = true;
                }
            }
        }
    }

    private void updateInput(){
        int i;

        List<InputModel> list = InputModel.getInputList(mInputSource);
        Log.d(TAG, "input :" + list.size());

        int curSize = mInputListRowAdapter.size();
        int newSize = list.size();
        if (curSize != newSize){
            mInputListRowAdapter.clear();
            for (i = 0; i < newSize; i++) {
                InputModel model2 = (InputModel) list.get(i);
                mInputListRowAdapter.add(model2);
            }
        }
        else {
            for (i = 0; i < newSize; i++) {
                InputModel model1 = (InputModel) mInputListRowAdapter.get(i);
                InputModel model2 = (InputModel) list.get(i);
                if (model1.getId() != model2.getId()) {
                    mInputListRowAdapter.replace(i, model2);
                }
            }
        }
    }


    private void addVideoRow() {
        String headerName = getResources().getString(R.string.app_header_video_name);
        mTvListRowAdapter = new ArrayObjectAdapter(new TvCardPresenter());
        for (MediaModel mediaModel : MediaModel.getDTVModels(mContext)) {
            mTvListRowAdapter.add(mediaModel);
        }
        HeaderItem header = new HeaderItem(0, headerName);
        rowsAdapter.add(new ListRow(header, mTvListRowAdapter));
    }

    private void loadAppData(){
        ArrayList<AppModel> appDataList = new AppDataManage(mContext).getAppsList();
        int cardCount = appDataList.size();

        for (int i = 0; i < cardCount; i++) {
            mAppListRowAdapter.add(appDataList.get(i));
        }
    }

    private void addAppRow() {
        String headerName = getResources().getString(R.string.app_header_app_name);
        mAppListRowAdapter = new ArrayObjectAdapter(new AppCardPresenter());

        loadAppData();

        HeaderItem header = new HeaderItem(0, headerName);
        rowsAdapter.add(new ListRow(header, mAppListRowAdapter));
    }

    private void addInputRow() {
        String headerName = getResources().getString(R.string.app_header_input_name);

        List<InputModel> inputModels = InputModel.getInputList(mInputSource);
        int cardCount = inputModels.size();
        for (int i = 0; i < cardCount; i++) {
            mInputListRowAdapter.add(inputModels.get(i));
        }
        HeaderItem header = new HeaderItem(0, headerName);
        rowsAdapter.add(new ListRow(header, mInputListRowAdapter));
    }


    private void addFunctionRow() {
        String headerName = getResources().getString(R.string.app_header_function_name);
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new FunctionCardPresenter());
        List<FunctionModel> functionModels = FunctionModel.getFunctionList(mContext);
        int cardCount = functionModels.size();
        for (int i = 0; i < cardCount; i++) {
            listRowAdapter.add(functionModels.get(i));
        }
        HeaderItem header = new HeaderItem(0, headerName);
        rowsAdapter.add(new ListRow(header, listRowAdapter));
    }


    private void registerAppReceiver(){
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        registerReceiver(appReceiver, filter);
    }

    private void unregisterAppReceiver() {
        unregisterReceiver(appReceiver);
    }

    private void updateAppList(Intent intent){
        String packageName = null;
        String action = intent.getAction();

        if (intent.getData() != null) {
            packageName = intent.getData().getSchemeSpecificPart();
            if (packageName == null || packageName.length() == 0) {
                return;
            }
            if (packageName.equals("com.android.provision"))
                return;
        }
        //com.farproc.wifi.analyzer
        Log.d(TAG, "---update app:" + packageName);

        if (Intent.ACTION_PACKAGE_REMOVED.equals(action)){
            int i;
            for(i=0; i<mAppListRowAdapter.size();i++){
                AppModel model = (AppModel)mAppListRowAdapter.get(i);
                if (model.getPackageName().equals(packageName)){
                    mAppListRowAdapter.remove(model);
                    break;
                }
            }
        }
        else if (Intent.ACTION_PACKAGE_ADDED.equals(action)){
            AppModel model = new AppDataManage(mContext).getLaunchAppModel(packageName);
            if (model != null){
                mAppListRowAdapter.add(model);
            }
        }
        else if (Intent.ACTION_PACKAGE_CHANGED.equals(action)){
            int i;
            AppModel newModel = new AppDataManage(mContext).getLaunchAppModel(packageName);

            for(i=0; i<mAppListRowAdapter.size();i++){
                AppModel model = (AppModel)mAppListRowAdapter.get(i);
                if (model.getPackageName().equals(packageName)){
                    mAppListRowAdapter.replace(i, newModel);
                    break;
                }
            }
        }
    }

    private int mLoadCount = 0;
    private Handler mLoadHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    if (mLoadCount < 10) {
                        mLoadCount++;
                        Log.d(TAG, "start get app.....");
                        updateVideo();
                        updateInput();
                        mLoadHandler.sendEmptyMessageDelayed(100, 1000);
                    }
                    break;

            }
        }
    };


    private BroadcastReceiver appReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            Log.d(TAG,"appReceiver receive " + action);
            if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
                    || Intent.ACTION_PACKAGE_REMOVED.equals(action)
                    || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                updateAppList(intent);
            }
        }
    };

    private class ChannelObserver extends ContentObserver {
        private static final String TAG = "ChannelObserver";

        public ChannelObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Log.d(TAG, "detect channel changed =" + uri);
            //updateVideo();
        }
    }

}
