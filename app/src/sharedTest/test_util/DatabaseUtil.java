package test_util;

import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.GoalHelper;
import com.github.q115.goalie_android.utils.UserHelper;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

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

public class DatabaseUtil {
    public static void ReadDatabase() {
        try {
            List<User> users = SQLite.select().from(User.class).queryList();
            //populate contacts
            for (User user : users) {
                UserHelper.getInstance().getAllContacts().put(user.username, user);
            }

            //populate goals
            List<Goal> goals = SQLite.select().from(Goal.class).queryList();
            for (Goal goal : goals) {
                if (!goal.createdByUsername.equals(UserHelper.getInstance().getOwnerProfile().username)) {
                    GoalHelper.getInstance().getRequests().add(goal);
                } else if (UserHelper.getInstance().getAllContacts().get(goal.createdByUsername) != null) {
                    if (goal.goalCompleteResult == Goal.GoalCompleteResult.Ongoing || goal.goalCompleteResult == Goal.GoalCompleteResult.Pending)
                        UserHelper.getInstance().getAllContacts().get(goal.createdByUsername).activieGoals.add(goal);
                    else
                        UserHelper.getInstance().getAllContacts().get(goal.createdByUsername).finishedGoals.add(goal);
                }
            }
        } catch (OutOfMemoryError outOfMemoryException) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.MainApplication, "Too many entries: " + outOfMemoryException.toString());
        } catch (Exception ex) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.MainApplication, "Error loading database: " + ex.toString());
        }
    }
}
