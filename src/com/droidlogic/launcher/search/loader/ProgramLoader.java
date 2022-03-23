package com.droidlogic.launcher.search.loader;

import android.content.Context;
import android.database.Cursor;
import android.media.tv.TvContract;

import com.droidlogic.launcher.livetv.Program;
import com.droidlogic.launcher.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.media.tv.TvContract.BaseTvColumns.COLUMN_PACKAGE_NAME;
import static android.media.tv.TvContract.Programs.COLUMN_CHANNEL_ID;
import static com.droidlogic.launcher.livetv.Channel.INVALID_CHANNEL_ID;

public class ProgramLoader extends BaseLoader<Program> {

    private final String query;
    private String packageName = "";
    private long channelId = INVALID_CHANNEL_ID;

    public ProgramLoader(Context context, String query) {
        super(context);
        this.query = query;
    }

    public ProgramLoader(Context context, String query, String packageName, long channelId) {
        super(context);
        this.query = query;
        this.packageName = packageName;
        this.channelId = channelId;
    }

    @Override
    public Cursor createCursor() {
        String selection = COLUMN_PACKAGE_NAME + " = ? and title like ? and " + COLUMN_CHANNEL_ID + " = ?";
        String[] args = {
                String.format(Locale.getDefault(), "%s", packageName),
                String.format(Locale.getDefault(), "%%%s%%", query),
                String.format(Locale.getDefault(), "%s", channelId == INVALID_CHANNEL_ID ? "" : String.valueOf(channelId))

        };
        return getContentResolver().query(TvContract.Programs.CONTENT_URI, Program.PROJECTION, selection, args, null);
    }

    @Override
    public List<Program> getDataList() {
        List<Program> programs = new ArrayList<>();
        Cursor cursor = createCursor();
        if (cursor == null || cursor.getCount() == 0) {
            return programs;
        }
        try {
            while (cursor.moveToNext()) {
                programs.add(Program.fromCursor(cursor));
            }
        } catch (Exception e) {
            Logger.i("Unable to get programs for " + e);
        } finally {
            cursor.close();
        }
        return programs;
    }

}
