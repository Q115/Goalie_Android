package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.https.RESTRegister;
import com.github.q115.goalie_android.https.RESTUpdateMeta;
import com.github.q115.goalie_android.utils.PreferenceHelper;

import org.junit.Test;

import java.util.UUID;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by Qi on 8/7/2017.
 */

public class RESTRegisterTest {
    @Test
    public void executeWithoutPushID() throws Exception {
        RESTUpdateMeta restUpdateMeta = mock(RESTUpdateMeta.class);

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
        RESTUpdateMeta restUpdateMeta = mock(RESTUpdateMeta.class);

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