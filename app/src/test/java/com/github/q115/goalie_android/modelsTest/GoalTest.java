package com.github.q115.goalie_android.modelsTest;

import com.github.q115.goalie_android.models.Goal;

import org.junit.Test;

import test_util.TestUtil;

import static com.github.q115.goalie_android.models.Goal.GoalCompleteResult.Success;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Qi on 8/3/2017.
 */

public class GoalTest {
    @Test
    public void initalization() throws Exception {
        Goal goalTest = null;

        Goal goal1 = new Goal();
        assertEquals("", goal1.guid);
        assertEquals("", goal1.title);
        assertEquals("", goal1.encouragement);
        assertEquals(0, goal1.startDate);
        assertEquals(0, goal1.endDate);
        assertEquals(0, goal1.wager);
        assertEquals(Goal.GoalCompleteResult.None, goal1.goalCompleteResult);

        Goal goal2 = new Goal("guid", "createdByUsername", "title", 111, 999, 100, "encouragement", Success, "referee");
        goalTest = new Goal();
        goalTest.guid = "guid";
        goalTest.createdByUsername = "createdByUsername";
        goalTest.title = "title";
        goalTest.startDate = 111;
        goalTest.endDate = 999;
        goalTest.wager = 100;
        goalTest.referee = "referee";
        goalTest.encouragement = "encouragement";
        goalTest.goalCompleteResult = Success;
        assertTrue(TestUtil.isGoalEqual(goal2, goalTest));
    }
}