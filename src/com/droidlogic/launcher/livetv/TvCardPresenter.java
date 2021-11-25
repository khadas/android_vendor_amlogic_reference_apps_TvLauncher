package com.droidlogic.launcher.livetv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.main.BaseCardPresenter;


public class TvCardPresenter extends BaseCardPresenter {

    private int CARD_WIDTH  = 313;
    private int CARD_HEIGHT = 176;

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
        if (item instanceof MediaModel) {
            MediaModel mediaModel = (MediaModel) item;
            cardView.setTitleText(mediaModel.getTitle());
            cardView.setMainImageScaleType(ImageView.ScaleType.CENTER_INSIDE);

            //cardView.setContentText(mediaModel.getContent());
            //cardView.getMainImageView().setBackgroundColor(Color.rgb(0, 255, 255));
            if (mediaModel.getIcon() != 0){
                cardView.getMainImageView().setImageResource(mediaModel.getIcon());
            }
        }
    }

}
