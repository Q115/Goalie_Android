package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.https.RESTNewGoal;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.Pair;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static test_util.RESTUtil.getTestUsername;
import static test_util.RESTUtil.getValidFriendUsername;

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
@RunWith(RobolectricTestRunner.class)
public class RESTNewGoalTest extends BaseRESTTest {

    @Test
    public void setNewGoal() throws Exception {
        String friendUsername = getValidFriendUsername();
        Pair<Integer, RESTNewGoal.Listener> pair = createAListener();

        Object[] array = UserHelper.getInstance().getOwnerProfile().activeGoals.values().toArray();
        int currentActivitySize = array.length;
        RESTNewGoal sm = new RESTNewGoal(username, "title", 12000, 120000, 55, "encouragement", friendUsername, true);
        sm.setListener(pair.second);
        sm.execute();

        while (!isOperationCompleteList.get(pair.first)) {
            Thread.sleep(1000);
        }

        verify(pair.second).onSuccess();

        User user = UserHelper.getInstance().getAllContacts().get(username);
        assertNotNull(user);
        assertEquals(user.username, username);
        assertEquals(user.activeGoals.size(), currentActivitySize + 1);

        array = UserHelper.getInstance().getOwnerProfile().activeGoals.values().toArray();
        Goal goal = (Goal)array[currentActivitySize];
        assertEquals(goal.createdByUsername, username);
        assertEquals(goal.title, "title");
        assertEquals(goal.startDate, 12000);
        assertEquals(goal.endDate, 120000);
        assertEquals(goal.wager, 55);
        assertEquals(goal.encouragement, "encouragement");
        assertEquals(goal.referee, friendUsername);
        assertEquals(goal.goalCompleteResult, Goal.GoalCompleteResult.Pending);
        assertEquals(UserHelper.getInstance().getOwnerProfile().reputation, 45);
    }

    @Test
    public void onResponseEmpty() throws Exception {
        String friendUsername = getTestUsername();
        User user = UserHelper.getInstance().getAllContacts().get(friendUsername);
        assertNull(user);

        Pair<Integer, RESTNewGoal.Listener> pair = createAListener();
        int currentActivitySize = UserHelper.getInstance().getOwnerProfile().activeGoals.size();

        RESTNewGoal sm = new RESTNewGoal(username, "title", 12000, 120000, 55, "encouragement", friendUsername, true);
        sm.setListener(pair.second);
        sm.onResponse("");

        verify(pair.second).onSuccess();

        User friendUser = UserHelper.getInstance().getAllContacts().get(friendUsername);
        assertNotNull(friendUser);

        User owner = UserHelper.getInstance().getAllContacts().get(username);
        assertNotNull(owner);
        assertEquals(owner.activeGoals.size(), currentActivitySize + 1);
    }

    private synchronized Pair<Integer, RESTNewGoal.Listener> createAListener() {
        final Integer index = isOperationCompleteList.size();
        isOperationCompleteList.add(false);

        RESTNewGoal.Listener listener = Mockito.spy(new RESTNewGoal.Listener() {
            @Override
            public void onSuccess() {
                isOperationCompleteList.set(index, true);
            }

            @Override
            public void onFailure(String errMsg) {
                isOperationCompleteList.set(index, true);
            }
        });

        return new Pair<>(index, listener);
    }
}