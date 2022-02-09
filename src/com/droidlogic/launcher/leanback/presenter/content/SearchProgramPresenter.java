package com.droidlogic.launcher.leanback.presenter.content;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.droidlogic.launcher.R;
import com.droidlogic.launcher.leanback.presenter.BasePresenter;
import com.droidlogic.launcher.leanback.presenter.BaseViewHolder;
import com.droidlogic.launcher.livetv.Program;

public class SearchProgramPresenter extends BasePresenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new Holder(viewGroup, R.layout.item_search_program);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object o) {
        Holder holder = (Holder) viewHolder;
        holder.bindData((Program) o);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }

    private static class Holder extends BaseViewHolder<Program> {

        private final ImageView imageCardView;
        private final TextView tvTitle;

        public Holder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
            imageCardView = (ImageView) view.findViewById(R.id.item_search_img);
            tvTitle = (TextView) view.findViewById(R.id.item_search_title);
        }

        @Override
        protected void bindData(Program program) {
            tvTitle.setText(program.getTile());
            Glide.with(view.getContext())
                    .load(program.getPosterArtUrl())
                    .placeholder(R.drawable.place_holder_tv)
                    .into(imageCardView);
//            String iconUri=channel.getAppLinkPosterArtUri();
//            if (iconUri!=null){
//                imageCardView.setImageURI(Uri.parse(iconUri));
//            }
        }
    }

}
