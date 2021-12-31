package com.droidlogic.launcher.leanback.presenter.content;

import androidx.leanback.widget.Presenter;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.function.FunctionModel;
import com.droidlogic.launcher.leanback.presenter.BaseViewHolder;

public class SystemFunctionPresenter extends Presenter {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new Holder(viewGroup, R.layout.item_system_function);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        FunctionModel functionModel = (FunctionModel) item;
        Holder holder = (Holder) viewHolder;
        holder.bindData(functionModel);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }

    private static class Holder extends BaseViewHolder<FunctionModel> {

        private final ImageView imgFunIcon;
        private final TextView tvFunName;
        private final LinearLayout lltParent;

        public Holder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
            imgFunIcon = (ImageView) view.findViewById(R.id.img_item_fun_icon);
            tvFunName = (TextView) view.findViewById(R.id.tv_item_fun_name);
            lltParent = (LinearLayout) view.findViewById(R.id.item_llt_parent);
        }

        public void bindData(FunctionModel functionModel) {
            lltParent.setBackgroundColor(functionModel.getBgColor());
            imgFunIcon.setImageResource(functionModel.getIcon());
            tvFunName.setText(view.getResources().getString(functionModel.getName()));
        }

    }

}
