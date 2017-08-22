package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.https.RESTGetUserInfo;
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
public class RESTGetUserInfoTest extends BaseTest {
    private int operation1;

    @Test(timeout = 10000)
    public void getUserInfo() throws Exception {
        operation1 = 1;
        String username = UUID.randomUUID().toString();

        // register friend
        final String friendUsername = getValidUsername();

        // register self
        RESTRegisterTest.registerUser(username, null);
        Thread.sleep(1000);

        // get userinfo on friend
        RESTGetUserInfo sm = new RESTGetUserInfo(friendUsername);
        sm.setListener(new RESTGetUserInfo.Listener() {
            @Override
            public void onSuccess() {
                User user = UserHelper.getInstance().getAllContacts().get(friendUsername);
                assertNotNull(user);
                assertEquals(user.username, friendUsername);

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
