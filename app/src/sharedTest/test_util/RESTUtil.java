package test_util;

import com.github.q115.goalie_android.https.RESTRegister;

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

public class RESTUtil {
    private static boolean isRegisteredSuccessfully;
    private static boolean isFinishedRegistering;

    public static boolean registerUserSyncronized(String username) throws Exception {
        isRegisteredSuccessfully = false;
        isFinishedRegistering = false;

        RESTRegister rest = new RESTRegister(username, "");
        rest.setListener(new RESTRegister.Listener() {
            @Override
            public void onSuccess() {
                isFinishedRegistering = true;
                isRegisteredSuccessfully = true;
            }

            @Override
            public void onFailure(String errMsg) {
                isFinishedRegistering = true;
                isRegisteredSuccessfully = false;
            }
        });
        rest.execute();

        while (!isFinishedRegistering)
            Thread.sleep(1000);

        return isRegisteredSuccessfully;
    }

    public static String getValidFriendUsername() {
        return "device";
    }

    public static String getTestUsername() {
        return "test";
    }
}
