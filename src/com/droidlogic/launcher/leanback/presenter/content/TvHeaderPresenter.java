package com.droidlogic.launcher.leanback.presenter.content;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.OnChildViewHolderSelectedListener;
import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.RecyclerView;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.function.FunctionRow;
import com.droidlogic.launcher.input.InputModel;
import com.droidlogic.launcher.input.InputRow;
import com.droidlogic.launcher.input.InputSourceManager;
import com.droidlogic.launcher.leanback.model.HotFunctionModel;
import com.droidlogic.launcher.leanback.model.IRowSignalSourceProvider;
import com.droidlogic.launcher.leanback.model.ITvHeader;
import com.droidlogic.launcher.leanback.presenter.BasePresenter;
import com.droidlogic.launcher.leanback.presenter.BaseViewHolder;
import com.droidlogic.launcher.leanback.presenter.OnItemClickListener;
import com.droidlogic.launcher.leanback.view.BorderEffectLayout;
import com.droidlogic.launcher.leanback.view.LeanBarSeekBar;
import com.droidlogic.launcher.livetv.MediaModel;
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
        private final LeanBarSeekBar scrollBar;

        public Holder(OnItemClickListener listener, ViewGroup viewGroup, int layoutId, InputSourceManager inputSource) {
            super(listener, viewGroup, layoutId);

            //input source or live channel
            signalSourceListView = (VerticalGridView) view.findViewById(R.id.vtl_view_source_list);
            if (new TvConfig(view.getContext()).isTvFeature()) {
                rowDataProvider = new InputRow(inputSource);
            } else {
                rowDataProvider = new TvRow(view.getContext());
            }
            signalSourceListView.post(new Runnable() {
                @Override
                public void run() {
                    initSignalSourceList(rowDataProvider);
                }
            });

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

            scrollBar = (LeanBarSeekBar) view.findViewById(R.id.scroll_bar);

        }

        private void initSignalSourceList(final IRowSignalSourceProvider rowDataProvider) {
            ItemBridgeAdapter signalItemBridgeAdapter = new ItemBridgeAdapter(rowDataProvider.getListRowAdapter());
            signalSourceListView.setAdapter(signalItemBridgeAdapter);
            signalItemBridgeAdapter.setAdapterListener(new ItemBridgeAdapter.AdapterListener(){
                @Override
                public void onCreate(ItemBridgeAdapter.ViewHolder viewHolder) {
                    viewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() ==  MotionEvent.ACTION_DOWN) {
                                buildChildClickListener(viewHolder.itemView, rowDataProvider.getListRowAdapter().get(viewHolder.getPosition()),  rowDataProvider.getListRowAdapter());
                            }
                            return false;
                        }
                    });
                }
            });
            signalSourceListView.setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() {
                @Override
                public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
                    super.onChildViewHolderSelected(parent, child, position, subposition);
                    if (child != null) {
                        scrollBar.update(position, rowDataProvider.getListRowAdapter().size() - 1);
                        buildChildClickListener(child.itemView, rowDataProvider.getListRowAdapter().get(position), rowDataProvider.getListRowAdapter());
                        //start childView's marquee effect
                        BorderEffectLayout borderEffectLayout = (BorderEffectLayout) child.itemView.findViewById(R.id.tv_item_source_info_parent);
                        final TextView tvSourceInfo = (TextView) child.itemView.findViewById(R.id.tv_item_source_info);
                        if (borderEffectLayout != null) {
                            borderEffectLayout.setOnFocusChangeListener((view, gainFocus) -> tvSourceInfo.setSelected(gainFocus));
                        }
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
            ItemBridgeAdapter systemItemBridgeAdapter = new ItemBridgeAdapter(functionRow.getListRowAdapter());
            systemFunctionListView.setAdapter(systemItemBridgeAdapter);
            systemItemBridgeAdapter.setAdapterListener(new ItemBridgeAdapter.AdapterListener(){
                @Override
                public void onCreate(ItemBridgeAdapter.ViewHolder viewHolder) {
                    viewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() ==  MotionEvent.ACTION_DOWN) {
                                buildChildClickListener(viewHolder.itemView, functionRow.getListRowAdapter().get(viewHolder.getPosition()), null);
                            }
                            return false;
                        }
                    });
                }
            });
            systemFunctionListView.setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() {
                @Override
                public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
                    super.onChildViewHolderSelected(parent, child, position, subposition);
                    if (position % column == 0) {
                        child.itemView.setNextFocusLeftId(signalSourceListView.getId());
                    } else {
                        child.itemView.setNextFocusLeftId(View.NO_ID);
                    }
                    buildChildClickListener(child.itemView, functionRow.getListRowAdapter().get(position), null);
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

        private void buildChildClickListener(View view, final Object model, final ArrayObjectAdapter arrayObjectAdapter) {
            if (view != null) {
                view.setOnClickListener(v -> {
                    onPresenterItemClick(v, model);
                    if (arrayObjectAdapter != null) {
                        for (int i = 0; i < arrayObjectAdapter.size(); i++) {
                            if (arrayObjectAdapter.get(i) instanceof InputModel) {
                                InputModel inputModel = (InputModel) arrayObjectAdapter.get(i);
                                inputModel.setSignalInput(inputModel == model);
                                arrayObjectAdapter.notifyArrayItemRangeChanged(0, arrayObjectAdapter.size());
                            } else if (arrayObjectAdapter.get(i) instanceof MediaModel) {
                                MediaModel mediaModel = (MediaModel) arrayObjectAdapter.get(i);
                                mediaModel.setPlaying(mediaModel == model);
                                arrayObjectAdapter.notifyArrayItemRangeChanged(0, arrayObjectAdapter.size());
                            }
                        }
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
