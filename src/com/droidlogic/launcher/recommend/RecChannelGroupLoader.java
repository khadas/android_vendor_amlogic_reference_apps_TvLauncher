package com.droidlogic.launcher.recommend;

import android.content.Context;
import android.database.Cursor;
import android.media.tv.TvContract;

import com.droidlogic.launcher.livetv.Channel;
import com.droidlogic.launcher.search.loader.BaseLoader;
import com.droidlogic.launcher.util.Logger;

import java.util.ArrayList;
import java.util.List;

import static android.media.tv.TvContract.BaseTvColumns.COLUMN_PACKAGE_NAME;
import static android.provider.BaseColumns._ID;

public class RecChannelGroupLoader extends BaseLoader<Channel> {

    public RecChannelGroupLoader(Context context) {
        super(context);
    }

    @Override
    protected Cursor createCursor() {
        //TYPE_PREVIEW TYPE_OTHER
        String selection = "type = 'TYPE_PREVIEW') group by (" + COLUMN_PACKAGE_NAME;
        return getContentResolver().query(TvContract.Channels.CONTENT_URI, Channel.PROJECTION, selection, null, _ID + " ASC");
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
