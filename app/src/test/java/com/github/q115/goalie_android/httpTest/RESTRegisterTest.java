package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.https.RESTRegister;
import com.github.q115.goalie_android.utils.PreferenceHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import android.util.Pair;

import java.util.UUID;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
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
public class RESTRegisterTest extends BaseRESTTest {
    @Before
    @Override
    public void registerOwner() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void executeWithoutPushIDWithoutPushIDSaved() throws Exception {
        Pair<Integer, RESTRegister.Listener> pair = createAListener();

        RESTRegister rest = new RESTRegister(UUID.randomUUID().toString(), "");
        rest.setListener(pair.second);
        rest.execute();

        while (!isOperationCompleteList.get(pair.first)) {
            Thread.sleep(1000);
        }

        verify(pair.second).onSuccess();
        assertFalse(!PreferenceHelper.getInstance().getPushID().isEmpty());
    }

    @Test
    public void executeWithoutPushIDWithPushIDSaved() throws Exception {
        Pair<Integer, RESTRegister.Listener> pair = createAListener();

        PreferenceHelper.getInstance().setPushID("newPushID");
        RESTRegister rest = new RESTRegister(UUID.randomUUID().toString(), "");
        rest.setListener(pair.second);
        rest.execute();

        while (!isOperationCompleteList.get(pair.first)) {
            Thread.sleep(1000);
        }

        verify(pair.second).onSuccess();
        assertTrue(!PreferenceHelper.getInstance().getPushID().isEmpty());
        assertFalse(RESTRegister.isRegistering());
    }

    @Test
    public void executeWithPushID() throws Exception {
        Pair<Integer, RESTRegister.Listener> pair = createAListener();

        assertFalse(RESTRegister.isRegistering());
        RESTRegister rest = new RESTRegister(UUID.randomUUID().toString(), "pushID");
        rest.setListener(pair.second);
        rest.execute();
        assertTrue(RESTRegister.isRegistering());

        while (!isOperationCompleteList.get(pair.first)) {
            Thread.sleep(1000);
        }

        verify(pair.second).onSuccess();
        assertFalse(RESTRegister.isRegistering());
    }

    private synchronized Pair<Integer, RESTRegister.Listener> createAListener() {
        final Integer index = isOperationCompleteList.size();
        isOperationCompleteList.add(false);

        RESTRegister.Listener listener = Mockito.spy(new RESTRegister.Listener() {
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