package com.github.q115.goalie_android.ui.friends;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.github.q115.goalie_android.https.RESTGetPhoto;
import com.github.q115.goalie_android.https.RESTGetUserInfo;
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

    public void onAddContactDialogComplete(String username) {
        getPhotoForUser(username);
    }

    public void refresh(final String username) {
        mFriendListView.updateProgress(true);
        RESTGetUserInfo sm2 = new RESTGetUserInfo(username);
        sm2.setListener(new RESTGetUserInfo.Listener() {
            @Override
            public void onSuccess() {
                getPhotoForUser(username);
            }

            @Override
            public void onFailure(String errMsg) {
                getPhotoForUser(username);
            }
        });
        sm2.execute();
    }

    private void getPhotoForUser(String username) {
        RESTGetPhoto sm = new RESTGetPhoto(username);
        sm.setListener(new RESTGetPhoto.Listener() {
            @Override
            public void onSuccess(Bitmap photo) {
                mFriendListView.reload();
                mFriendListView.updateProgress(false);
            }

            @Override
            public void onFailure(String errMsg) {
                mFriendListView.reload();
                mFriendListView.updateProgress(false);
            }
        });
        sm.execute();
    }

    public void delete(String username) {
        UserHelper.getInstance().deleteUser(username);
        mFriendListView.reload();
    }
}
