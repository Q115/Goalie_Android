package com.github.q115.goalie_android.serviceTest;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.services.MessagingService;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertEquals;

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
public class InstanceIDServiceTest extends BaseTest {
    private final String validToken = "this_is_a_valid_token__this_is_a_valid_token__this_is_a_valid_token";

    @Before
    public void setup() {
        PreferenceHelper.getInstance().setPushID("");
    }

    @Test
    public void saveTokenInvalid() throws Exception {
        MessagingService service = new MessagingService();
        UserHelper.getInstance().setOwnerProfile(new User());

        service.saveToken("invalid");
        assertEquals(PreferenceHelper.getInstance().getPushID(), "");

        service.saveToken("");
        assertEquals(PreferenceHelper.getInstance().getPushID(), "");

        service.saveToken(null);
        assertEquals(PreferenceHelper.getInstance().getPushID(), "");
    }

    @Test
    public void saveTokenWhenNotRegistered() throws Exception {
        MessagingService service = new MessagingService();
        User user = UserHelper.getInstance().getOwnerProfile();
        user = null;

        service.saveToken(validToken);

        assertEquals(PreferenceHelper.getInstance().getPushID(), validToken);
    }

    @Test
    public void saveToken() throws Exception {
        MessagingService service = new MessagingService();
        UserHelper.getInstance().setOwnerProfile(new User("device"));

        service.saveToken(validToken);

        Thread.sleep(1000);
        assertEquals(PreferenceHelper.getInstance().getPushID(), validToken);
    }
}
