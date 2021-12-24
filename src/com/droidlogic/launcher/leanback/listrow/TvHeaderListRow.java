package com.droidlogic.launcher.leanback.listrow;

import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ObjectAdapter;

import com.droidlogic.launcher.leanback.model.ITvHeader;
import com.droidlogic.launcher.leanback.presenter.content.TvHeaderPresenter;

public class TvHeaderListRow extends ListRow implements ITvHeader {

    private TvHeaderPresenter tvHeaderPresenter;

    public void setTvHeaderPresenter(TvHeaderPresenter tvHeaderPresenter) {
        this.tvHeaderPresenter = tvHeaderPresenter;
    }

    public TvHeaderListRow(HeaderItem header, ObjectAdapter adapter) {
        super(header, adapter);
    }

    public TvHeaderListRow(long id, HeaderItem header, ObjectAdapter adapter) {
        super(id, header, adapter);
    }

    public TvHeaderListRow(ObjectAdapter adapter) {
        super(adapter);
    }

    @Override
    public void signalUpdate() {
        if (tvHeaderPresenter != null) {
            tvHeaderPresenter.signalUpdate();
        }
    }

}
