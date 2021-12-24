package com.droidlogic.launcher.leanback.presenter;

import android.support.v17.leanback.widget.Presenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseViewHolder<T> extends Presenter.ViewHolder implements OnItemClickListener {

    OnItemClickListener listener;

    public BaseViewHolder(View view) {
        super(view);
    }

    public BaseViewHolder(ViewGroup viewGroup, int layoutId) {
        this(null, viewGroup, layoutId);
    }

    public BaseViewHolder(OnItemClickListener listener, ViewGroup viewGroup, int layoutId) {
        this(LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false));
        this.listener = listener;
    }

    protected abstract void bindData(T t);

    @Override
    public void onPresenterItemClick(View view, Object o) {
        if (listener != null) {
            listener.onPresenterItemClick(view, o);
        }
    }

}
