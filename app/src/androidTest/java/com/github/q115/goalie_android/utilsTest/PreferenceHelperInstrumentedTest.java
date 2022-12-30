package com.github.q115.goalie_android.utilsTest;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.utils.PreferenceHelper;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

@RunWith(AndroidJUnit4.class)
public class PreferenceHelperInstrumentedTest {
    @Before
    public void init() throws Exception {
        PreferenceHelper.getInstance().initialize(InstrumentationRegistry.getTargetContext());
    }

    @AfterClass
    public static void teardown() throws Exception {
        SharedPreferences sp = InstrumentationRegistry.getTargetContext().getSharedPreferences(Constants.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }

    @Test
    public void initalization() throws Exception {
        assertNotNull(PreferenceHelper.getInstance().getPushID());
        assertNotNull(PreferenceHelper.getInstance().getAccountUsername());
    }

    @Test
    public void setPushID() throws Exception {
        PreferenceHelper.getInstance().setPushID("new pushID");
        assertEquals("new pushID", PreferenceHelper.getInstance().getPushID());
    }

    @Test
    public void setAccountUsername() throws Exception {
        PreferenceHelper.getInstance().setAccountUsername("new AccountUsername");
        assertEquals("new AccountUsername", PreferenceHelper.getInstance().getAccountUsername());
    }

    @Test
    public void setLastSyncedTimeEpoch() throws Exception {
        PreferenceHelper.getInstance().setLastSyncedTimeEpoch(22);
        assertEquals(22, PreferenceHelper.getInstance().getLastSyncedTimeEpoch());
    }

    @Test
    public void storedOnDisk() throws Exception {
        setAccountUsername();
        setPushID();
        PreferenceHelper.getInstance().initialize(InstrumentationRegistry.getTargetContext());

        assertEquals("new pushID", PreferenceHelper.getInstance().getPushID());
        assertEquals("new AccountUsername", PreferenceHelper.getInstance().getAccountUsername());
    }
}