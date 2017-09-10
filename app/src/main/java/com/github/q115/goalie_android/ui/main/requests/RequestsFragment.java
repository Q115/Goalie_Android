package com.github.q115.goalie_android.ui.main.requests;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.ui.main.BaseRefresherFragment;
import com.github.q115.goalie_android.ui.main.GoalsDetailedDialog;

import static android.app.Activity.RESULT_OK;

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
public class RequestsFragment extends BaseRefresherFragment implements RequestsView {
    private RequestsPresenter mRequestsPresenter;

    public RequestsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RequestsFragment newInstance() {
        return new RequestsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_requests, container, false);
        mRecyclerView = rootView.findViewById(R.id.request_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new RequestsRecycler(getActivity(), mRequestsPresenter));
        mRecyclerView.addOnScrollListener(onScrollListener());

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(onRefresherRefreshListener());
        mSwipeRefreshLayout.setEnabled(true);

        mEmptyMsg = rootView.findViewById(R.id.empty);
        showEmptyWhenNecessary();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mRequestsPresenter != null)
            mRequestsPresenter.start();
    }

    @Override
    public void setPresenter(RequestsPresenter presenter) {
        mRequestsPresenter = presenter;

        // reconnect presenter if needed.
        if (mRecyclerView != null && mRecyclerView.getAdapter() != null)
            ((RequestsRecycler) mRecyclerView.getAdapter()).setPresenter(mRequestsPresenter);
    }

    public void showDialog(String title, String end, String start, String reputation, String encouragment, String referee,
                           Drawable profileImage, Goal.GoalCompleteResult goalCompleteResult, String guid) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isMyGoal", false);
        bundle.putString("title", title);
        bundle.putString("end", end);
        bundle.putString("start", start);
        bundle.putString("reputation", reputation);
        bundle.putString("referee", referee);
        bundle.putString("encouragement", encouragment);
        if (profileImage instanceof RoundedBitmapDrawable)
            bundle.putParcelable("profile", ((RoundedBitmapDrawable) profileImage).getBitmap());
        else if (profileImage instanceof BitmapDrawable)
            bundle.putParcelable("profile", ((BitmapDrawable) profileImage).getBitmap());
        bundle.putSerializable("goalCompleteResult", goalCompleteResult);
        bundle.putString("guid", guid);

        GoalsDetailedDialog detailedDialog = new GoalsDetailedDialog();
        detailedDialog.setArguments(bundle);
        detailedDialog.setTargetFragment(this, Constants.RESULT_MY_GOAL_DIALOG);
        detailedDialog.show(getActivity().getSupportFragmentManager(), "GoalsDetailedDialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RESULT_MY_GOAL_DIALOG && resultCode == RESULT_OK && data != null) {
            int goalInt = Integer.parseInt(data.getStringExtra("goalCompleteResultInt"));
            if (goalInt < Goal.GoalCompleteResult.values().length) {
                switch (Goal.GoalCompleteResult.values()[goalInt]) {
                    case Ongoing:
                        Toast.makeText(getActivity(), getString(R.string.accepte_toast), Toast.LENGTH_SHORT).show();
                        break;
                    case Success:
                        Toast.makeText(getActivity(), getString(R.string.complete_toast), Toast.LENGTH_SHORT).show();
                        break;
                    case Failed:
                        Toast.makeText(getActivity(), getString(R.string.failed_toast), Toast.LENGTH_SHORT).show();
                        break;
                    case Cancelled:
                        Toast.makeText(getActivity(), getString(R.string.delete_toast), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            reload();
        }
    }

    @Override
    public void reload() {
        ((RequestsRecycler) mRecyclerView.getAdapter()).notifyDataSetHasChanged();
        showEmptyWhenNecessary();
    }
}
