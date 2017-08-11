package com.github.q115.goalie_android.ui.profile;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.github.q115.goalie_android.https.RESTUploadPhoto;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.ui.BasePresenter;
import com.github.q115.goalie_android.utils.UserHelper;

/**
 * Created by Qi on 8/6/2017.
 */

public class ProfilePresenter implements BasePresenter {
    private final ProfileView mProfileView;
    private final String mUsername;

    public ProfilePresenter(String username, @NonNull ProfileView profileView) {
        mUsername = username;
        mProfileView = profileView;
        mProfileView.setPresenter(this);
    }

    public void start() {
        if (mUsername.equals(UserHelper.getInstance().getOwnerProfile().username)) {
            mProfileView.setupForOwner(true);
        } else {
            mProfileView.setupForOwner(false);
        }

        User user = UserHelper.getInstance().getAllContacts().get(mUsername);
        mProfileView.setupView(user.username, user.bio, user.reputation, user.finishedGoals);
    }

    public void newProfileImageSelected(final Bitmap image) {
        mProfileView.updateProgress(true);

        RESTUploadPhoto sm = new RESTUploadPhoto(image, mUsername);
        sm.setListener(new RESTUploadPhoto.Listener() {
            @Override
            public void onSuccess() {
                mProfileView.updateProgress(false);
                mProfileView.uploadSuccess(image);
            }

            @Override
            public void onFailure(String errMsg) {
                mProfileView.updateProgress(false);
                mProfileView.showUploadError(errMsg);
            }
        });
        sm.execute();
    }
}
