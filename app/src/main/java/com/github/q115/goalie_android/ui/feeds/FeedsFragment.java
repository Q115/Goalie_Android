package com.github.q115.goalie_android.ui.feeds;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.MainActivity;

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
public class FeedsFragment extends Fragment implements FeedsView {
    private FeedsPresenter mFeedsPresenter;
    private RecyclerView mFeedsList;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public FeedsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FeedsFragment newInstance() {
        return new FeedsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_feeds, container, false);
        mFeedsList = rootView.findViewById(R.id.feed_list);
        mFeedsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mFeedsList.setHasFixedSize(true);
        mFeedsList.setAdapter(new FeedsRecycler(getActivity()));
        mFeedsList.addOnScrollListener(onScrollListener());

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mFeedsPresenter.onRefresherRefresh();
            }
        });
        mSwipeRefreshLayout.setEnabled(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFeedsPresenter != null)
            mFeedsPresenter.start();
    }

    @Override
    public void setPresenter(FeedsPresenter presenter) {
        mFeedsPresenter = presenter;
    }

    private RecyclerView.OnScrollListener onScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                try {
                    LinearLayoutManager layoutManager = ((LinearLayoutManager) mFeedsList.getLayoutManager());
                    int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                    int topRowVerticalPosition = (mFeedsList.getChildCount() == 0) ? 0 : mFeedsList.getChildAt(0).getTop();
                    mSwipeRefreshLayout.setEnabled(firstVisiblePosition <= 0 && topRowVerticalPosition >= 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void reload() {
        ((FeedsRecycler) mFeedsList.getAdapter()).notifyDataSetHasChanged();
    }

    @Override
    public void syncComplete(boolean isSuccessful, String errMsg) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (isSuccessful) {
            ((MainActivity) getActivity()).reloadAll();
        } else {
            Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
        }
    }
}