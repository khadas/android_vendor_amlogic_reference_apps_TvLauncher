package com.droidlogic.launcher.leanback.presenter.content;

import android.support.v17.leanback.widget.ItemBridgeAdapter;
import android.support.v17.leanback.widget.OnChildViewHolderSelectedListener;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.function.FunctionRow;
import com.droidlogic.launcher.input.InputRow;
import com.droidlogic.launcher.input.InputSourceManager;
import com.droidlogic.launcher.leanback.model.HotFunctionModel;
import com.droidlogic.launcher.leanback.model.IRowSignalSourceProvider;
import com.droidlogic.launcher.leanback.model.ITvHeader;
import com.droidlogic.launcher.leanback.presenter.BasePresenter;
import com.droidlogic.launcher.leanback.presenter.BaseViewHolder;
import com.droidlogic.launcher.leanback.presenter.OnItemClickListener;
import com.droidlogic.launcher.leanback.view.BorderEffectLayout;
import com.droidlogic.launcher.livetv.TvConfig;
import com.droidlogic.launcher.livetv.TvRow;
import com.droidlogic.launcher.model.TvViewModel;

public class TvHeaderPresenter extends BasePresenter implements ITvHeader {

    private ITvHeader iTvHeader = null;

    private final InputSourceManager inputSource;

    public TvHeaderPresenter(OnItemClickListener onItemClickListener, InputSourceManager inputSource) {
        super(onItemClickListener);
        this.inputSource = inputSource;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        Holder holder = new Holder(this, viewGroup, R.layout.row_content_tv_header, inputSource);
        iTvHeader = holder;
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object o) {

    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }

    @Override
    public void signalUpdate() {
        if (iTvHeader != null) {
            iTvHeader.signalUpdate();
        }
    }

    private static class Holder extends BaseViewHolder<HotFunctionModel> implements ITvHeader {

        private final VerticalGridView signalSourceListView;
        private final VerticalGridView systemFunctionListView;
        private final IRowSignalSourceProvider rowDataProvider;

        public Holder(OnItemClickListener listener, ViewGroup viewGroup, int layoutId, InputSourceManager inputSource) {
            super(listener, viewGroup, layoutId);

            //input source or live channel
            signalSourceListView = (VerticalGridView) view.findViewById(R.id.vtl_view_source_list);
            if (new TvConfig(view.getContext()).isTvFeture()) {
                rowDataProvider = new InputRow(inputSource);
            } else {
                rowDataProvider = new TvRow(view.getContext());
            }
            initSignalSourceList(rowDataProvider);

            //system function
            systemFunctionListView = (VerticalGridView) view.findViewById(R.id.vtl_view_system_function);
            initSystemFunctionList();

            view.findViewById(R.id.border_view_holder).setOnClickListener(new View.OnClickListener() {
                final TvViewModel tvViewModel = new TvViewModel();

                @Override
                public void onClick(View view) {
                    onPresenterItemClick(view, tvViewModel);
                }
            });
        }

        private void initSignalSourceList(final IRowSignalSourceProvider rowDataProvider) {
            signalSourceListView.setAdapter(new ItemBridgeAdapter(rowDataProvider.getListRowAdapter()));
            signalSourceListView.setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() {
                @Override
                public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
                    super.onChildViewHolderSelected(parent, child, position, subposition);
                    buildChildClickListener(child.itemView, rowDataProvider.getListRowAdapter().get(position));

                    //start childView's marquee effect
                    BorderEffectLayout borderEffectLayout = (BorderEffectLayout) child.itemView.findViewById(R.id.tv_item_source_info_parent);
                    final TextView tvSourceInfo = (TextView) child.itemView.findViewById(R.id.tv_item_source_info);
                    if (borderEffectLayout != null) {
                        borderEffectLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View view, boolean gainFocus) {
                                tvSourceInfo.setSelected(gainFocus);
                            }
                        });
                    }
                }

                @Override
                public void onChildViewHolderSelectedAndPositioned(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
                    super.onChildViewHolderSelectedAndPositioned(parent, child, position, subposition);
                }
            });
        }

        private void initSystemFunctionList() {
            final int column = 2;
            systemFunctionListView.setNumColumns(column);
            int itemSpace = (int) view.getResources().getDimension(R.dimen.item_hot_function_space);
            systemFunctionListView.setVerticalSpacing(itemSpace);
            systemFunctionListView.setHorizontalSpacing(itemSpace);
            final FunctionRow functionRow = new FunctionRow(view.getContext());
            systemFunctionListView.setAdapter(new ItemBridgeAdapter(functionRow.getListRowAdapter()));
            systemFunctionListView.setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() {
                @Override
                public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
                    super.onChildViewHolderSelected(parent, child, position, subposition);
                    if (position % column == 0) {
                        child.itemView.setNextFocusLeftId(signalSourceListView.getId());
                    } else {
                        child.itemView.setNextFocusLeftId(View.NO_ID);
                    }
                    buildChildClickListener(child.itemView, functionRow.getListRowAdapter().get(position));
                }

                @Override
                public void onChildViewHolderSelectedAndPositioned(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
                    super.onChildViewHolderSelectedAndPositioned(parent, child, position, subposition);
                }
            });
        }

        @Override
        public void bindData(HotFunctionModel hotFunctionModel) {

        }

        private void buildChildClickListener(View view, final Object model) {
            if (view != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPresenterItemClick(v, model);
                    }
                });
            }
        }

        @Override
        public void signalUpdate() {
            if (rowDataProvider != null) {
                rowDataProvider.update();
            }
        }
    }

}
