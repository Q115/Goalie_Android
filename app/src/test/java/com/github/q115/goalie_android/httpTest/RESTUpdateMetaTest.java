package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.https.RESTUpdateMeta;
import com.github.q115.goalie_android.utils.PreferenceHelper;

import org.junit.Test;

import java.util.UUID;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by Qi on 8/7/2017.
 */

public class RESTUpdateMetaTest {

    @Test
    public void execute() throws Exception {
        // update with a registered username
        String username = UUID.randomUUID().toString();
        RESTRegisterTest.registerUser(username);

        String newPushID = "pushID";
        RESTUpdateMeta restUpdateMeta = new RESTUpdateMeta(username, newPushID);
        restUpdateMeta.setListener(null);
        restUpdateMeta.execute();

        Thread.sleep(1000);
        verify(PreferenceHelper.getInstance()).setPushID(newPushID);
        assertTrue(newPushID.equals(PreferenceHelper.getInstance().getPushID()));

        // update with invalid username
        String newPushID2 = "pushID2";
        restUpdateMeta = new RESTUpdateMeta(UUID.randomUUID().toString(), newPushID2);
        restUpdateMeta.setListener(null);
        restUpdateMeta.execute();

        Thread.sleep(1000);
        verify(PreferenceHelper.getInstance(), never()).setPushID(newPushID2);
        assertFalse(newPushID2.equals(PreferenceHelper.getInstance().getPushID()));
    }
}