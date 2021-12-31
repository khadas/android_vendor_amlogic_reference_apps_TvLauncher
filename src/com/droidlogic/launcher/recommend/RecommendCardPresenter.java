package com.droidlogic.launcher.recommend;

import android.widget.ImageView;

import androidx.leanback.widget.ImageCardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.droidlogic.launcher.main.BaseCardPresenter;

public class RecommendCardPresenter extends BaseCardPresenter {

    private int CARD_WIDTH = 313;
    private int CARD_HEIGHT = 176;

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
        cardView.setMainImageScaleType(ImageView.ScaleType.CENTER_INSIDE);

        RecommendModel model = (RecommendModel) item;

        //cardView.getMainImageView().setImageResource(functionModel.getIcon());
        cardView.setTitleText(model.getName());
        String url = model.getPosterArtUrl();
        if (url != null && url.length() > 0) {
            DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
            Glide.with(cardView.getMainImageView().getContext())
                    .load(url)
                    .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
                    .into(cardView.getMainImageView());
        }
    }

}
