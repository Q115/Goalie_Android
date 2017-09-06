package com.github.q115.goalie_android.serviceTest;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.services.MessagingServiceUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

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
public class MessagingServiceUtilTest extends BaseTest {
    @Test
    public void callMessagingServiceListenerInvalid() throws Exception {
        MessagingServiceUtil.setMessagingServiceListener(null, null);
        MessagingServiceUtil.setMessagingServiceListener("", null);
        MessagingServiceUtil.setMessagingServiceListener("", new MessagingServiceUtil.MessagingServiceListener() {
            @Override
            public void onNotification() {

            }
        });

        MessagingServiceUtil.setMessagingServiceListener("test", new MessagingServiceUtil.MessagingServiceListener() {
            @Override
            public void onNotification() {

            }
        });

        MessagingServiceUtil.callMessagingServiceListeners();
    }
}