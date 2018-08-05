package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.https.RESTNewGoal;
import com.github.q115.goalie_android.https.RESTSync;
import com.github.q115.goalie_android.https.RESTUpdateGoal;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.utils.GoalHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.Pair;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

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
public class RESTUpdateGoalTest extends BaseRESTTest {
    private boolean isSettingUpGoal;

    @Test()
    public void acceptGoal() throws Exception {
        final Pair<Integer, RESTUpdateGoal.Listener> pair = createAListener();

        assertEquals(GoalHelper.getInstance().getRequests().size(), 0);
        setupGoal();
        assertEquals(GoalHelper.getInstance().getRequests().size(), 1);
        RESTUpdateGoal sm = new RESTUpdateGoal(username, GoalHelper.getInstance().getRequests().get(0).guid,
                Goal.GoalCompleteResult.Ongoing);
        sm.setListener(pair.second);
        sm.execute();

        while (!isOperationCompleteList.get(pair.first)) {
            Thread.sleep(1000);
        }

        verify(pair.second).onSuccess();
        assertEquals(GoalHelper.getInstance().getRequests().get(0).goalCompleteResult, Goal.GoalCompleteResult.Ongoing);
    }

    @Test()
    public void passGoal() throws Exception {
        final Pair<Integer, RESTUpdateGoal.Listener> pair = createAListener();

        assertEquals(GoalHelper.getInstance().getRequests().size(), 0);
        setupGoal();
        assertEquals(GoalHelper.getInstance().getRequests().size(), 1);
        RESTUpdateGoal sm = new RESTUpdateGoal(username, GoalHelper.getInstance().getRequests().get(0).guid,
                Goal.GoalCompleteResult.Success);
        sm.setListener(pair.second);
        sm.execute();

        while (!isOperationCompleteList.get(pair.first)) {
            Thread.sleep(1000);
        }

        verify(pair.second).onSuccess();
        assertEquals(GoalHelper.getInstance().getRequests().size(), 0);
    }

    @Test()
    public void failGoal() throws Exception {
        final Pair<Integer, RESTUpdateGoal.Listener> pair = createAListener();

        assertEquals(GoalHelper.getInstance().getRequests().size(), 0);
        setupGoal();
        assertEquals(GoalHelper.getInstance().getRequests().size(), 1);
        RESTUpdateGoal sm = new RESTUpdateGoal(username, GoalHelper.getInstance().getRequests().get(0).guid,
                Goal.GoalCompleteResult.Failed);
        sm.setListener(pair.second);
        sm.execute();

        while (!isOperationCompleteList.get(pair.first)) {
            Thread.sleep(1000);
        }

        verify(pair.second).onSuccess();
        assertEquals(GoalHelper.getInstance().getRequests().size(), 0);
    }

    /* Cancelled goal not updating on server side at this time
    @Test()
    public void deletedGoal() throws Exception {
        final Pair<Integer, RESTUpdateGoal.Listener> pair = createAListener();

        assertEquals(UserHelper.getInstance().getRequests().size(), 0);
        setupGoal();
        assertEquals(UserHelper.getInstance().getRequests().size(), 1);
        RESTUpdateGoal sm = new RESTUpdateGoal(username, UserHelper.getInstance().getRequests().get(0).guid,
                Goal.GoalCompleteResult.Cancelled);
        sm.setListener(pair.second);
        sm.execute();

        while (!isOperationCompleteList.get(pair.first)) {
            Thread.sleep(1000);
        }

        verify(pair.second).onSuccess();
        assertEquals(UserHelper.getInstance().getRequests().get(0).goalCompleteResult, Goal.GoalCompleteResult.Cancelled);
    } */

    private synchronized Pair<Integer, RESTUpdateGoal.Listener> createAListener() {
        final Integer index = isOperationCompleteList.size();
        isOperationCompleteList.add(false);

        RESTUpdateGoal.Listener listener = Mockito.spy(new RESTUpdateGoal.Listener() {
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

    private void setupGoal() throws Exception {
        isSettingUpGoal = true;
        RESTNewGoal sm = new RESTNewGoal(username, "title", 12000, 120000, 55, "encouragement", username, true);
        sm.setListener(new RESTNewGoal.Listener() {
            @Override
            public void onSuccess(String guid) {
                assertTrue(guid != null);
                RESTSync sm = new RESTSync(username, 0);
                sm.setListener(new RESTSync.Listener() {
                    @Override
                    public void onSuccess() {
                        isSettingUpGoal = false;
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        assertTrue(false);
                    }
                });
                sm.execute();
            }

            @Override
            public void onFailure(String errMsg) {
                assertTrue(false);
            }
        });
        sm.execute();

        while (isSettingUpGoal)
            Thread.sleep(1000);
    }
}