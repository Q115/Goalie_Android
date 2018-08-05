package com.github.q115.goalie_android.services;

/*
 * Copyright 2018 Qi Li
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.q115.goalie_android.ui.main.MainActivity;
import com.github.q115.goalie_android.utils.UserHelper;

public class AlarmService extends BroadcastReceiver {

    public static Intent newIntent(Context context, String guid) {
        Intent newIntent = new Intent(context, AlarmService.class);
        newIntent.putExtra("guid", guid);
        return newIntent;
    }

    public void onReceive(Context context, Intent intent) {
        String guid = intent.getStringExtra("guid");

        if (guid != null && UserHelper.getInstance().getOwnerProfile().activeGoals != null && UserHelper.getInstance().getOwnerProfile().activeGoals
                .containsKey(guid)) {
            Intent mainIntent = MainActivity.newIntent(context, 0);
            MessagingServiceUtil.showNotification("Goal Reminder",
                    "You have a deadline coming up. Just a friendly reminder to get it done :)", mainIntent, context);
        }
    }
}
