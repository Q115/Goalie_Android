package com.github.q115.goalie_android.ui.main;

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

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.q115.goalie_android.https.RESTSync;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

public abstract class BaseRefresherFragment extends Fragment {
    protected RecyclerView mRecyclerView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected TextView mEmptyMsg;

    public BaseRefresherFragment() {
    }

    protected RecyclerView.OnScrollListener onScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (mRecyclerView != null && mSwipeRefreshLayout != null) {
                    LinearLayoutManager layoutManager = ((LinearLayoutManager) mRecyclerView.getLayoutManager());
                    int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                    int topRowVerticalPosition = mRecyclerView.getChildCount() == 0 ?
                            0 : mRecyclerView.getChildAt(0).getTop();
                    mSwipeRefreshLayout.setEnabled(firstVisiblePosition <= 0 && topRowVerticalPosition >= 0);
                }
            }
        };
    }

    protected SwipeRefreshLayout.OnRefreshListener onRefresherRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RESTSync sm = new RESTSync(UserHelper.getInstance().getOwnerProfile().username,
                        PreferenceHelper.getInstance().getLastSyncedTimeEpoch(), getActivity());
                sm.setListener(new RESTSync.Listener() {
                    @Override
                    public void onSuccess() {
                        syncComplete(true, "");
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        syncComplete(false, errMsg);
                    }
                });
                sm.execute();
            }
        };
    }

    private void syncComplete(boolean isSuccessful, String errMsg) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (isSuccessful) {
            if (getActivity() instanceof MainActivity)
                ((MainActivity) getActivity()).reloadAll();
        } else {
            Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
        }
    }

    protected void showEmptyWhenNecessary() {
        if (mEmptyMsg != null && mRecyclerView != null) {
            if (mRecyclerView.getAdapter().getItemCount() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyMsg.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyMsg.setVisibility(View.GONE);
            }
        }
    }
}
