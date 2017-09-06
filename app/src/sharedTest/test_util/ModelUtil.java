package test_util;

import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.GoalFeed;
import com.github.q115.goalie_android.models.User;

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
public class ModelUtil {
    public static boolean isGoalEqual(Goal goalA, Goal goalB) {
        boolean isEqual;
        isEqual = goalA.guid.equals(goalB.guid);
        isEqual &= goalA.createdByUsername.equals(goalB.createdByUsername);
        isEqual &= goalA.title.equals(goalB.title);
        isEqual &= goalA.startDate == goalB.startDate;
        isEqual &= goalA.endDate == goalB.endDate;
        isEqual &= goalA.encouragement.equals(goalB.encouragement);
        isEqual &= goalA.goalCompleteResult == goalB.goalCompleteResult;
        isEqual &= goalA.wager == goalB.wager;
        isEqual &= goalA.referee.equals(goalB.referee);
        isEqual &= goalA.activityDate == goalB.activityDate;
        return isEqual;
    }

    public static boolean isUserEqual(User userA, User userB) {
        boolean isEqual;
        isEqual = userA.username.equals(userB.username);
        isEqual &= userA.bio.equals(userB.bio);
        isEqual &= userA.lastPhotoModifiedTime == userB.lastPhotoModifiedTime;
        isEqual &= userA.reputation == userB.reputation;
        isEqual &= (userA.profileBitmapImage != null && userB.profileBitmapImage != null)
                || userA.profileBitmapImage == userB.profileBitmapImage;
        isEqual &= userA.activeGoals == userB.activeGoals
                || userA.activeGoals.size() == userB.activeGoals.size();
        isEqual &= userA.finishedGoals == userB.finishedGoals
                || userA.finishedGoals.size() == userB.finishedGoals.size();

        if (userA.activeGoals != null && userB.activeGoals != null) {
            isEqual &= userA.activeGoals.size() == userB.activeGoals.size();

            for (String string : userA.activeGoals.keySet()) {
                isEqual &= isGoalEqual(userA.activeGoals.get(string), userB.activeGoals.get(string));
            }
        }
        if (userA.finishedGoals != null && userB.finishedGoals != null) {
            isEqual &= userA.finishedGoals.size() == userB.finishedGoals.size();

            for (String string : userA.finishedGoals.keySet()) {
                isEqual &= isGoalEqual(userA.finishedGoals.get(string), userB.finishedGoals.get(string));
            }
        }
        return isEqual;
    }

    public static boolean isGoalFeedEqual(GoalFeed feedA, GoalFeed feedB) {
        boolean isEqual;
        isEqual = feedA.guid.equals(feedB.guid);
        isEqual &= feedA.createdUsername.equals(feedB.createdUsername);
        isEqual &= feedA.goalCompleteResult == feedB.goalCompleteResult;
        isEqual &= feedA.wager == feedB.wager;
        isEqual &= feedA.hasVoted == feedB.hasVoted;
        isEqual &= feedA.upvoteCount == feedB.upvoteCount;

        return isEqual;
    }
}