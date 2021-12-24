
package com.droidlogic.launcher.leanback.presenter;

import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;

import com.droidlogic.launcher.input.InputSourceManager;
import com.droidlogic.launcher.leanback.listrow.TvHeaderListRow;
import com.droidlogic.launcher.leanback.presenter.content.TvHeaderPresenter;
import com.droidlogic.launcher.leanback.presenter.title.CusTitlePresenter;

public class MainPresenterSelector extends PresenterSelector {

    private final InputSourceManager inputSource;
    private final OnItemClickListener onItemClickListener;

    public MainPresenterSelector(InputSourceManager inputSource, OnItemClickListener onItemClickListener) {
        this.inputSource = inputSource;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public Presenter getPresenter(Object model) {
        Presenter presenter;
        if (model instanceof TvHeaderListRow) {
            TvHeaderPresenter tvHeaderPresenter = (TvHeaderPresenter) buildTvHeaderPresenter();
            TvHeaderListRow tvHeaderListRow = (TvHeaderListRow) model;
            tvHeaderListRow.setTvHeaderPresenter(tvHeaderPresenter);
            //TvHeaderListRow
            return tvHeaderPresenter;
        }
        presenter = buildNormalPresenter();
        if (model instanceof ListRow && presenter instanceof ListRowPresenter) {
            modifyCusTitle((ListRow) model, (ListRowPresenter) presenter);
        }
        return presenter;
    }

    private Presenter buildNormalPresenter() {
        return new ListRowPresenter(0) {

            @Override
            protected void initializeRowViewHolder(RowPresenter.ViewHolder holder) {
                setShadowEnabled(false);
                setSelectEffectEnabled(false);
                setKeepChildForeground(false);
                ViewHolder viewHolder = (ViewHolder) holder;
                final HorizontalGridView gridView = viewHolder.getGridView();
                gridView.setClipChildren(false);
                gridView.setClipToPadding(false);
                super.initializeRowViewHolder(holder);
            }

            @Override
            protected void onRowViewAttachedToWindow(RowPresenter.ViewHolder vh) {
                super.onRowViewAttachedToWindow(vh);
                vh.setOnItemViewClickedListener(new OnItemViewClickedListener() {
                    @Override
                    public void onItemClicked(Presenter.ViewHolder viewHolder, Object o, RowPresenter.ViewHolder rowViewHolder, Row row) {
                        Log.i("MainSelector", "onRowViewAttachedToWindow-->setOnItemViewClickedListener:" + o + "view:" + viewHolder.view);
                        if (onItemClickListener != null) {
                            onItemClickListener.onPresenterItemClick(viewHolder.view, o);
                        }
                    }
                });
            }

            @Override
            protected void onRowViewDetachedFromWindow(RowPresenter.ViewHolder vh) {
                super.onRowViewDetachedFromWindow(vh);
                vh.setOnItemViewClickedListener(null);
            }
        };
    }

    private Presenter buildTvHeaderPresenter() {
        return new TvHeaderPresenter(onItemClickListener, inputSource);
    }

    private void modifyCusTitle(ListRow listRow, ListRowPresenter presenter) {
        if (listRow.getHeaderItem() != null) {
            presenter.setHeaderPresenter(new CusTitlePresenter());
        }
    }

}
