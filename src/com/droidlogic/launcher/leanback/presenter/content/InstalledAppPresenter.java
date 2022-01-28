package com.droidlogic.launcher.leanback.presenter.content;import android.view.ViewGroup;import android.widget.ImageView;import android.widget.TextView;import androidx.leanback.widget.Presenter;import com.droidlogic.launcher.R;import com.droidlogic.launcher.app.AppModel;import com.droidlogic.launcher.leanback.presenter.BaseViewHolder;public class InstalledAppPresenter extends Presenter {    @Override    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {        return new Holder(viewGroup, R.layout.item_installed_app);    }    @Override    public void onBindViewHolder(ViewHolder viewHolder, Object o) {        Holder holder = (Holder) viewHolder;        holder.bindData((AppModel) o);    }    @Override    public void onUnbindViewHolder(ViewHolder viewHolder) {    }    public static final class Holder extends BaseViewHolder<AppModel> {        private final TextView tvAppName;        private final ImageView imgAppIcon;        public Holder(ViewGroup viewGroup, int layoutId) {            super(viewGroup, layoutId);            tvAppName = (TextView) view.findViewById(R.id.tv_app_name);            imgAppIcon = (ImageView) view.findViewById(R.id.iv_app_icon);        }        @Override        protected void bindData(AppModel appModel) {            imgAppIcon.setImageDrawable(appModel.getIcon());            tvAppName.setText(appModel.getName());        }    }}