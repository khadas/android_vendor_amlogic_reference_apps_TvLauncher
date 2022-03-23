package com.droidlogic.launcher.recommend;

import com.droidlogic.launcher.livetv.Channel;
import com.droidlogic.launcher.livetv.PreviewProgram;

import java.util.List;

public class AppPreviewProgramModel {

    private final String channelName;
    private final Channel channel;
    private final List<PreviewProgram> previewPrograms;

    public AppPreviewProgramModel(String channelName, Channel channel, List<PreviewProgram> previewPrograms) {
        this.channel = channel;
        this.channelName = channelName;
        this.previewPrograms = previewPrograms;
    }

    public List<PreviewProgram> getPreviewPrograms() {
        return previewPrograms;
    }

    public long getChannelId() {
        return channel.getId();
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelPkgName() {
        return channel.getPackageName();
    }

    @Override
    public String toString() {
        return "AppPreviewProgramModel{" +
                "channel=" + channel +
                ", channelName='" + channelName + '\'' +
                ", previewPrograms=" + previewPrograms +
                '}';
    }

}
