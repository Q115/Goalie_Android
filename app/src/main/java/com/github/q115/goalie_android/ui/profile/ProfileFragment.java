package com.github.q115.goalie_android.ui.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.friends.FriendsListFragment;
import com.github.q115.goalie_android.ui.friends.FriendsRecycler;

/**
 * Created by Qi on 8/4/2017.
 */

public class ProfileFragment extends Fragment {
    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        RecyclerView friendsList = rootView.findViewById(R.id.profile_activity_list);
        friendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsList.setAdapter(new ProfileActivityRecycler(getActivity()));

        return rootView;
    }
}
