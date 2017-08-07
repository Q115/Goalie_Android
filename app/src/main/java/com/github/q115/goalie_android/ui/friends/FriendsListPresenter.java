package com.github.q115.goalie_android.ui.friends;

import android.support.annotation.NonNull;

import com.github.q115.goalie_android.ui.BasePresenter;

/**
 * Created by Qi on 8/6/2017.
 */

public class FriendsListPresenter implements BasePresenter {
    private final FriendsListView mFriendListView;

    public FriendsListPresenter(@NonNull FriendsListView friendListView) {
        mFriendListView = friendListView;
        mFriendListView.setPresenter(this);
    }

    public void start() {
    }
}
