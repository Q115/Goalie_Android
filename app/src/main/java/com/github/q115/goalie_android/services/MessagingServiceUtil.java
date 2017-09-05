package com.github.q115.goalie_android.services;

import java.util.HashMap;

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

public class MessagingServiceUtil {
    public interface MessagingServiceListener {
        void onNotification();
    }

    private static HashMap<String, MessagingServiceListener> MSGListener = new HashMap<>();

    public static void setMessagingServiceListener(String id, MessagingServiceListener messagingServiceListener) {
        if (messagingServiceListener != null)
            MSGListener.put(id, messagingServiceListener);
        else
            MSGListener.remove(id);
    }

    public static void callMessagingServiceListeners() {
        for (MessagingServiceListener msgServiceListener : MSGListener.values())
            msgServiceListener.onNotification();
    }
}
