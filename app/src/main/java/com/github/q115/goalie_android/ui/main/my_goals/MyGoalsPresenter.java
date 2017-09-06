package com.github.q115.goalie_android.ui.main.my_goals;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.github.q115.goalie_android.https.RESTSync;
import com.github.q115.goalie_android.models.Goal;
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

public class MyGoalsPresenter implements BasePresenter {
    private final MyGoalsView mMyGoalsView;
    private boolean isFABOpen;

    public MyGoalsPresenter(@NonNull MyGoalsView myGoalsView) {
        isFABOpen = false;
        mMyGoalsView = myGoalsView;
        mMyGoalsView.setPresenter(this);
    }

    public void start() {
        if (isFABOpen)
            showFABMenu();
        else
            closeFABMenu();
    }

    public void toggleFAB() {
        if (!isFABOpen) {
            showFABMenu();
        } else {
            closeFABMenu();
        }
    }

    public void closeFABMenu() {
        isFABOpen = false;
        mMyGoalsView.closeFABMenu();
    }

    private void showFABMenu() {
        isFABOpen = true;
        mMyGoalsView.showFABMenu();
    }

    public boolean isFABOpen() {
        return isFABOpen;
    }

    public void onRefresherRefresh() {
        RESTSync sm = new RESTSync(UserHelper.getInstance().getOwnerProfile().username, PreferenceHelper.getInstance().getLastSyncedTimeEpoch());
        sm.setListener(new RESTSync.Listener() {
            @Override
            public void onSuccess() {
                mMyGoalsView.syncComplete(true, "");
            }

            @Override
            public void onFailure(String errMsg) {
                mMyGoalsView.syncComplete(false, errMsg);
            }
        });
        sm.execute();
    }

    public void showDialog(String title, String end, String start, String reputation, String encouragement,
                           String referee, Drawable profileImage, Goal.GoalCompleteResult goalCompleteResult, String guid) {
        mMyGoalsView.showDialog(title, end, start, reputation, encouragement, referee, profileImage, goalCompleteResult, guid);
    }

    public void reload() {
        mMyGoalsView.reload();
    }
}
