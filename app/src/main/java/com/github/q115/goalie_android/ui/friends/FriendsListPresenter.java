package com.github.q115.goalie_android.ui.friends;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.https.RESTGetPhoto;
import com.github.q115.goalie_android.https.RESTGetUserInfo;
import com.github.q115.goalie_android.https.RESTUpdateGoal;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.ui.BasePresenter;
import com.github.q115.goalie_android.utils.UserHelper;

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

    public void onAddContactDialog(String username) {
        final User user = UserHelper.getInstance().getAllContacts().get(username);
        if (user != null) {
            mFriendListView.onAddContactDialog(user);
            mFriendListView.reload(false);

            //fetch user image if applicable
            RESTGetPhoto sm = new RESTGetPhoto(username);
            sm.setListener(new RESTGetPhoto.Listener() {
                @Override
                public void onSuccess(Bitmap photo3) {
                    mFriendListView.reload(true);
                }

                @Override
                public void onFailure(String errMsg) {
                }
            });
            sm.execute();
        }
    }

    public void refresh(String username) {
        RESTGetUserInfo sm2 = new RESTGetUserInfo(username);
        sm2.setListener(null);
        sm2.execute();

        RESTGetPhoto sm = new RESTGetPhoto(username);
        sm.setListener(new RESTGetPhoto.Listener() {
            @Override
            public void onSuccess(Bitmap photo3) {
                mFriendListView.reload(true);
            }

            @Override
            public void onFailure(String errMsg) {
                mFriendListView.reload(true);
            }
        });
        sm.execute();
    }

    public void delete(String username) {
        UserHelper.getInstance().deleteUser(username);
        mFriendListView.reload(true);
    }
}
