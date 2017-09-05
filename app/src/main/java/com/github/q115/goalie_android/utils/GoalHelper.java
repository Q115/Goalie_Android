package com.github.q115.goalie_android.utils;

import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.GoalFeed;
import com.github.q115.goalie_android.models.Goal_Table;
import com.github.q115.goalie_android.models.User;
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
    private ArrayList<Goal> mRequests;

    public ArrayList<Goal> getRequests() {
        return mRequests;
    }

    public void setRequests(ArrayList<Goal> requests) {
        mRequests = requests;
    }

    private ArrayList<GoalFeed> mFeeds;

    public ArrayList<GoalFeed> getFeeds() {
        return mFeeds;
    }

    public void setFeeds(ArrayList<GoalFeed> feeds) {
        mFeeds = feeds;
    }

    private static GoalHelper mInstance;

    public static synchronized GoalHelper getInstance() {
        if (mInstance == null) {
            mInstance = new GoalHelper();
        }

        return mInstance;
    }

    public void initialize() {
        mRequests = new ArrayList<>();
        mFeeds = new ArrayList<>();
    }

    public boolean addGoal(Goal goal) {
        try {
            User owner = UserHelper.getInstance().getOwnerProfile();
            if (!goal.createdByUsername.equals(owner.username)) {
                mRequests.add(goal);
            } else {
                if (goal.goalCompleteResult == Goal.GoalCompleteResult.Ongoing || goal.goalCompleteResult == Goal.GoalCompleteResult.Pending)
                    owner.activieGoals.add(goal);
                else
                    owner.finishedGoals.add(goal);
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
            for (int i = 0; i < owner.activieGoals.size(); i++) {
                Goal goal = owner.activieGoals.get(i);
                if (goal.guid.equals(guid)) {
                    owner.activieGoals.remove(i);
                    break;
                }
            }

            SQLite.delete().from(Goal.class).where(Goal_Table.guid.eq(guid)).execute();
            return true;
        } catch (Exception ex) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.UserHelper, "Error adding goal: " + ex.toString());
            return false;
        }
    }

    public boolean modifyGoal(Goal newGoal) {
        try {
            newGoal.activityDate = System.currentTimeMillis();
            newGoal.update();
            return true;
        } catch (Exception ex) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.UserHelper, "Error adding goal: " + ex.toString());
            return false;
        }
    }
}
