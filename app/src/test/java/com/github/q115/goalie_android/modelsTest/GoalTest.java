package com.github.q115.goalie_android.modelsTest;

import com.github.q115.goalie_android.models.Goal;

import org.junit.Test;

import test_util.TestUtil;

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
        assertEquals(0, goal1.deadline);
        assertEquals(0, goal1.wager);

        Goal goal2 = new Goal("guid", "title", 999, 100);
        goalTest = new Goal();
        goalTest.guid = "guid";
        goalTest.title = "title";
        goalTest.deadline = 999;
        goalTest.wager = 100;
        assertTrue(TestUtil.isGoalEqual(goal2, goalTest));
    }
}