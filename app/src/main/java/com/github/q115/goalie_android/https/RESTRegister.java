package com.github.q115.goalie_android.https;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.github.q115.goalie_android.Constants.ASYNC_CONNECTION_EXTENDED_TIMEOUT;
import static com.github.q115.goalie_android.Constants.URL;

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

public class RESTRegister extends RESTBase<String> {
    private RESTRegister.Listener mListener;
    private final String mPushID;
    private static boolean isRegistering;

    public RESTRegister(String username, String pushID) {
        mUsername = username;
        mPushID = pushID;
    }

    public interface Listener extends RESTBaseListener {
        void onSuccess();

        void onFailure(String errMsg);
    }

    public static boolean isRegistering() {
        return isRegistering;
    }

    public void setListener(RESTRegister.Listener mList) {
        super.setListener(mList);
        this.mListener = mList;
    }

    public void execute() {
        final String url = URL + "/register";
        isRegistering = true;
        StringRequest req = new StringRequest(Request.Method.POST, url, this, this) {
            @Override
            public Map<String, String> getHeaders() {
                return getDefaultHeaders();
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", mUsername);
                params.put("device", "android");
                params.put("pushID", mPushID);
                return new JSONObject(params).toString().getBytes();
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                ASYNC_CONNECTION_EXTENDED_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                0));
        VolleyRequestQueue.getInstance().addToRequestQueue(req);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        isRegistering = false;
        super.onErrorResponse(error);
    }

    @Override
    public void onResponse(String response) {
        UserHelper.getInstance().getOwnerProfile().username = mUsername;
        UserHelper.getInstance().setOwnerProfile(UserHelper.getInstance().getOwnerProfile());

        isRegistering = false;

        // update pushID if one came while you were registering
        if ((mPushID == null || mPushID.isEmpty()) && !PreferenceHelper.getInstance().getPushID().isEmpty()) {
            RESTUpdateUserInfo rest = new RESTUpdateUserInfo(mUsername,
                    UserHelper.getInstance().getOwnerProfile().bio, PreferenceHelper.getInstance().getPushID());
            rest.setListener(null);
            rest.execute();
        }

        if (mListener != null)
            mListener.onSuccess();
    }
}