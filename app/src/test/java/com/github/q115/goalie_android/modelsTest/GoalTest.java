package com.github.q115.goalie_android.modelsTest;

import com.github.q115.goalie_android.models.Goal;

import org.junit.Test;

import test_util.ModelUtil;

import static com.github.q115.goalie_android.models.Goal.GoalCompleteResult.Failed;
import static com.github.q115.goalie_android.models.Goal.GoalCompleteResult.Success;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

public class GoalTest {
    @Test
    public void defaultConstructor() throws Exception {
        Goal goal = new Goal();
        assertEquals("", goal.guid);
        assertEquals("", goal.title);
        assertEquals("", goal.encouragement);
        assertEquals(0, goal.startDate);
        assertEquals(0, goal.endDate);
        assertEquals(0, goal.wager);
        assertEquals(Goal.GoalCompleteResult.None, goal.goalCompleteResult);
    }

    @Test
    public void initConstructor1() throws Exception {
        Goal goal = new Goal("guid", "createdByUsername", "title", 111, 999, 100, "encouragement",
                Success, "referee", System.currentTimeMillis());
        Goal goalTest = new Goal();
        goalTest.guid = "guid";
        goalTest.createdByUsername = "createdByUsername";
        goalTest.title = "title";
        goalTest.startDate = 111;
        goalTest.endDate = 999;
        goalTest.wager = 100;
        goalTest.referee = "referee";
        goalTest.encouragement = "encouragement";
        goalTest.goalCompleteResult = Success;
        assertTrue(ModelUtil.isGoalEqual(goal, goalTest));
    }

    @Test
    public void initConstructor2() throws Exception {
        Goal goal = new Goal("newguid", Failed);
        Goal goalTest = new Goal();
        goalTest.guid = "newguid";
        goalTest.goalCompleteResult = Failed;
        assertTrue(ModelUtil.isGoalEqual(goal, goalTest));
    }
}