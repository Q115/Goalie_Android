package com.github.q115.goalie_android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.Diagnostic.DiagnosticFlag;

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
public class PreferenceHelper {
    private enum PreferenceValue {
        PushID, AccountUsername, LastSyncedTimeEpoch
    }

    private String mPushID;

    public String getPushID() {
        return mPushID;
    }

    public void setPushID(String pushID) {
        commitStringPreference(PreferenceValue.PushID, pushID);
    }

    private String mAccountUsername;

    public String getAccountUsername() {
        return mAccountUsername;
    }

    public void setAccountUsername(String accountUsername) {
        commitStringPreference(PreferenceValue.AccountUsername, accountUsername);
    }

    private long mLastSyncedTimeEpoch;

    public long getLastSyncedTimeEpoch() {
        return mLastSyncedTimeEpoch;
    }

    public void setLastSyncedTimeEpoch(long mLastSyncedTimeEpoch) {
        commitLongPreference(PreferenceValue.LastSyncedTimeEpoch, mLastSyncedTimeEpoch);
    }

    private static PreferenceHelper mInstance;
    private SharedPreferences mSharedPreferences;

    private PreferenceHelper() {
    }

    public static synchronized PreferenceHelper getInstance() {
        if (mInstance == null) {
            mInstance = new PreferenceHelper();
        }
        return mInstance;
    }

    // Initialize all the preference settings. Should only be called from MainApplication class
    public void initialize(Context context) {
        mSharedPreferences = context.getSharedPreferences(Constants.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);

        mPushID = mSharedPreferences.getString(PreferenceValue.PushID.toString(), "");
        mAccountUsername = mSharedPreferences.getString(PreferenceValue.AccountUsername.toString(), "");
        mLastSyncedTimeEpoch = mSharedPreferences.getLong(PreferenceValue.LastSyncedTimeEpoch.toString(), 0);

        UserHelper.getInstance().getOwnerProfile().username = mAccountUsername;
    }

    private void commitStringPreference(PreferenceValue key, String value) {
        if (mSharedPreferences == null)
            return;
        SharedPreferences.Editor prefEditor = mSharedPreferences.edit();
        switch (key) {
            case PushID:
                mPushID = value;
                break;
            case AccountUsername:
                mAccountUsername = value;
                break;
            default:
                break;
        }

        prefEditor.putString(key.toString(), value);
        if (!prefEditor.commit())
            Diagnostic.logError(DiagnosticFlag.Preferences, key.toString() + " commitStringPreference FAILED");
    }

    private void commitLongPreference(PreferenceValue key, long value) {
        if (mSharedPreferences == null)
            return;
        SharedPreferences.Editor prefEditor = mSharedPreferences.edit();
        switch (key) {
            case LastSyncedTimeEpoch:
                mLastSyncedTimeEpoch = value;
                break;
            default:
                break;
        }

        prefEditor.putLong(key.toString(), value);
        if (!prefEditor.commit())
            Diagnostic.logError(DiagnosticFlag.Preferences, key.toString() + " commitLongPreference FAILED");
    }
}
