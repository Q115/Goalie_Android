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

import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class InstanceIDService extends FirebaseInstanceIdService {
    public InstanceIDService() {
        super();
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        saveToken(refreshedToken);
    }


    /**
     * Persist token to third-party servers.
     */
    private void saveToken(String pushID) {
        if (pushID == null || pushID.equals(PreferenceHelper.getInstance().getPushID()) || pushID.isEmpty())
            return;
        else if (pushID.length() < 24) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.Other, "Failed to obtain correct token");
            return;
        } else if (UserHelper.getmInstance().getOwnerProfile() == null) {
            return;
        }

        //update the server
        if (UserHelper.getmInstance().getOwnerProfile().username != null && !UserHelper.getmInstance().getOwnerProfile().username.isEmpty()) {
            // TODO Preferences.getmInstance().setPushID(pushID);
        }
    }
}