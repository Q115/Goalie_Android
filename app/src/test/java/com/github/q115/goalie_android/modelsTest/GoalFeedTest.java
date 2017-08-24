package com.github.q115.goalie_android.modelsTest;

import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.GoalFeed;

import org.junit.Test;

import test_util.TestUtil;

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

public class GoalFeedTest {
    @Test
    public void initalization() throws Exception {
        GoalFeed goalTest = null;

        GoalFeed goal1 = new GoalFeed();
        assertEquals("", goal1.guid);
        assertEquals("", goal1.createdUsername);
        assertEquals(false, goal1.hasVoted);
        assertEquals(Goal.GoalCompleteResult.None, goal1.goalCompleteResult);
        assertEquals(1, goal1.upvoteCount);
        assertEquals(0, goal1.wager);
        assertEquals(Goal.GoalCompleteResult.None, goal1.goalCompleteResult);

        GoalFeed goal2 = new GoalFeed("guid", 50, "createdUsername", 2, Goal.GoalCompleteResult.Success);
        goalTest = new GoalFeed();
        goalTest.guid = "guid";
        goalTest.createdUsername = "createdUsername";
        goalTest.upvoteCount = 2;
        goalTest.wager = 50;
        goalTest.goalCompleteResult = Success;
        assertTrue(TestUtil.isGoalFeedEqual(goal2, goalTest));
    }
}
