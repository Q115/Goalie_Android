package com.github.q115.goalie_android.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.GoalFeed;
import com.github.q115.goalie_android.models.Goal_Table;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.services.AlarmService;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;

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

public class GoalHelper {
    private static GoalHelper mInstance;

    private ArrayList<Goal> mMyRequests;
    private ArrayList<GoalFeed> mFeeds;

    public static synchronized GoalHelper getInstance() {
        if (mInstance == null) {
            mInstance = new GoalHelper();
        }

        return mInstance;
    }

    public void initialize() {
        mMyRequests = new ArrayList<>();
        mFeeds = new ArrayList<>();
    }

    public ArrayList<Goal> getRequests() {
        return mMyRequests;
    }

    public void setRequests(ArrayList<Goal> requests) {
        mMyRequests = requests;
    }

    public ArrayList<GoalFeed> getFeeds() {
        return mFeeds;
    }

    public void setFeeds(ArrayList<GoalFeed> feeds) {
        mFeeds = feeds;
    }

    public boolean addGoal(Goal goal) {
        try {
            User user = UserHelper.getInstance().getAllContacts().get(goal.createdByUsername);
            if (user == null) {
                UserHelper.getInstance().addUser(new User(goal.createdByUsername));
                user = UserHelper.getInstance().getAllContacts().get(goal.createdByUsername);
            }

            if (goal.goalCompleteResult.isActive())
                user.activeGoals.put(goal.guid, goal);
            else
                user.finishedGoals.put(goal.guid, goal);

            if (!user.username.equals(UserHelper.getInstance().getOwnerProfile().username)) {
                mMyRequests.add(goal);
            }

            goal.activityDate = System.currentTimeMillis();
            goal.save();

            return true;
        } catch (Exception ex) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.UserHelper, "Error adding goal: " + ex.toString());
            return false;
        }
    }

    public boolean deleteGoal(String guid) {
        try {
            User owner = UserHelper.getInstance().getOwnerProfile();
            owner.activeGoals.remove(guid);

            SQLite.delete().from(Goal.class).where(Goal_Table.guid.eq(guid)).execute();
            return true;
        } catch (Exception ex) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.UserHelper, "Error adding goal: " + ex.toString());
            return false;
        }
    }

    public boolean modifyGoal(Goal goal) {
        try {
            goal.activityDate = System.currentTimeMillis();

            if (goal.goalCompleteResult != Goal.GoalCompleteResult.Pending
                    && goal.goalCompleteResult != Goal.GoalCompleteResult.Ongoing) {
                User user = UserHelper.getInstance().getAllContacts().get(goal.createdByUsername);
                if (user == null) {
                    UserHelper.getInstance().addUser(new User(goal.createdByUsername));
                    user = UserHelper.getInstance().getAllContacts().get(goal.createdByUsername);
                }

                user.activeGoals.remove(goal.guid);
                user.finishedGoals.put(goal.guid, goal);
            }

            goal.update();
            return true;
        } catch (Exception ex) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.UserHelper, "Error adding goal: " + ex.toString());
            return false;
        }
    }

    public void cancelAlarm(String guid, Context context) {
        Intent intent = AlarmService.newIntent(context, guid);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, guid.hashCode(), intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(pendingIntent);
    }
}
