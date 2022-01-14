package com.droidlogic.launcher.search.loader;

import android.content.Context;
import android.database.Cursor;
import android.media.tv.TvContract;

import com.droidlogic.launcher.livetv.PreviewProgram;
import com.droidlogic.launcher.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PreviewProgramLoader extends BaseLoader<PreviewProgram> {

    private final String query;

    public PreviewProgramLoader(Context context, String query) {
        super(context);
        this.query = query;
    }

    @Override
    public Cursor createCursor() {
        String selection = "title like ?";
        String[] args = {String.format(Locale.getDefault(), "%%%s%%", query)};
        return getContentResolver().query(TvContract.PreviewPrograms.CONTENT_URI, PreviewProgram.PROJECTION, selection, args, null);
    }

    @Override
    public List<PreviewProgram> getDataList() {
        List<PreviewProgram> programs = new ArrayList<>();
        Cursor cursor = createCursor();
        if (cursor == null || cursor.getCount() == 0) {
            return programs;
        }
        try {
            while (cursor.moveToNext()) {
                programs.add(PreviewProgram.fromCursor(cursor));
            }
        } catch (Exception e) {
            Logger.i("Unable to get programs for " + e);
        } finally {
            cursor.close();
        }
        return programs;
    }

}
