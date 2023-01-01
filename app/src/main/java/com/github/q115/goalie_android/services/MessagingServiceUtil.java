package com.github.q115.goalie_android.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.R;

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

    private static final HashMap<String, MessagingServiceListener> MSGListener = new HashMap<>();

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

    public static void showNotification(String title, String description, Intent intent, Context context) {
        String channelID = getNotificationChannelID(context);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap largeNotificationImage = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                .setDefaults(Notification.DEFAULT_ALL)
                .setLargeIcon(largeNotificationImage)
                .setSmallIcon(R.drawable.ic_logo)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary));

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Get the notification manager & publish the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.ID_NOTIFICATION_BROADCAST, notification);
    }

    private static String getNotificationChannelID(Context context) {
        final String channelID = "GoalieRemindersChannel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            CharSequence name = context.getString(R.string.app_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(channelID, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        return channelID;
    }
}
