/**
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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.https.RESTSync;
import com.github.q115.goalie_android.ui.MainActivity;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MessagingService extends FirebaseMessagingService {
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        Map<String, String> data = remoteMessage.getData();
        if (data != null && data.size() > 0) {
            try {
                String payload = data.get("payload");
                JSONObject payloadJson = new JSONObject(payload);
                String key = payloadJson.getString("key");
                String message = payloadJson.getString("message");
                String guid = payloadJson.getString("guid");

                switch (key) {
                    case "remind":
                        showNotification(getString(R.string.notification_title), message);
                        break;
                    case "response":
                    case "request":
                        RESTSync sm = new RESTSync(UserHelper.getInstance().getOwnerProfile().username, PreferenceHelper.getInstance().getLastSyncedTimeEpoch());
                        sm.setListener(null);
                        sm.execute();
                        showNotification(getString(R.string.notification_title), message);
                        break;
                    default:
                        break;
                }
            } catch (JSONException je) {
                Diagnostic.logError(Diagnostic.DiagnosticFlag.Notification, "Failed to parse JSON");
            } catch (Exception e) {
                Diagnostic.logError(Diagnostic.DiagnosticFlag.Notification, "Failed to handle notification data");
            }
        }
    }

    private void showNotification(String title, String description) {
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);

        Bitmap largeNotificationImage = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(description)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setVibrate(null)
                .setSound(null)
                .setLargeIcon(largeNotificationImage)
                .setSmallIcon(R.mipmap.ic_launcher);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder.setColor(ContextCompat.getColor(this, R.color.colorPrimary));

        // Build the notification:
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Get the notification manager & publish the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.ID_NOTIFICATION_BROADCAST, notification);
    }
}