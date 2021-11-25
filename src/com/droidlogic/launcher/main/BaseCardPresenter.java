package com.droidlogic.launcher.main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.app.AppModel;


public class BaseCardPresenter extends Presenter {

    protected Context mContext;
    //private Drawable mDefaultCardImage;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        mContext = parent.getContext();
        //mDefaultCardImage = mContext.getResources().getDrawable(R.drawable.pic_default);
        ImageCardView cardView = new ImageCardView(mContext) {
            @Override
            public void setSelected(boolean selected) {
                int selected_background = mContext.getResources().getColor(R.color.detail_background);
                int default_background = mContext.getResources().getColor(R.color.default_background);
                int color = selected ? selected_background : default_background;
                findViewById(R.id.info_field).setBackgroundColor(color);

                if (selected) {
                    Drawable bg = mContext.getResources().getDrawable(R.drawable.bg_stroke);
                    setPadding(2,2,2,2);
                    setBackground(bg);
                }
                else{
                    setPadding(0,0,0,0);
                }

                super.setSelected(selected);
            }
        };

        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        cardView.findViewById(R.id.content_text).setVisibility(View.GONE);
        TextView text = (TextView)cardView.findViewById(R.id.title_text);
        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        int img_default_background = mContext.getResources().getColor(R.color.img_default_background);
        cardView.getMainImageView().setBackgroundColor(img_default_background);
        int default_background = mContext.getResources().getColor(R.color.default_background);
        cardView.setInfoAreaBackgroundColor(default_background);

        return new ViewHolder(cardView);
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
