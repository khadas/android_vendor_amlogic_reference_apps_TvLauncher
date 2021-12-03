package com.droidlogic.launcher.recommend;

import android.support.v17.leanback.widget.ImageCardView;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.droidlogic.launcher.function.FunctionModel;
import com.droidlogic.launcher.main.BaseCardPresenter;

public class RecommendCardPresenter extends BaseCardPresenter {

    private int CARD_WIDTH  = 313;
    private int CARD_HEIGHT = 176;

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setMainImageDimensions(CARD_WIDTH,CARD_HEIGHT);
        cardView.setMainImageScaleType(ImageView.ScaleType.CENTER_INSIDE);

        RecommendModel model = (RecommendModel) item;
        //cardView.getMainImageView().setImageResource(functionModel.getIcon());
        cardView.setTitleText(model.getName());
        String url = model.getPosterArtUrl();
        if (url != null && url.length() > 0){
            Glide.with(cardView.getMainImageView().getContext())
                    .load(url)
                    .crossFade()
                    .into(cardView.getMainImageView());
        }
    }

}
