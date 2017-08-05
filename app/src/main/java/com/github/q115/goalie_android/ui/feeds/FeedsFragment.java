package com.github.q115.goalie_android.ui.feeds;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.my_goals.MyGoalsRecycler;

public class FeedsFragment extends Fragment {
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
        RecyclerView feedList = rootView.findViewById(R.id.feed_list);
        feedList.setLayoutManager(new LinearLayoutManager(getContext()));
        feedList.setAdapter(new FeedsRecycler(getActivity()));

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}