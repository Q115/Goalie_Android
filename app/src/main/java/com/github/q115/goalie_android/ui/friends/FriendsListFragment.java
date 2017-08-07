package com.github.q115.goalie_android.ui.friends;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.q115.goalie_android.R;

/**
 * Created by Qi on 8/4/2017.
 */

public class FriendsListFragment extends Fragment implements FriendsListView {
    private FriendsListPresenter mPresenter;

    public FriendsListFragment() {
    }

    public static FriendsListFragment newInstance() {
        return new FriendsListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);

        RecyclerView friendsList = rootView.findViewById(R.id.friends_list);
        friendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsList.setAdapter(new FriendsRecycler(getActivity()));

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(FriendsListPresenter presenter) {
        mPresenter = presenter;
    }
}
