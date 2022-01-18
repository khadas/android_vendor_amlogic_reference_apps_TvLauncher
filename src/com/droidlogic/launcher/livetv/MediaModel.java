
package com.droidlogic.launcher.livetv;

import android.content.Context;

import com.droidlogic.launcher.R;

import java.util.ArrayList;
import java.util.List;


public class MediaModel {
    private long id;
    private int icon;
    private String title;
    private String content;
    private String inputId;
    private String type; //TYPE_PAL
    private boolean playing;

    private MediaModel(
            final long id,
            final String title,
            final String content,
            final String inputId,
            final String type) {
        this.id = id;
        this.icon = 0;
        this.title = title;
        this.content = content;
        this.inputId = inputId;
        this.type = type;
    }

    public MediaModel(long id, String title, String content, String inputId, String type, boolean playing) {
        this.id = id;
        this.icon = 0;
        this.title = title;
        this.content = content;
        this.inputId = inputId;
        this.type = type;
        this.playing = playing;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getInputId() {
        return inputId;
    }

    public void setInputId(String inputId) {
        this.inputId = inputId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public static List<MediaModel> getDTVModels(Context context) {
        List<MediaModel> mediaModels = new ArrayList<>();

        String titles;
        String contents;
        String type;
        int icon = 0;
        icon = R.drawable.live_tv;

        String inputid = null;
        List<Channel> channels = TVModelUtils.getChannels(context.getContentResolver(), inputid);
        int num = channels.size();
        //num = 0;
        if (num > 0) {

            ChannelDataManager channelDataManager = new ChannelDataManager(context);
            long currentChannelId = channelDataManager.getCurrentChannelId();

            long id;

            for (int i = 0; i < num; i++) {
                Channel channel = channels.get(i);
                titles = channel.getDisplayName();
                contents = channel.getDescription();
                id = channel.getId();
                inputid = channel.getInputId();
                type = channel.getType();
                //Loggerd("Media", "input id:" + channel.getInputId() + " " + inputid);
                MediaModel mediaModel = new MediaModel(id, titles, contents, inputid, type, currentChannelId == id);
                mediaModel.setIcon(icon);
                mediaModels.add(mediaModel);
            }
        } else {
            titles = context.getString(R.string.tv_no_channel);
            contents = context.getString(R.string.tv_search_channel);

            MediaModel mediaModel = new MediaModel(-1, titles, contents, null, null);
            mediaModel.setIcon(icon);
            mediaModels.add(mediaModel);
        }

        return mediaModels;
    }

}
