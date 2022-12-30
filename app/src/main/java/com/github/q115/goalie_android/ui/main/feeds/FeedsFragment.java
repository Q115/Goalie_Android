package com.github.q115.goalie_android.ui.main.feeds;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.main.BaseRefresherFragment;

/*
 * Copyright 2017 Qi Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class FeedsFragment extends BaseRefresherFragment implements FeedsView {
    private FeedsPresenter mPresenter;

    public FeedsFragment() {
    }

    public static FeedsFragment newInstance() {
        return new FeedsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_feeds, container, false);
        mRecyclerView = rootView.findViewById(R.id.feed_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new FeedsRecycler(getActivity()));
        mRecyclerView.addOnScrollListener(onScrollListener());

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(onRefresherRefreshListener());
        mSwipeRefreshLayout.setEnabled(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null)
            mPresenter.start();
    }

    @Override
    public void setPresenter(FeedsPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void reload() {
        ((FeedsRecycler) mRecyclerView.getAdapter()).notifyDataSetHasChanged();
    }
}