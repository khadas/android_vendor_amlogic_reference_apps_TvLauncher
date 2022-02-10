/*
 * Copyright (c) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.droidlogic.launcher.search;

import android.Manifest;
import android.content.Intent;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.SearchBar;
import androidx.leanback.widget.VerticalGridView;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.leanback.presenter.MainPresenterSelector;
import com.droidlogic.launcher.leanback.presenter.OnItemClickListener;
import com.droidlogic.launcher.leanback.presenter.content.SearchChannelPresenter;
import com.droidlogic.launcher.leanback.presenter.content.SearchProgramPresenter;
import com.droidlogic.launcher.livetv.Channel;
import com.droidlogic.launcher.livetv.PreviewProgram;
import com.droidlogic.launcher.livetv.Program;
import com.droidlogic.launcher.search.loader.ChannelLoader;
import com.droidlogic.launcher.search.loader.ProgramLoader;
import com.droidlogic.launcher.util.DensityTool;
import com.droidlogic.launcher.util.Logger;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static android.content.Intent.URI_INTENT_SCHEME;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class SearchFragment extends Fragment implements SearchBar.SearchBarListener, OnItemClickListener {

    private String mQuery;
    private SearchBar mSearchBar;
    private VerticalGridView gridView;
    static final int AUDIO_PERMISSION_REQUEST_CODE = 0;

    private final SearchBar.SearchBarPermissionListener mPermissionListener =
            () -> requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                    AUDIO_PERMISSION_REQUEST_CODE);

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == AUDIO_PERMISSION_REQUEST_CODE && permissions.length > 0) {
            if (permissions[0].equals(Manifest.permission.RECORD_AUDIO)
                    && grantResults[0] == PERMISSION_GRANTED) {
                startRecognition();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSearchBar = (SearchBar) view.findViewById(R.id.lb_search_bar);
        mSearchBar.setSearchBarListener(this);
        mSearchBar.setPermissionListener(mPermissionListener);
        gridView = (VerticalGridView) view.findViewById(R.id.search_grid_view);
        gridView.setVerticalSpacing(DensityTool.dp2px(30));
        loadQuery("");
    }

    private SpeechRecognizer mSpeechRecognizer;
    private boolean mIsPaused;
    private boolean mPendingStartRecognitionWhenPaused;

//    @Override
//    public void onResume() {
//        super.onResume();
//        mIsPaused = false;
//        if (null == mSpeechRecognizer) {
//            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
//            mSearchBar.setSpeechRecognizer(mSpeechRecognizer);
//        }
//        mSearchBar.startRecognition();
//    }
//
//    @Override
//    public void onPause() {
//        releaseRecognizer();
//        mIsPaused = true;
//        super.onPause();
//    }

    private void releaseRecognizer() {
//        if (null != mSpeechRecognizer) {
//            mSearchBar.setSpeechRecognizer(null);
//            mSpeechRecognizer.destroy();
//            mSpeechRecognizer = null;
//        }
    }

    public void startRecognition() {
//        if (mIsPaused) {
//            mPendingStartRecognitionWhenPaused = true;
//        } else {
//            mSearchBar.startRecognition();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelQuery();
    }

    private void loadQuery(String query) {
        this.mQuery = query;
        query(query);
    }

    private Disposable disposable;

    private void cancelQuery() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    private void query(final String query) {
        cancelQuery();
        disposable = Observable.zip(Observable.create(emitter -> {
            emitter.onNext(new ChannelLoader(getContext(), query).getDataList());
            emitter.onComplete();
        }), Observable.create(emitter -> {
            emitter.onNext(new ProgramLoader(getContext(), query).getDataList());
            emitter.onComplete();
        }), (BiFunction<List<Channel>, List<Program>, ArrayObjectAdapter>) (channels, previewPrograms) -> {
            ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new MainPresenterSelector(SearchFragment.this));
            if (channels.size() > 0) {
                ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new SearchChannelPresenter());
                arrayObjectAdapter.addAll(0, channels);
                ListRow listRow = new ListRow(new HeaderItem(getString(R.string.search_result_channel)), arrayObjectAdapter);
                rowsAdapter.add(listRow);
            }
            if (previewPrograms.size() > 0) {
                ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new SearchProgramPresenter());
                arrayObjectAdapter.addAll(0, previewPrograms);
                ListRow listRow = new ListRow(new HeaderItem(getString(R.string.search_result_program)), arrayObjectAdapter);
                rowsAdapter.add(listRow);
            }
            return rowsAdapter;
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(rowsAdapter -> {
            gridView.setAdapter(new ItemBridgeAdapter(rowsAdapter));
        }, throwable -> Logger.i("search--throwable--" + Thread.currentThread() + "\t" + throwable));
    }

    @Override
    public void onSearchQueryChange(String s) {
        loadQuery(s);
    }

    @Override
    public void onSearchQuerySubmit(String s) {
        loadQuery(s);
    }

    @Override
    public void onKeyboardDismiss(String s) {

    }

    @Override
    public void onPresenterItemClick(View view, Object item) {
        if (item instanceof Channel) {
            Channel channel = (Channel) item;
            String appLinkIntentUri = channel.getAppLinkIntentUri();
//            if (TextUtils.isEmpty(appLinkIntentUri)) {
//                lunchTv(channel.getId());
//            } else {
//                lunchApp(appLinkIntentUri);
//            }
            lunchTv(channel.getId());
        } else if (item instanceof PreviewProgram) {
            PreviewProgram program = (PreviewProgram) item;
            String intentUri = program.getIntentUri();
            if (TextUtils.isEmpty(intentUri)) {
                lunchTv(program.getChannelId());
            } else {
                lunchApp(intentUri);
            }
        } else if (item instanceof Program) {
            Program program = (Program) item;
            lunchTv(program.getChannelId());
        }
    }

    private void lunchTv(long channelId) {
        try {
            Uri channelUri = TvContract.buildChannelUri(channelId);
            Intent intent = new Intent(Intent.ACTION_VIEW, channelUri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void lunchApp(String intentUri) {
        try {
            Intent intent = Intent.parseUri(intentUri, URI_INTENT_SCHEME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
