package com.droidlogic.launcher.leanback.presenter;

import androidx.leanback.widget.Presenter;
import android.view.View;

public abstract class BasePresenter extends Presenter implements OnItemClickListener {

    private OnItemClickListener onItemClickListener;

    public BasePresenter() {
    }

    public BasePresenter(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onPresenterItemClick(View view, Object o) {
        if (onItemClickListener != null) {
            onItemClickListener.onPresenterItemClick(view, o);
        }
    }
}
