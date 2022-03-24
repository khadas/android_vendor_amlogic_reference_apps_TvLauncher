package com.droidlogic.launcher.leanback.listrow;

import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ObjectAdapter;

import com.droidlogic.launcher.recommend.AppPreviewProgramModel;

public class TvRecommendListRow extends ListRow {

    private final AppPreviewProgramModel previewProgramModel;
    private boolean recycleMark = false;

    public TvRecommendListRow(HeaderItem header, ObjectAdapter adapter, AppPreviewProgramModel previewProgramModel) {
        super(header, adapter);
        this.previewProgramModel = previewProgramModel;
    }

    public TvRecommendListRow(long id, HeaderItem header, ObjectAdapter adapter, AppPreviewProgramModel previewProgramModel) {
        super(id, header, adapter);
        this.previewProgramModel = previewProgramModel;
    }

    public TvRecommendListRow(ObjectAdapter adapter, AppPreviewProgramModel previewProgramModel) {
        super(adapter);
        this.previewProgramModel = previewProgramModel;
    }

    public AppPreviewProgramModel getPreviewProgramModel() {
        return previewProgramModel;
    }

    public boolean isRecycleMark() {
        return recycleMark;
    }

    public void setRecycleMark(boolean recycleMark) {
        this.recycleMark = recycleMark;
    }
}
