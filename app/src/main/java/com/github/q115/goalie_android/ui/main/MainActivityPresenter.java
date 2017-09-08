package com.github.q115.goalie_android.ui.main;

import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.https.RESTSync;
import com.github.q115.goalie_android.ui.BasePresenter;
import com.github.q115.goalie_android.utils.PreferenceHelper;
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
