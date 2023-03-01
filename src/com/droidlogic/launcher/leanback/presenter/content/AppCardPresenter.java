package com.droidlogic.launcher.leanback.presenter.content;

import android.annotation.SuppressLint;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.leanback.widget.Presenter;
import androidx.palette.graphics.Palette;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.app.AppModel;
import com.droidlogic.launcher.app.AppMoreModel;
import com.droidlogic.launcher.app.IAppInfo;
import com.droidlogic.launcher.app.ShortcutModel;
import com.droidlogic.launcher.leanback.presenter.BaseViewHolder;
import com.droidlogic.launcher.util.ImageTool;

import java.util.List;


public class AppCardPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new AppHolder(viewGroup, R.layout.item_common_card);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        AppHolder appHolder = (AppHolder) viewHolder;
        appHolder.bindData((IAppInfo) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }

    private static class AppHolder extends BaseViewHolder<IAppInfo> {

        private final ImageView imgAppIcon;
        private final ImageView imgAppBanner;
        private final TextView tvAppName;
        private final GridView gvAppMore;

        public AppHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
            imgAppIcon = (ImageView) view.findViewById(R.id.item_img_card_icon);
            imgAppBanner = (ImageView) view.findViewById(R.id.item_img_card_banner);
            tvAppName = (TextView) view.findViewById(R.id.item_tv_card_name);
            gvAppMore = (GridView) view.findViewById(R.id.gv_app_more);
            view.findViewById(R.id.item_img_card_parent)
                    .setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean gainFocus) {
                            tvAppName.setSelected(gainFocus);
                        }
                    });
        }

        @Override
        protected void bindData(IAppInfo appInfo) {
            gvAppMore.setAdapter(null);
            gvAppMore.setVisibility(View.GONE);
            imgAppIcon.setImageDrawable(null);
            imgAppBanner.setImageDrawable(null);
            if (appInfo instanceof AppModel) {
                AppModel appModel = (AppModel) appInfo;
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
            } else if (appInfo instanceof AppMoreModel) {
                tvAppName.setText(R.string.app_more);
                AppMoreModel appMoreModel = (AppMoreModel) appInfo;
                AppMoreAdapter adapter = new AppMoreAdapter(appMoreModel.getAppModelList());
                gvAppMore.setAdapter(adapter);
                gvAppMore.setVisibility(View.VISIBLE);
            } else if (appInfo instanceof ShortcutModel) {
                ShortcutModel shortcutModel = (ShortcutModel) appInfo;
                tvAppName.setText(shortcutModel.getName());
                imgAppIcon.setImageResource(((ShortcutModel) appInfo).getIconRes());
            }
        }

        private void generateAppCardBg(final ImageView imageView, Bitmap bitmap) {
            Palette.from(bitmap).generate(palette -> {
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

    private static class AppMoreAdapter extends BaseAdapter {

        private final List<AppModel> appModelList;

        public AppMoreAdapter(List<AppModel> appModelList) {
            this.appModelList = appModelList;
        }

        @Override
        public int getCount() {
            final int MAX_SHOW_NUM = 8;
            int count = appModelList == null ? 0 : appModelList.size();
            return Math.min(count, MAX_SHOW_NUM);
        }

        @Override
        public Object getItem(int position) {
            return appModelList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            parent.setFocusable(false);
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_more, parent, false);
            ImageView imgAppIcon = (ImageView) convertView.findViewById(R.id.img_app_more_icon);
            imgAppIcon.setImageDrawable(appModelList.get(position).getIcon());
            return convertView;
        }

    }

}
