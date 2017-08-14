package com.github.q115.goalie_android.ui.feeds;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.models.GoalFeed;

import java.util.ArrayList;

public class FeedsFragment extends Fragment implements FeedsView {
    private FeedsPresenter mFeedsPresenter;
    private ProgressDialog mProgressDialog;
    private RecyclerView mFeedsList;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_feeds, container, false);
        mFeedsList = rootView.findViewById(R.id.feed_list);
        mFeedsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mFeedsList.setAdapter(new FeedsRecycler(getActivity()));

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.connecting));
        mProgressDialog.setCancelable(false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFeedsPresenter.start();
    }

    @Override
    public void setPresenter(FeedsPresenter presenter) {
        mFeedsPresenter = presenter;
    }

    @Override
    public void showRefresher(boolean shouldShow) {
    }

    @Override
    public void syncError(String msg) {
    }

    @Override
    public void syncSuccess() {
    }

    @Override
    public void reload() {

    }

    @Override
    public void getFeedComplete(boolean isSuccessful, String errMsg) {
        mProgressDialog.cancel();
        if (isSuccessful) {
            ((FeedsRecycler) mFeedsList.getAdapter()).notifyDataSetChanged(mFeedsPresenter.getGoalFeedArrayList());
        } else if (mProgressDialog.isShowing()) {
            Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
        }
    }
}