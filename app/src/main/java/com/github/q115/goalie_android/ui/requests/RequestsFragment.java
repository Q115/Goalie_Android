package com.github.q115.goalie_android.ui.requests;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.my_goals.MyGoalsRecycler;

public class RequestsFragment extends Fragment {
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
}
