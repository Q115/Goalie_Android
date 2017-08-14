package com.github.q115.goalie_android.ui.requests;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.MainActivity;
import com.github.q115.goalie_android.ui.GoalsDetailedDialog;

public class RequestsFragment extends Fragment implements RequestsView {
    private RequestsPresenter mRequestsPresenter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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
        RecyclerView requestList = rootView.findViewById(R.id.request_list);
        requestList.setLayoutManager(new LinearLayoutManager(getContext()));
        requestList.setAdapter(new RequestsRecycler(getActivity(), mRequestsPresenter));
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

    public void showDialog(String title, String end, String start, String reputation, String encouragment, String referee, Bitmap profileImage) {
        GoalsDetailedDialog detailedDialog = new GoalsDetailedDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isMyGoal", false);
        bundle.putString("title", title);
        bundle.putString("end", end);
        bundle.putString("start", start);
        bundle.putString("reputation", reputation);
        bundle.putString("referee", referee);
        bundle.putString("encouragment", encouragment);
        bundle.putParcelable("profile", profileImage);
        detailedDialog.setArguments(bundle);
        detailedDialog.setTargetFragment(this, Constants.RESULT_MY_GOAL_DIALOG);
        detailedDialog.show(getActivity().getSupportFragmentManager(), "GoalsDetailedDialog");
    }

    @Override
    public void showRefresher(boolean shouldShow) {
        mSwipeRefreshLayout.setEnabled(shouldShow);
        if (getView() != null)
            getView().findViewById(R.id.info).setVisibility(shouldShow ? View.VISIBLE : View.GONE);

    }

    @Override
    public void syncError(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void syncSuccess() {
        //TODO

        ((MainActivity) getActivity()).reloadAll();
    }

    @Override
    public void reload() {
        //TODO
    }
}
