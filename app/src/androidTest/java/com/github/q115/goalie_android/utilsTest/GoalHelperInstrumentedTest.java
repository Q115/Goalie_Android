package com.github.q115.goalie_android.utilsTest;

import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.utils.GoalHelper;
import com.github.q115.goalie_android.utils.UserHelper;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static test_util.DatabaseUtil.ReadDatabase;

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

public class GoalHelperInstrumentedTest {
    @BeforeClass
    public static void init() {
        FlowManager.init(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

    @AfterClass
    public static void teardown() {
        FlowManager.reset();
    }

    @Before
    public void setup() {
        UserHelper.getInstance().getOwnerProfile().activeGoals.clear();
        UserHelper.getInstance().getOwnerProfile().finishedGoals.clear();
    }

    @Test
    public void initalization() throws Exception {
        GoalHelper.getInstance().initialize();
        assertNotNull(GoalHelper.getInstance().getFeeds());
        assertNotNull(GoalHelper.getInstance().getRequests());
        assertEquals(0, GoalHelper.getInstance().getFeeds().size());
        assertEquals(0, GoalHelper.getInstance().getRequests().size());
    }

    @Test
    public void goalTestAdd() throws Exception {
        UserHelper.getInstance().getAllContacts().put("",
                UserHelper.getInstance().getOwnerProfile());

        assertEquals(UserHelper.getInstance().getOwnerProfile().activeGoals.size(), 0);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 0);

        Goal goal = new Goal("goal", Goal.GoalCompleteResult.Pending);
        GoalHelper.getInstance().addGoal(goal);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activeGoals.size(), 1);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 0);

        Goal goal2 = new Goal("goal2", Goal.GoalCompleteResult.Ongoing);
        GoalHelper.getInstance().addGoal(goal2);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activeGoals.size(), 2);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 0);

        Goal goal3 = new Goal("goal3", Goal.GoalCompleteResult.Success);
        GoalHelper.getInstance().addGoal(goal3);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activeGoals.size(), 2);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 1);

        Goal goal4 = new Goal("goal4", Goal.GoalCompleteResult.Failed);
        GoalHelper.getInstance().addGoal(goal4);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activeGoals.size(), 2);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 2);

        Goal goal5 = new Goal("goal5", Goal.GoalCompleteResult.Cancelled);
        GoalHelper.getInstance().addGoal(goal5);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activeGoals.size(), 2);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 3);

        UserHelper.getInstance().getOwnerProfile().finishedGoals.clear();
        UserHelper.getInstance().getOwnerProfile().activeGoals.clear();
        UserHelper.getInstance().getAllContacts().put(UserHelper.getInstance().getOwnerProfile().username,
                UserHelper.getInstance().getOwnerProfile());

        ReadDatabase();
        UserHelper.getInstance().getAllContacts().put("",
                UserHelper.getInstance().getOwnerProfile());
        assertEquals(UserHelper.getInstance().getOwnerProfile().activeGoals.size(), 2);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 3);
    }

    @Test
    public void goalTestDelete() throws Exception {
        UserHelper.getInstance().getAllContacts().put("",
                UserHelper.getInstance().getOwnerProfile());

        Goal goal4 = new Goal("goal4", Goal.GoalCompleteResult.Ongoing);
        GoalHelper.getInstance().addGoal(goal4);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activeGoals.size(), 1);

        Goal goal5 = new Goal("goal5", Goal.GoalCompleteResult.Ongoing);
        GoalHelper.getInstance().addGoal(goal5);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activeGoals.size(), 2);

        GoalHelper.getInstance().deleteGoal(goal5.guid);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activeGoals.size(), 1);
    }

    @Test
    public void goalTestModify() throws Exception {
        UserHelper.getInstance().getAllContacts().put("",
                UserHelper.getInstance().getOwnerProfile());

        Goal goal4 = new Goal("goal4", Goal.GoalCompleteResult.Ongoing);
        GoalHelper.getInstance().addGoal(goal4);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activeGoals.size(), 1);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 0);

        Goal goal5 = new Goal("goal5", Goal.GoalCompleteResult.Ongoing);
        GoalHelper.getInstance().addGoal(goal5);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activeGoals.size(), 2);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 0);

        goal5.goalCompleteResult = Goal.GoalCompleteResult.Success;
        GoalHelper.getInstance().modifyGoal(goal5);
        assertEquals(UserHelper.getInstance().getOwnerProfile().activeGoals.size(), 1);
        assertEquals(UserHelper.getInstance().getOwnerProfile().finishedGoals.size(), 1);
    }
}
