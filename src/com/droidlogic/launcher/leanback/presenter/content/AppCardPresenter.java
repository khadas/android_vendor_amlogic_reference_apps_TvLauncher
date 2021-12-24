package com.droidlogic.launcher.leanback.presenter.content;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.Presenter;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.app.AppModel;
import com.droidlogic.launcher.leanback.presenter.BaseViewHolder;
import com.droidlogic.launcher.util.ImageTool;


public class AppCardPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new Holder(viewGroup, R.layout.item_common_card);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Holder holder = (Holder) viewHolder;
        holder.bindData((AppModel) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }

    private static class Holder extends BaseViewHolder<AppModel> {

        private final ImageView imgAppIcon;
        private final ImageView imgAppBanner;
        private final TextView tvAppName;

        public Holder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
            imgAppIcon = (ImageView) view.findViewById(R.id.item_img_card_icon);
            imgAppBanner = (ImageView) view.findViewById(R.id.item_img_card_banner);
            tvAppName = (TextView) view.findViewById(R.id.item_tv_card_name);
            view.findViewById(R.id.item_img_card_parent)
                    .setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean gainFocus) {
                            tvAppName.setSelected(gainFocus);
                        }
                    });
        }

        @Override
        protected void bindData(AppModel appModel) {
            Drawable banner = appModel.getBanner();
            Drawable icon = appModel.getIcon();
            imgAppBanner.setImageDrawable(banner);
            if (banner != null) {
                icon = null;
            } else {
                generateAppCardBg(imgAppBanner, ImageTool.drawableToBitmap(icon));
            }
            imgAppIcon.setImageDrawable(icon);
            tvAppName.setText(appModel.getName());
        }

        private void generateAppCardBg(final ImageView imageView, Bitmap bitmap) {
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    if (palette == null) return;
                    int startColor = palette.getLightMutedColor(Color.TRANSPARENT);
                    int endColor = palette.getLightVibrantColor(Color.TRANSPARENT);
                    if (palette.getDarkVibrantColor(Color.TRANSPARENT) != Color.TRANSPARENT) {
                        startColor = palette.getDarkVibrantColor(Color.TRANSPARENT);
                        endColor = palette.getVibrantColor(Color.TRANSPARENT);
                    } else if (palette.getDarkMutedColor(Color.TRANSPARENT) != Color.TRANSPARENT) {
                        startColor = palette.getDarkMutedColor(Color.TRANSPARENT);
                        endColor = palette.getMutedColor(Color.TRANSPARENT);
                    }
                    bindImageBackground(imageView, startColor, endColor);
                }
            });
        }

        private void bindImageBackground(ImageView imageView, int startColor, int endColor) {
            int[] colors = {startColor, endColor};
            Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas();
            Paint paint = new Paint();
            canvas.setBitmap(bitmap);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            LinearGradient gradient = new LinearGradient(0, 0, 0, bitmap.getHeight(), colors[0], colors[1], Shader.TileMode.CLAMP);
            paint.setShader(gradient);
            RectF rectF = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
            canvas.drawRect(rectF, paint);
            imageView.setBackground(new BitmapDrawable(bitmap));
        }
    }

}
