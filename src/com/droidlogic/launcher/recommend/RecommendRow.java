package com.droidlogic.launcher.recommend;

import android.content.Context;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import com.droidlogic.launcher.livetv.Channel;
import com.droidlogic.launcher.livetv.TVModelUtils;

import java.util.List;

public class RecommendRow {
    private Context mContext;
    private ArrayObjectAdapter mGridAdapter;
    private ArrayObjectAdapter mListRowAdapter = new ArrayObjectAdapter(new RecommendCardPresenter());

    public RecommendRow(Context context, ArrayObjectAdapter gridAdapter){
        mContext = context;
        mGridAdapter = gridAdapter;

        load();
    }

    public void load(){
        List<Channel> channels = TVModelUtils.getPreviewChannels(mContext.getContentResolver());

        for(Channel ch : channels){
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new RecommendCardPresenter());
            HeaderItem header = new HeaderItem(0, ch.getDisplayName());
            List<RecommendModel> models = RecommendModel.getProgramList(mContext, ch.getId());
            for(RecommendModel model:models){
                listRowAdapter.add(model);
            }
            mGridAdapter.add(new ListRow(header, listRowAdapter));
        }
    }

    public void update(){

    }
}
