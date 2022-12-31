/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.q115.goalie_android.services;

import android.content.Intent;

import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.https.RESTRegister;
import com.github.q115.goalie_android.https.RESTSync;
import com.github.q115.goalie_android.https.RESTUpdateUserInfo;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.ui.main.MainActivity;
import com.github.q115.goalie_android.ui.profile.ProfileActivity;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MessagingService extends FirebaseMessagingService {
    private String mMessage;
    private String mGuid;
    private int mResultCode;

    public MessagingService() {
        super();
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        saveToken(s);
    }

    public void saveToken(String pushID) {
        if (pushID == null || pushID.equals(PreferenceHelper.getInstance().getPushID()) || pushID.isEmpty())
            return;
        else if (pushID.length() < 12) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.Other, "FAILED to obtain correct token");
            return;
        } else if (UserHelper.getInstance().getOwnerProfile() == null) {
            return;
        }

        // check whether we need to send up to server or not
        if (UserHelper.getInstance().getOwnerProfile().username == null
                || UserHelper.getInstance().getOwnerProfile().username.isEmpty()) {
            // let register take care of sending this to server
            PreferenceHelper.getInstance().setPushID(pushID);
        } else if (RESTRegister.isRegistering()) {
            // let register take care of sending this to server after register call completes
            PreferenceHelper.getInstance().setPushID(pushID);
        } else {
            // update the old pushID on server
            RESTUpdateUserInfo rest = new RESTUpdateUserInfo(UserHelper.getInstance().getOwnerProfile().username,
                    UserHelper.getInstance().getOwnerProfile().bio, pushID);
            rest.setListener(null);
            rest.execute();
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        if (data != null && data.size() > 0) {
            onJSONParsed(data.get("payload"));
        }
    }

    public void onJSONParsed(String payload) {
        try {
            JSONObject payloadJson = new JSONObject(payload);
            mMessage = payloadJson.getString("message");
            mGuid = payloadJson.getString("guid");
            mResultCode = payloadJson.getInt("result");

            String key = payloadJson.getString("key");
            long time = payloadJson.getLong("time");

            // no need to publish a notification if already syncing/synced
            if (RESTSync.isSyncing() || time < PreferenceHelper.getInstance().getLastSyncedTimeEpoch()) {
                return;
            }

            switch (key) {
                case "remind":
                    remind();
                    break;
                case "response":
                    response();
                    break;
                case "request":
                    request();
                    break;
                default:
                    Intent intent = MainActivity.newIntent(this, 0);
                    MessagingServiceUtil.showNotification(getString(R.string.notification_title), mMessage, intent, this);
                    break;
            }
        } catch (JSONException je) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.Notification, "Failed to parse JSON");
        } catch (Exception e) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.Notification, "Failed to handle notification data");
        }
    }

    private void remind() {
        Intent intent = MainActivity.newIntent(this, mResultCode); // mResultCode = isRemindingRef
        MessagingServiceUtil.showNotification(getString(R.string.notification_remind), mMessage, intent, this);
    }

    private void response() {
        Intent intent;
        if (mResultCode < Goal.GoalCompleteResult.values().length) {
            Goal.GoalCompleteResult goalCompleteResult = Goal.GoalCompleteResult.values()[mResultCode];
            if (goalCompleteResult.isActive()) {
                intent = MainActivity.newIntent(this, 0);
            } else {
                intent = ProfileActivity.newIntent(this, UserHelper.getInstance().getOwnerProfile().username);
            }
        } else
            intent = MainActivity.newIntent(this, 0);

        sync();
        MessagingServiceUtil.showNotification(getString(R.string.notification_response), mMessage, intent, this);
    }

    private void request() {
        sync();
        Intent intent = MainActivity.newIntent(this, 1);
        MessagingServiceUtil.showNotification(getString(R.string.notification_request), mMessage, intent, this);
    }

    private void sync() {
        RESTSync sm = new RESTSync(UserHelper.getInstance().getOwnerProfile().username,
                PreferenceHelper.getInstance().getLastSyncedTimeEpoch(), this);
        sm.setListener(new RESTSync.Listener() {
            @Override
            public void onSuccess() {
                MessagingServiceUtil.callMessagingServiceListeners();
            }

            @Override
            public void onFailure(String errMsg) {
                // intentionally left blank
            }
        });
        sm.execute();
    }
}