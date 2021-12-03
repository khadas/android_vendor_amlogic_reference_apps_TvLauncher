
package com.droidlogic.launcher.recommend;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.app.AppUninstall;
import com.droidlogic.launcher.livetv.PreviewProgram;
import com.droidlogic.launcher.livetv.TVModelUtils;

import java.util.ArrayList;
import java.util.List;

public class RecommendModel {

    private int icon;
    private long id;
    private String name;
    private String postArtUrl;
    private Intent mIntent;

    private RecommendModel(String name, long id, String url){
        this.name = name;
        this.id   = id;
        this.postArtUrl = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }
    public String getPosterArtUrl() { return postArtUrl; }

    public Intent getIntent() {
        return mIntent;
    }

    public void setIntent(Intent intent) {
        mIntent = intent;
    }

    public static List<RecommendModel> getProgramList(Context context, long channelId) {
        List<RecommendModel> models = new ArrayList<>();
        List<PreviewProgram> programs = TVModelUtils.getPreviewPrograms(context.getContentResolver(), channelId);

        for(PreviewProgram program : programs){
            RecommendModel model = new RecommendModel(program.getTile(), program.getChannelId(),
                    program.getPosterArtUrl());
            models.add(model);
        }

        return models;
    }
}
