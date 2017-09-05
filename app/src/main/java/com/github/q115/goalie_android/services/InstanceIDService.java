package com.github.q115.goalie_android.services;

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

import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.https.RESTRegister;
import com.github.q115.goalie_android.https.RESTUpdateUserInfo;
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
        if (UserHelper.getInstance().getOwnerProfile().username == null || UserHelper.getInstance().getOwnerProfile().username.isEmpty()) {
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
}