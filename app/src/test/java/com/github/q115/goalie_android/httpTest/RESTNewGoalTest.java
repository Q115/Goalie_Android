package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.https.RESTNewGoal;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static test_util.TestUtil.getValidUsername;

/**
 * Created by Qi on 8/20/2017.
 */
@RunWith(RobolectricTestRunner.class)
public class RESTNewGoalTest extends BaseTest {
    private int operation1;

    @Test(timeout = 10000)
    public void setNewGoal() throws Exception {
        operation1 = 1;
        final String username = UUID.randomUUID().toString();

        // register friend
        final String friendUsername = getValidUsername();

        // register self
        RESTRegisterTest.registerUser(username, null);
        Thread.sleep(1000);

        // get userinfo on RESTNewGoal
        RESTNewGoal sm = new RESTNewGoal(username, "title", 12000, 120000, 55, "encouragement", friendUsername);
        sm.setListener(new RESTNewGoal.Listener() {
            @Override
            public void onSuccess(String guid) {
                User user = UserHelper.getInstance().getAllContacts().get(username);
                assertNotNull(user);
                assertEquals(user.username, username);
                assertEquals(user.activieGoals.size(), 1);

                Goal goal = user.activieGoals.get(0);
                assertEquals(goal.createdByUsername, username);
                assertEquals(goal.title, "title");
                assertEquals(goal.startDate, 12000);
                assertEquals(goal.endDate, 120000);
                assertEquals(goal.wager, 55);
                assertEquals(goal.encouragement, "encouragement");
                assertEquals(goal.referee, friendUsername);
                assertEquals(goal.goalCompleteResult, Goal.GoalCompleteResult.Pending);

                assertEquals(UserHelper.getInstance().getOwnerProfile().reputation, 45);
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
    }
}