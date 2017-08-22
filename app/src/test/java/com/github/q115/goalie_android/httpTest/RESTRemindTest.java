package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.https.RESTNewGoal;
import com.github.q115.goalie_android.https.RESTRemind;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.UUID;

import static test_util.TestUtil.getValidUsername;

/**
 * Created by Qi on 8/20/2017.
 */
@RunWith(RobolectricTestRunner.class)
public class RESTRemindTest extends BaseTest {
    private int operation1;

    @Test(timeout = 10000)
    public void sendReminder() throws Exception {
        operation1 = 1;
        final String username = UUID.randomUUID().toString();

        // register friend
        final String friendUsername = getValidUsername();

        // register self
        RESTRegisterTest.registerUser(username, null);
        Thread.sleep(1000);

        // set up a new goal
        RESTNewGoal sm = new RESTNewGoal(username, "title", 12000, 120000, 55, "encouragement", friendUsername);
        sm.setListener(new RESTNewGoal.Listener() {
            @Override
            public void onSuccess(String guid) {
                // remind on that goal
                RESTRemind sm = new RESTRemind(username, friendUsername, UserHelper.getInstance().getOwnerProfile().activieGoals.get(0).guid);
                sm.setListener(new RESTRemind.Listener() {
                    @Override
                    public void onSuccess() {
                        operation1--;
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        operation1--;
                    }
                });
                sm.execute();
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