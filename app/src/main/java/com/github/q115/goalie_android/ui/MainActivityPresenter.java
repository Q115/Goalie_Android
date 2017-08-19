package com.github.q115.goalie_android.ui;

import android.support.annotation.NonNull;

import com.github.q115.goalie_android.https.RESTSync;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

/**
 * Created by Qi on 8/4/2017.
 */

public class MainActivityPresenter implements BasePresenter {
    private final MainActivityView mMainActivityView;
    private static boolean isSyncedOnStartup = false;

    public MainActivityPresenter(@NonNull MainActivityView mainActivityView) {
        mMainActivityView = mainActivityView;
        mMainActivityView.setPresenter(this);
    }

    public void start() {
        String accountUsername = PreferenceHelper.getInstance().getAccountUsername();
        if (accountUsername == null || accountUsername.isEmpty()) {
            mMainActivityView.showLogin();
        } else if (!isSyncedOnStartup) {
            isSyncedOnStartup = true;
            RESTSync sm = new RESTSync(UserHelper.getInstance().getOwnerProfile().username, PreferenceHelper.getInstance().getLastSyncedTimeEpoch());
            sm.setListener(new RESTSync.Listener() {
                @Override
                public void onSuccess() {
                    mMainActivityView.reloadAll();
                }

                @Override
                public void onFailure(String errMsg) {
                }
            });
            sm.execute();
        }
    }
}
