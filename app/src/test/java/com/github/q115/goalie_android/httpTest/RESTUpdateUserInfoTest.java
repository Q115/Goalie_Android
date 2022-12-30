package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.https.RESTUpdateUserInfo;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import android.util.Pair;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

/*
 * Copyright 2017 Qi Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@RunWith(RobolectricTestRunner.class)
public class RESTUpdateUserInfoTest extends BaseRESTTest {
    @Test
    public void updatePushID() throws Exception {
        Pair<Integer, RESTUpdateUserInfo.Listener> pair = createAListener();

        final String newPushID = "pushID";
        RESTUpdateUserInfo restUpdateMeta = new RESTUpdateUserInfo(username, "", newPushID);
        restUpdateMeta.setListener(pair.second);
        restUpdateMeta.execute();

        while (!isOperationCompleteList.get(pair.first)) {
            Thread.sleep(1000);
        }

        verify(pair.second).onSuccess();
        assertTrue(newPushID.equals(PreferenceHelper.getInstance().getPushID()));
    }

    @Test
    public void updateBio() throws Exception {
        Pair<Integer, RESTUpdateUserInfo.Listener> pair = createAListener();

        final String bio = "new bio";
        RESTUpdateUserInfo restUpdateMeta = new RESTUpdateUserInfo(username, bio, "pushID");
        restUpdateMeta.setListener(pair.second);
        restUpdateMeta.execute();

        while (!isOperationCompleteList.get(pair.first)) {
            Thread.sleep(1000);
        }

        verify(pair.second).onSuccess();
        assertTrue(UserHelper.getInstance().getOwnerProfile().bio.equals(bio));
    }

    @Test
    public void updateFailed() throws Exception {
        Pair<Integer, RESTUpdateUserInfo.Listener> pair = createAListener();

        // update with invalid username
        String bio = "bio";
        String pushID = "pushID";
        RESTUpdateUserInfo restUpdateMeta = new RESTUpdateUserInfo("", bio, pushID);
        restUpdateMeta.setListener(pair.second);
        restUpdateMeta.execute();

        while (!isOperationCompleteList.get(pair.first)) {
            Thread.sleep(1000);
        }

        verify(pair.second).onFailure("Unauthorized, Please Update App");
        assertFalse(UserHelper.getInstance().getOwnerProfile().bio.equals(bio));
        assertFalse(pushID.equals(PreferenceHelper.getInstance().getPushID()));
    }

    private synchronized Pair<Integer, RESTUpdateUserInfo.Listener> createAListener() {
        final Integer index = isOperationCompleteList.size();
        isOperationCompleteList.add(false);

        RESTUpdateUserInfo.Listener listener = Mockito.spy(new RESTUpdateUserInfo.Listener() {
            @Override
            public void onSuccess() {
                isOperationCompleteList.set(index, true);
            }

            @Override
            public void onFailure(String errMsg) {
                isOperationCompleteList.set(index, true);
            }
        });

        return new Pair<>(index, listener);
    }
}