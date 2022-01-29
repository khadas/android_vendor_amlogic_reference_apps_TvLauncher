package com.droidlogic.launcher.app.gallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.DiffCallback;
import androidx.leanback.widget.ItemBridgeAdapter;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.app.AppDataManage;
import com.droidlogic.launcher.app.AppModel;
import com.droidlogic.launcher.leanback.presenter.content.InstalledAppPresenter;
import com.droidlogic.launcher.leanback.view.AppVerticalGridView;
import com.droidlogic.launcher.util.AppStateChangeListener;
import com.droidlogic.launcher.util.DensityTool;
import com.droidlogic.launcher.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AppGalleryFragment extends Fragment {

    private TextView tvInstalledName;
    private AppVerticalGridView vgInstalledApp;
    private ArrayObjectAdapter installedAppAdapter;
    private AppStateChangeListener appListener;
    private Disposable appLoadDisposable = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_gallery, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appListener = new AppStateChangeListener(getContext(), new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String packageName = null;
                if (intent.getData() != null) {
                    packageName = intent.getData().getSchemeSpecificPart();
                }
                if (packageName == null || packageName.length() == 0) {
                    return;
                }
                if (packageName.equals("com.android.provision")) {
                    return;
                }
                loadApps();
            }
        });
        appListener.registerReceiver();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        loadApps();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        appListener.unregisterReceiver();
        cancelLoadApps();
    }

    private void initView() {
        View view = getView();
        if (view == null) return;
        tvInstalledName = (TextView) view.findViewById(R.id.tv_app_installed_name);
        vgInstalledApp = (AppVerticalGridView) view.findViewById(R.id.vg_app_installed);
        vgInstalledApp.setColumnNumbers(6);
        vgInstalledApp.setHorizontalSpacing(DensityTool.dp2px(50));
        vgInstalledApp.setVerticalSpacing(DensityTool.dp2px(50));
        installedAppAdapter = new ArrayObjectAdapter(new InstalledAppPresenter());
        vgInstalledApp.setAdapter(new ItemBridgeAdapter(installedAppAdapter) {
            @Override
            protected void onBind(final ViewHolder viewHolder) {
                super.onBind(viewHolder);
                viewHolder.itemView.setOnClickListener(v -> {
                    Object itemData = viewHolder.getItem();
                    if (itemData instanceof AppModel) {
                        AppModel appModel = (AppModel) itemData;
                        appModel.onClickModel(v);
                    }
                });
            }
        });
    }

    private void cancelLoadApps() {
        if (appLoadDisposable != null) {
            appLoadDisposable.dispose();
        }
    }

    private void loadApps() {
        cancelLoadApps();
        appLoadDisposable = Observable.create((ObservableOnSubscribe<List<AppModel>>) emitter -> {
            ArrayList<AppModel> appDataList = new AppDataManage(getContext()).getAppsList();
            emitter.onNext(appDataList);
            emitter.onComplete();
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(appModels -> {
            if (installedAppAdapter == null || tvInstalledName == null || vgInstalledApp == null)
                return;
            installedAppAdapter.setItems(appModels, new DiffCallback<AppModel>() {
                @Override
                public boolean areItemsTheSame(@NonNull AppModel oldModel, @NonNull AppModel newModel) {
                    return TextUtils.equals(oldModel.getPackageName(), newModel.getPackageName());
                }

                @Override
                public boolean areContentsTheSame(@NonNull AppModel oldModel, @NonNull AppModel newModel) {
                    return TextUtils.equals(oldModel.getPackageName(), newModel.getPackageName());
                }
            });

            tvInstalledName.setText(String.format(Locale.getDefault(), "%s ( %d )", getString(R.string.installed_app), appModels.size()));
            int selectedPosition = vgInstalledApp.getSelectedPosition();
            if (selectedPosition >= installedAppAdapter.size()) {
                selectedPosition = installedAppAdapter.size() - 1;
            }
            if (selectedPosition > -1) {
                int finalSelectedPosition = selectedPosition;
                vgInstalledApp.post(() -> vgInstalledApp.setSelectedPosition(finalSelectedPosition));
            }
        }, throwable -> Logger.d("" + throwable));
    }

}