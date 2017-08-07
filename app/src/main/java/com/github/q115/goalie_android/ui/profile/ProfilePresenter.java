package com.github.q115.goalie_android.ui.profile;

import android.support.annotation.NonNull;

import com.github.q115.goalie_android.ui.BasePresenter;
import com.github.q115.goalie_android.ui.login.LoginView;

/**
 * Created by Qi on 8/6/2017.
 */

public class ProfilePresenter implements BasePresenter {
    private final ProfileView mProfileView;

    public ProfilePresenter(@NonNull ProfileView profileView) {
        mProfileView = profileView;
        mProfileView.setPresenter(this);
    }

    public void start() {
    }
}
