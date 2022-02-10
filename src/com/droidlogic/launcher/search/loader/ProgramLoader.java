package com.droidlogic.launcher.search.loader;

import android.content.Context;
import android.database.Cursor;
import android.media.tv.TvContract;

import com.droidlogic.launcher.livetv.Program;
import com.droidlogic.launcher.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProgramLoader extends BaseLoader<Program> {

    private final String query;

    public ProgramLoader(Context context, String query) {
        super(context);
        this.query = query;
    }

    @Override
    public Cursor createCursor() {
        String selection = "title like ?";
        String[] args = {String.format(Locale.getDefault(), "%%%s%%", query)};
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
