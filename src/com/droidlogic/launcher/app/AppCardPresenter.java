package com.droidlogic.launcher.app;

import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.widget.ImageView;
import com.droidlogic.launcher.main.BaseCardPresenter;


public class AppCardPresenter extends BaseCardPresenter {

    private static int CARD_WIDTH  = 313;
    private static int CARD_HEIGHT = 176;

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setMainImageDimensions(CARD_WIDTH,CARD_HEIGHT);

        AppModel app = (AppModel) item;
        Drawable  draw = app.getIcon();
        ImageView img = cardView.getMainImageView();
        setMatrix(img, draw);
        //img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        img.setImageDrawable(draw);
        cardView.setTitleText(app.getName());
    }

    private void setMatrix(ImageView img, Drawable draw){
        int dw = 100;
        int dh = 100;

        int width  = draw.getIntrinsicWidth();
        int height = draw.getIntrinsicHeight();
        float sx = (float)dw/(float)width;
        float sy = (float)dh/(float)height;

        Matrix matrix = new Matrix();
        matrix.setScale(sx, sy);
        width  = (CARD_WIDTH  - dw) /2;
        height = (CARD_HEIGHT - dh)/2;
        matrix.postTranslate(width, height);

        img.setScaleType(ImageView.ScaleType.MATRIX);
        img.setImageMatrix(matrix);
    }

}
