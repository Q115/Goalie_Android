package com.github.q115.goalie_android;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.github.q115.goalie_android.utilsTest.ImageHelperInstrumentedTest;
import com.github.q115.goalie_android.utilsTest.PreferenceHelperInstrumentedTest;
import com.github.q115.goalie_android.utilsTest.UserHelperInstrumentedTest;
import org.junit.Test;
import org.junit.runner.RunWith;

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
public class MainApplicationInstrumentedTest {
    @Test
    public void initalization() throws Exception {
        MainApplication ma = new MainApplication();
        ma.initialize(InstrumentationRegistry.getTargetContext());

        ImageHelperInstrumentedTest imageHelperInstrumentedTest = new ImageHelperInstrumentedTest();
        imageHelperInstrumentedTest.initalization();

        PreferenceHelperInstrumentedTest preferenceHelperInstrumentedTest = new PreferenceHelperInstrumentedTest();
        preferenceHelperInstrumentedTest.initalization();

        UserHelperInstrumentedTest userHelperInstrumentedTest = new UserHelperInstrumentedTest();
        userHelperInstrumentedTest.initalization();
    }
}
