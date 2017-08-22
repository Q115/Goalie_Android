package com.github.q115.goalie_android.modelsTest;

import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.GoalFeed;

import org.junit.Test;

import test_util.TestUtil;

import static com.github.q115.goalie_android.models.Goal.GoalCompleteResult.Success;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Qi on 8/11/2017.
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
