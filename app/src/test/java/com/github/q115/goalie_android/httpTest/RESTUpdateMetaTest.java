package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.https.RESTUpdateUserInfo;
import com.github.q115.goalie_android.https.VolleyRequestQueue;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.UUID;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Qi on 8/7/2017.
 */
@RunWith(RobolectricTestRunner.class)
public class RESTUpdateMetaTest {
    @Before
    public void init() {
        FlowManager.init(new FlowConfig.Builder(RuntimeEnvironment.application).build());
        VolleyRequestQueue.getInstance().initialize(RuntimeEnvironment.application);
        PreferenceHelper.getInstance().initialize(RuntimeEnvironment.application);
    }

    @Test
    public void execute() throws Exception {
        // update with a registered username
        String username = UUID.randomUUID().toString();
        RESTRegisterTest.registerUser(username);

        String newPushID = "pushID";
        RESTUpdateUserInfo restUpdateMeta = new RESTUpdateUserInfo(username, "", newPushID);
        restUpdateMeta.setListener(null);
        restUpdateMeta.execute();

        Thread.sleep(1000);
        assertTrue(newPushID.equals(PreferenceHelper.getInstance().getPushID()));

        // update with invalid username
        String newPushID2 = "pushID2";
        restUpdateMeta = new RESTUpdateUserInfo(UUID.randomUUID().toString(), "", newPushID2);
        restUpdateMeta.setListener(null);
        restUpdateMeta.execute();

        Thread.sleep(1000);
        assertFalse(newPushID2.equals(PreferenceHelper.getInstance().getPushID()));
    }
}