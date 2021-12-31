package com.droidlogic.launcher.leanback.presenter.content;

import android.graphics.drawable.Drawable;
import androidx.leanback.widget.Presenter;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.input.InputModel;
import com.droidlogic.launcher.leanback.presenter.BaseViewHolder;

public class InputSourcePresenter extends Presenter {

    public InputSourcePresenter() {

    }

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

        public Holder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
            tvSourceInfo = (TextView) view.findViewById(R.id.tv_item_source_info);
        }

        @Override
        public void bindData(InputModel inputModel) {
            Drawable leftDrawable = view.getResources().getDrawable(inputModel.getIcon());
            leftDrawable.setBounds(0, 0, leftDrawable.getIntrinsicWidth(), leftDrawable.getIntrinsicHeight());
            tvSourceInfo.setText(inputModel.getName());
            tvSourceInfo.setCompoundDrawables(leftDrawable, null, null, null);
        }
    }

}
