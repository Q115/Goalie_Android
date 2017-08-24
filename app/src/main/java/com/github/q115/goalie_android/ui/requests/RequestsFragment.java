package com.github.q115.goalie_android.ui.requests;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.ui.GoalsDetailedDialog;
import com.github.q115.goalie_android.ui.MainActivity;

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
public class RequestsFragment extends Fragment implements View.OnTouchListener, RequestsView {
    private RequestsPresenter mRequestsPresenter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isRefresherEnabled;
    private RecyclerView mRequestList;
    private TextView mEmptyMsg;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_requests, container, false);
        mRequestList = rootView.findViewById(R.id.request_list);
        mRequestList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRequestList.setHasFixedSize(true);
        mRequestList.setAdapter(new RequestsRecycler(getActivity(), mRequestsPresenter));
        mEmptyMsg = rootView.findViewById(R.id.empty);
        showEmptyWhenNecessary();

        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRequestsPresenter.onRefresherRefresh();
            }
        });
        mSwipeRefreshLayout.setEnabled(true);

        if (mRequestsPresenter != null)
            ((MainActivity) getActivity()).attachRequestsPresenter(mRequestsPresenter);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRequestsPresenter.start();
    }

    @Override
    public void setPresenter(RequestsPresenter presenter) {
        mRequestsPresenter = presenter;
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        if (v.getId() == mRequestList.getId()) {
            switch (e.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    isRefresherEnabled = true;
                    break;
                default:
                    break;
            }

            if (isRefresherEnabled) {
                RecyclerView DailyPondersList = (RecyclerView) v;
                LinearLayoutManager layoutManager = ((LinearLayoutManager) DailyPondersList.getLayoutManager());
                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                int topRowVerticalPosition = (DailyPondersList.getChildCount() == 0) ? 0 : DailyPondersList.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(firstVisiblePosition <= 0 && topRowVerticalPosition >= 0);
            }
        }
        return false;
    }

    public void showDialog(String title, String end, String start, String reputation, String encouragment,
                           String referee, Bitmap profileImage, Goal.GoalCompleteResult goalCompleteResult, String guid) {
        GoalsDetailedDialog detailedDialog = new GoalsDetailedDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isMyGoal", false);
        bundle.putString("title", title);
        bundle.putString("end", end);
        bundle.putString("start", start);
        bundle.putString("reputation", reputation);
        bundle.putString("referee", referee);
        bundle.putString("encouragement", encouragment);
        bundle.putParcelable("profile", profileImage);
        bundle.putSerializable("goalCompleteResult", goalCompleteResult);
        bundle.putString("guid", guid);
        detailedDialog.setArguments(bundle);
        detailedDialog.setTargetFragment(this, Constants.RESULT_MY_GOAL_DIALOG);
        detailedDialog.show(getActivity().getSupportFragmentManager(), "GoalsDetailedDialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RESULT_MY_GOAL_DIALOG && resultCode == RESULT_OK && data != null) {
            int goalInt = Integer.parseInt(data.getStringExtra("goalCompleteResultInt"));
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
            reload();
        }
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

    @Override
    public void reload() {
        ((RequestsRecycler) mRequestList.getAdapter()).notifyDataSetHasChanged();
        showEmptyWhenNecessary();
    }

    private void showEmptyWhenNecessary() {
        if (mEmptyMsg != null && mRequestList != null) {
            if (mRequestList.getAdapter().getItemCount() == 0) {
                mRequestList.setVisibility(View.GONE);
                mEmptyMsg.setVisibility(View.VISIBLE);
            } else {
                mRequestList.setVisibility(View.VISIBLE);
                mEmptyMsg.setVisibility(View.GONE);
            }
        }
    }
}
