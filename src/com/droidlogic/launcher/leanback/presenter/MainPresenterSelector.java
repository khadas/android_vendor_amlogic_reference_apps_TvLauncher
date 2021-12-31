
package com.droidlogic.launcher.leanback.presenter;

import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
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
