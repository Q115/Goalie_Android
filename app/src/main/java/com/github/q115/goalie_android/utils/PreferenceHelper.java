package com.github.q115.goalie_android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.Diagnostic.DiagnosticFlag;

public class PreferenceHelper {
    private enum PreferenceValue {
        PushID, AccountUsername
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

    /// <summary>
    /// Initialize all the preference settings. Should only be called from MainApplication class
    /// </summary>
    public void initialize(Context context) {
        mSharedPreferences = context.getSharedPreferences(Constants.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        mPushID = mSharedPreferences.getString(PreferenceValue.PushID.toString(), "");
        mAccountUsername = mSharedPreferences.getString(PreferenceValue.AccountUsername.toString(), "");
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
}
