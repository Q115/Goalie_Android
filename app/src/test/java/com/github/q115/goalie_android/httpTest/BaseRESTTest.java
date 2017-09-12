package com.github.q115.goalie_android.httpTest;

import com.github.q115.goalie_android.BaseTest;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import test_util.RESTUtil;

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
public abstract class BaseRESTTest extends BaseTest {
    protected static String username;
    protected static List<Boolean> isOperationCompleteList = Collections.synchronizedList(new ArrayList<Boolean>());

    @Before
    public void registerOwner() throws Exception {
        username = UUID.randomUUID().toString();
        RESTUtil.registerUserSyncronized(username);
    }
}
