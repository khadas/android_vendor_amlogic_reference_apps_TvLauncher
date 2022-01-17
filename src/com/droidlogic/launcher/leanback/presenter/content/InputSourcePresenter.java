package com.droidlogic.launcher.leanback.presenter.content;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.leanback.widget.Presenter;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.input.InputModel;
import com.droidlogic.launcher.leanback.presenter.BaseViewHolder;

public class InputSourcePresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new Holder(viewGroup, R.layout.item_info_source);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Holder holder = (Holder) viewHolder;
        InputModel inputModel = (InputModel) item;
        holder.bindData(inputModel);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }

    private static class Holder extends BaseViewHolder<InputModel> {

        private final TextView tvSourceInfo;
        private final View inPutState;

        public Holder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
            tvSourceInfo = (TextView) view.findViewById(R.id.tv_item_source_info);
            inPutState = view.findViewById(R.id.img_source_selected);
        }

        @Override
        public void bindData(InputModel inputModel) {
            Drawable leftDrawable = ContextCompat.getDrawable(view.getContext(), inputModel.getIcon());
            if (leftDrawable != null) {
                leftDrawable.setBounds(0, 0, leftDrawable.getIntrinsicWidth(), leftDrawable.getIntrinsicHeight());
                tvSourceInfo.setCompoundDrawables(leftDrawable, null, null, null);
            } else {
                tvSourceInfo.setCompoundDrawables(null, null, null, null);
            }
            tvSourceInfo.setText(inputModel.getName());
            inPutState.setVisibility(inputModel.isSignalInput() ? View.VISIBLE : View.GONE);
        }

    }

}
