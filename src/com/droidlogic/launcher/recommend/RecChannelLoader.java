package com.droidlogic.launcher.recommend;

import android.content.Context;
import android.database.Cursor;
import android.media.tv.TvContract;

import com.droidlogic.launcher.livetv.Channel;
import com.droidlogic.launcher.search.loader.BaseLoader;
import com.droidlogic.launcher.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class RecChannelLoader extends BaseLoader<Channel> {

    private final String query;

    public RecChannelLoader(Context context, String query) {
        super(context);
        this.query = query;
    }

    @Override
    protected Cursor createCursor() {
        //TYPE_PREVIEW TYPE_OTHER
        String selection = "type = ?";
        String[] args = {"TYPE_PREVIEW"};
        Logger.i("createCursor:" + args[0]);
        return getContentResolver().query(TvContract.Channels.CONTENT_URI, Channel.PROJECTION, selection, args, TvContract.Channels.COLUMN_PACKAGE_NAME + " ASC");
    }

    @Override
    public List<Channel> getDataList() {
        Cursor cursor = createCursor();
        List<Channel> channels = new ArrayList<>();
        if (cursor == null || cursor.getCount() == 0) {
            return channels;
        }
        try {
            while (cursor.moveToNext()) {
                Channel ch = Channel.fromCursor(cursor);
                channels.add(ch);
                Logger.i("get channel:" + ch.getDisplayName());
            }
        } catch (Exception e) {
            Logger.i("Unable to get channels" + e);
        } finally {
            cursor.close();
        }
        return channels;
    }

}
