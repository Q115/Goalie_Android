package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.https.RESTNewGoal;
import com.github.q115.goalie_android.https.RESTUpdateGoal;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static test_util.TestUtil.getValidUsername;

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
public class RESTUpdateGoalTest extends BaseTest {
    private final String mUsername = UUID.randomUUID().toString();
    private final String mFriendUsername = getValidUsername();

    private int operation1;
    private int operation2;
    private int operation3;
    private int operation4;
    private int operation5;
    private String mGuid1;
    private String mGuid2;
    private String mGuid3;
    private String mGuid4;

    @Test(timeout = 15000)
    public void updateGoal() throws Exception {
        setupGoal();

        acceptGoal();
        sucessfulGoal();
        failedGoal();
        deletedGoal();
    }

    private void setupGoal() throws Exception {
        operation1 = 4;

        // register self
        RESTRegisterTest.registerUser(mUsername, null);
        Thread.sleep(1000);

        // 4 incoming
        RESTNewGoal sm = new RESTNewGoal(mFriendUsername, "title", 12000, 120000, 55, "encouragement", mUsername);
        sm.setListener(new RESTNewGoal.Listener() {
            @Override
            public void onSuccess(String guid) {
                mGuid1 = guid;
                operation1--;
            }

            @Override
            public void onFailure(String errMsg) {
                operation1--;
            }
        });
        sm.execute();

        sm = new RESTNewGoal(mFriendUsername, "title", 12000, 120000, 55, "encouragement", mUsername);
        sm.setListener(new RESTNewGoal.Listener() {
            @Override
            public void onSuccess(String guid) {
                mGuid2 = guid;
                operation1--;
            }

            @Override
            public void onFailure(String errMsg) {
                operation1--;
            }
        });
        sm.execute();

        sm = new RESTNewGoal(mFriendUsername, "title", 12000, 120000, 55, "encouragement", mUsername);
        sm.setListener(new RESTNewGoal.Listener() {
            @Override
            public void onSuccess(String guid) {
                mGuid3 = guid;
                operation1--;
            }

            @Override
            public void onFailure(String errMsg) {
                operation1--;
            }
        });
        sm.execute();

        sm = new RESTNewGoal(mFriendUsername, "title", 12000, 120000, 55, "encouragement", mUsername);
        sm.setListener(new RESTNewGoal.Listener() {
            @Override
            public void onSuccess(String guid) {
                mGuid4 = guid;
                operation1--;
            }

            @Override
            public void onFailure(String errMsg) {
                operation1--;
            }
        });
        sm.execute();

        while (operation1 != 0)
            Thread.sleep(1000);

        // clear out others, manually add in the ones you received. (in prod, you'll automatically get it via sync or notification)
        UserHelper.getInstance().getRequests().clear();
        UserHelper.getInstance().getRequests().add(new Goal(mGuid1, Goal.GoalCompleteResult.Pending));
        UserHelper.getInstance().getRequests().add(new Goal(mGuid2, Goal.GoalCompleteResult.Pending));
        UserHelper.getInstance().getRequests().add(new Goal(mGuid3, Goal.GoalCompleteResult.Pending));
        UserHelper.getInstance().getRequests().add(new Goal(mGuid4, Goal.GoalCompleteResult.Pending));
    }

    private void acceptGoal() throws Exception {
        operation2 = 1;

        assertEquals(UserHelper.getInstance().getRequests().size(), 4);
        RESTUpdateGoal sm = new RESTUpdateGoal(mUsername, mGuid1, Goal.GoalCompleteResult.Ongoing);
        sm.setListener(new RESTUpdateGoal.Listener() {
            @Override
            public void onSuccess() {
                assertEquals(UserHelper.getInstance().getRequests().size(), 4);
                assertEquals(UserHelper.getInstance().getRequests().get(0).guid, mGuid1);
                assertEquals(UserHelper.getInstance().getRequests().get(0).goalCompleteResult, Goal.GoalCompleteResult.Ongoing);
                operation2--;
            }

            @Override
            public void onFailure(String errMsg) {
                operation2--;
            }
        });
        sm.execute();

        while (operation2 != 0)
            Thread.sleep(1000);
    }

    private void sucessfulGoal() throws Exception {
        operation3 = 1;

        assertEquals(UserHelper.getInstance().getRequests().size(), 4);
        RESTUpdateGoal sm = new RESTUpdateGoal(mUsername, mGuid2, Goal.GoalCompleteResult.Success);
        sm.setListener(new RESTUpdateGoal.Listener() {
            @Override
            public void onSuccess() {
                assertEquals(UserHelper.getInstance().getRequests().size(), 3);
                operation3--;
            }

            @Override
            public void onFailure(String errMsg) {
                operation3--;
            }
        });
        sm.execute();

        while (operation3 != 0)
            Thread.sleep(1000);
    }

    private void failedGoal() throws Exception {
        operation4 = 1;
        assertEquals(UserHelper.getInstance().getRequests().size(), 3);
        RESTUpdateGoal sm = new RESTUpdateGoal(mUsername, mGuid3, Goal.GoalCompleteResult.Failed);
        sm.setListener(new RESTUpdateGoal.Listener() {
            @Override
            public void onSuccess() {
                assertEquals(UserHelper.getInstance().getRequests().size(), 2);
                operation4--;
            }

            @Override
            public void onFailure(String errMsg) {
                operation4--;
            }
        });
        sm.execute();

        while (operation4 != 0)
            Thread.sleep(1000);
    }

    private void deletedGoal() throws Exception {
        operation5 = 1;
        assertEquals(UserHelper.getInstance().getRequests().size(), 2);
        RESTUpdateGoal sm = new RESTUpdateGoal(mUsername, mGuid4, Goal.GoalCompleteResult.Cancelled);
        sm.setListener(new RESTUpdateGoal.Listener() {
            @Override
            public void onSuccess() {
                assertEquals(UserHelper.getInstance().getRequests().size(), 1);
                operation5--;
            }

            @Override
            public void onFailure(String errMsg) {
                operation5--;
            }
        });
        sm.execute();

        while (operation5 != 0)
            Thread.sleep(1000);
    }
}