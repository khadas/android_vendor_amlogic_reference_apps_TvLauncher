package com.droidlogic.launcher.leanback.presenter.title;

import android.graphics.Color;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowHeaderPresenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droidlogic.launcher.R;

public class CusTitlePresenter extends RowHeaderPresenter {

    private final int mLayoutResourceId;
    private final boolean mAnimateSelect;

    public CusTitlePresenter() {
        this(R.layout.row_title_cus);
    }

    public CusTitlePresenter(int layoutResourceId) {
        this(layoutResourceId, true);
    }

    public CusTitlePresenter(int layoutResourceId, boolean animateSelect) {
        mLayoutResourceId = layoutResourceId;
        mAnimateSelect = animateSelect;
    }

    @Override
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup parent) {
        View root = LayoutInflater.from(parent.getContext())
                .inflate(mLayoutResourceId, parent, false);
        HeadViewHolder viewHolder = new HeadViewHolder(root);
        if (mAnimateSelect) {
            setSelectLevel(viewHolder, 0);
        }
        return viewHolder;
    }

    @Override
    protected void onSelectLevelChanged(ViewHolder holder) {
        //super.onSelectLevelChanged(holder);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        HeaderItem headerItem = item == null ? null : ((Row) item).getHeaderItem();
        TextView tvTitleName = ((TextView) viewHolder.view.findViewById(R.id.row_title_name));
        if (headerItem == null) {
            if (viewHolder.view.findViewById(R.id.row_title_name) != null) {
                tvTitleName.setText(null);
            }
            viewHolder.view.setContentDescription(null);
            viewHolder.view.setVisibility(View.GONE);
        } else {
            if (tvTitleName != null) {
                tvTitleName.setText(headerItem.getName());
                tvTitleName.setTextColor(Color.WHITE);
            }
            viewHolder.view.setContentDescription(headerItem.getContentDescription());
            viewHolder.view.setVisibility(View.VISIBLE);
        }
    }

    public static class HeadViewHolder extends ViewHolder {
        HeadViewHolder(View view) {
            super(view);
        }


    }
}
