package com.github.q115.goalie_android.ui.friends;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

/**
 * Created by Qi on 8/9/2017.
 */

public class FriendsPresenter {
    private final FriendsView mFriendsView;
    private final Context mContext;

    public FriendsPresenter(Context context, @NonNull FriendsView friendsView) {
        mContext = context;
        mFriendsView = friendsView;
        mFriendsView.setPresenter(this);
    }

    public void start() {
    }

    public void sendSMSInvite(Uri contactUri) {
        if(contactUri == null)
            return;

        Cursor c = mContext.getContentResolver().query(contactUri, null, null, null, null);

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
