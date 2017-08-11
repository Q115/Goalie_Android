package com.github.q115.goalie_android.ui.requests;

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
import com.github.q115.goalie_android.ui.my_goals.MyGoalsPresenter;
import com.github.q115.goalie_android.ui.my_goals.MyGoalsRecycler;

public class RequestsFragment extends Fragment implements RequestsView {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_requests, container, false);
        RecyclerView requestList = rootView.findViewById(R.id.request_list);
        requestList.setLayoutManager(new LinearLayoutManager(getContext()));
        requestList.setAdapter(new RequestsRecycler(getActivity()));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
       // mMyGoalsPresenter.start();
    }

    @Override
    public void setPresenter(RequestsPresenter presenter) {
        //mMyGoalsPresenter = presenter;
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

        ((MainActivity)getActivity()).reloadAll();
    }

    @Override
    public void reload() {
        //TODO
    }
}
