package com.github.q115.goalie_android.ui.friends;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;

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

public class FriendsActivityPresenter {
    private final FriendsActivityView mFriendsView;

    public FriendsActivityPresenter(@NonNull FriendsActivityView friendsView) {
        mFriendsView = friendsView;
        mFriendsView.setPresenter(this);
    }

    public void start() {
        // intentionally left blank
    }

    public void sendSMSInvite(Context context, Uri contactUri) {
        if(contactUri == null)
            return;

        Cursor c = context.getContentResolver().query(contactUri, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            try {
                c.moveToFirst();
                int phoneIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String phoneNo = c.getString(phoneIndex);
                mFriendsView.sendSMSInvite(phoneNo);
            } finally {
                c.close();
            }
        }
    }
}
