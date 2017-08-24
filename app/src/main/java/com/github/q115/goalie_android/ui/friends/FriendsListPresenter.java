package com.github.q115.goalie_android.ui.friends;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.github.q115.goalie_android.https.RESTGetPhoto;
import com.github.q115.goalie_android.https.RESTGetUserInfo;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.ui.BasePresenter;
import com.github.q115.goalie_android.utils.UserHelper;

/*
 * Copyright 2017 Qi Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
