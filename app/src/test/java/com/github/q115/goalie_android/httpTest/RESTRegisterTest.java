package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.https.RESTRegister;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by Qi on 8/7/2017.
 */

@RunWith(RobolectricTestRunner.class)
public class RESTRegisterTest {
    @Before
    public void init() {
        FlowManager.init(new FlowConfig.Builder(RuntimeEnvironment.application).build());
        VolleyRequestQueue.getInstance().initialize(RuntimeEnvironment.application);
    }

    @Test
    public void executeWithoutPushID() throws Exception {
        RESTUpdateUserInfo restUpdateMeta = mock(RESTUpdateUserInfo.class);

        // with no pushID saved
        assertFalse(RESTRegister.isRegistering());
        RESTRegisterTest.registerUser(UUID.randomUUID().toString());
        assertTrue(RESTRegister.isRegistering());

        Thread.sleep(1000);
        verify(restUpdateMeta, never()).execute();

        // with pushID saved
        PreferenceHelper.getInstance().setPushID("newPushID");
        assertFalse(RESTRegister.isRegistering());
        RESTRegisterTest.registerUser(UUID.randomUUID().toString());
        assertTrue(RESTRegister.isRegistering());

        Thread.sleep(1000);
        verify(restUpdateMeta).execute();
    }

    @Test
    public void executeWithPushID() throws Exception {
        RESTUpdateUserInfo restUpdateMeta = mock(RESTUpdateUserInfo.class);

        assertFalse(RESTRegister.isRegistering());
        RESTRegister rest = new RESTRegister(UUID.randomUUID().toString(), "pushID");
        rest.setListener(null);
        rest.execute();
        assertTrue(RESTRegister.isRegistering());

        Thread.sleep(1000);
        verify(restUpdateMeta, never()).execute();
    }

    public static void registerUser(String username) {
        RESTRegister rest = new RESTRegister(username, "");
        rest.setListener(null);
        rest.execute();
    }
}