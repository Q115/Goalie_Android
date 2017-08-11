package com.github.q115.goalie_android.ui;

import android.support.annotation.NonNull;

import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.UUID;

/**
 * Created by Qi on 8/4/2017.
 */

public class MainActivityPresenter implements BasePresenter {
    private final MainActivityView mMainActivityView;

    public MainActivityPresenter(@NonNull MainActivityView mainActivityView) {
        mMainActivityView = mainActivityView;
        mMainActivityView.setPresenter(this);
    }

    public void start() {
        String accountUsername = PreferenceHelper.getInstance().getAccountUsername();
        if (accountUsername == null || accountUsername.isEmpty()) {
            mMainActivityView.showLogin();
        }
    }
}
