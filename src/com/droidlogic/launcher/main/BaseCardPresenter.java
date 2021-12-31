package com.droidlogic.launcher.main;

import android.content.Context;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.Presenter;

import android.view.ViewGroup;


public class BaseCardPresenter extends Presenter {

    protected Context mContext;
    //private Drawable mDefaultCardImage;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
//        mContext = parent.getContext();
//        //mDefaultCardImage = mContext.getResources().getDrawable(R.drawable.pic_default);
//        BorderImageCardView cardView = new BorderImageCardView(mContext);
//
//        cardView.findViewById(R.id.content_text).setVisibility(View.GONE);
//        TextView text = (TextView)cardView.findViewById(R.id.title_text);
//        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//
//        int img_default_background = mContext.getResources().getColor(R.color.img_default_background);
//        cardView.getMainImageView().setBackgroundColor(img_default_background);
//        int default_background = mContext.getResources().getColor(R.color.default_background);
//        cardView.setInfoAreaBackgroundColor(default_background);
//
//        return new ViewHolder(cardView);

        return null;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        //ImageCardView cardView = (ImageCardView) viewHolder.view;
        //cardView.setMainImageDimensions(CARD_WIDTH,CARD_HEIGHT);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }
}
