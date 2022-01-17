package com.droidlogic.launcher.leanback.presenter.content;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.leanback.presenter.BaseViewHolder;
import com.droidlogic.launcher.livetv.MediaModel;


public class TvCardPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new Holder(viewGroup, R.layout.item_info_source);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object o) {
        Holder holder = (Holder) viewHolder;
        holder.bindData((MediaModel) o);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }

    private static class Holder extends BaseViewHolder<MediaModel> {

        private final TextView tvAppName;
        private final View inPutState;

        public Holder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
            tvAppName = (TextView) view.findViewById(R.id.tv_item_source_info);
            inPutState = view.findViewById(R.id.img_source_selected);
        }

        @Override
        protected void bindData(MediaModel mediaModel) {
            tvAppName.setText(mediaModel.getTitle());
            inPutState.setVisibility(mediaModel.isPlaying() ? View.VISIBLE : View.GONE);
        }

    }

}
