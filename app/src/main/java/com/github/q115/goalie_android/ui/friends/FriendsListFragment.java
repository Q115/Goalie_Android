package com.github.q115.goalie_android.ui.friends;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.models.User;

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
        friendsList.setAdapter(new FriendsListRecycler(getActivity()));

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
        reload(false);
    }

    @Override
    public void setPresenter(FriendsListPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onAddContactDialog(User user) {
        if (getView() != null) {
            RecyclerView friendsList = getView().findViewById(R.id.friends_list);
            FriendsListRecycler friendsRecycler = (FriendsListRecycler) friendsList.getAdapter();
            friendsRecycler.addUserToList(user);
        }
    }

    @Override
    public void reload(boolean shouldReloadList) {
        if (getView() != null) {
            RecyclerView friendsList = getView().findViewById(R.id.friends_list);

            if (friendsList.getAdapter().getItemCount() == 0) {
                friendsList.setVisibility(View.GONE);
                getView().findViewById(R.id.empty).setVisibility(View.VISIBLE);
            } else {
                friendsList.setVisibility(View.VISIBLE);
                getView().findViewById(R.id.empty).setVisibility(View.GONE);
            }

            if (shouldReloadList)
                friendsList.getAdapter().notifyDataSetChanged();
        }
    }
}
