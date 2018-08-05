package com.github.q115.goalie_android.ui.profile;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.github.q115.goalie_android.https.RESTUploadPhoto;
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

public class ProfileFragmentPresenter implements BasePresenter {
    private final ProfileFragmentView mProfileView;
    private final String mUsername;

    public ProfileFragmentPresenter(String username, @NonNull ProfileFragmentView profileView) {
        mUsername = username;
        mProfileView = profileView;
        mProfileView.setPresenter(this);
    }

    public void start() {
        User user = UserHelper.getInstance().getAllContacts().get(mUsername);
        if (user != null)
            mProfileView.setupView(user.username, user.bio, user.reputation);

        if (mUsername.equals(UserHelper.getInstance().getOwnerProfile().username)) {
            mProfileView.toggleOwnerSpecificFeatures(true);
        } else {
            mProfileView.toggleOwnerSpecificFeatures(false);
        }
    }

    public void newProfileImageSelected(final Bitmap image) {
        if (image == null)
            return;

        mProfileView.updateProgress(true);
        RESTUploadPhoto sm = new RESTUploadPhoto(image, mUsername);
        sm.setListener(new RESTUploadPhoto.Listener() {
            @Override
            public void onSuccess() {
                mProfileView.updateProgress(false);
                mProfileView.uploadComplete(true, image, null);
            }

            @Override
            public void onFailure(String errMsg) {
                mProfileView.updateProgress(false);
                mProfileView.uploadComplete(false, null, errMsg);
            }
        });
        sm.execute();
    }
}
