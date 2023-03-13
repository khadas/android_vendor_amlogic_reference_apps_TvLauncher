package com.droidlogic.launcher.api;

import android.util.Pair;

import com.droidlogic.launcher.model.ZeasnColumn;
import com.droidlogic.launcher.model.ZeasnColumnContent;
import com.droidlogic.launcher.model.ZeasnToken;
import com.google.gson.Gson;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

public enum ZeasnApiService {

    INSTANCE;

    public Observable<List<Pair<ZeasnColumn.DataBean.ChildrenBean, ZeasnColumnContent>>> fetColumnContent(final String mac) {
        return Observable.create((ObservableOnSubscribe<ZeasnToken>) emitter -> {
            final String params = "productId=aml950d&brandId=40&deviceSetId=10af6434deeb464e4fa8c2f61fe02c4979&functionType=TvLauncher&ifGetTvDetail=1&iconResolution=320*180&terminalType=TV&appVersion=1010054&countryCode=US&osType=ANDROID&androidVersion=6.0&langCode=en&mac=" + URLEncoder.encode(mac, "utf-8");
            String tokenResult = HttpUtils.doRequest("https://saas.zeasn.tv/auth-api/api/v1/auth/deviceSign?" + params);
            emitter.onNext(new Gson().fromJson(tokenResult, ZeasnToken.class));
            emitter.onComplete();
        }).map(zeasnToken -> {
            final String token = zeasnToken.getData().getToken();
            String columnResult = HttpUtils.doRequest("https://saas.zeasn.tv/sp/api/device/v1/column?token=" + token);
            ZeasnColumn zeasnColumn = new Gson().fromJson(columnResult, ZeasnColumn.class);
            List<Pair<ZeasnColumn.DataBean.ChildrenBean, ZeasnColumnContent>> contentList = new ArrayList<>();
            for (ZeasnColumn.DataBean dataBean : zeasnColumn.getData()) {
                for (ZeasnColumn.DataBean.ChildrenBean child : dataBean.getChildren()) {
                    String contentResult = HttpUtils.doRequest("https://saas.zeasn.tv/sp/api/device/v1/column/content?token=" + token + "&columnIds=" + child.getId());
                    ZeasnColumnContent zeasnColumnContent = new Gson().fromJson(contentResult, ZeasnColumnContent.class);
                    contentList.add(new Pair<>(child, zeasnColumnContent));
                }
            }
            return contentList;
        });
    }

}
