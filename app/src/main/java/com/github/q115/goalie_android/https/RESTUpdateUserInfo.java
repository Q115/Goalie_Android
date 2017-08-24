package com.github.q115.goalie_android.https;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.github.q115.goalie_android.Constants.ASYNC_CONNECTION_NORMAL_TIMEOUT;
import static com.github.q115.goalie_android.Constants.FAILED;
import static com.github.q115.goalie_android.Constants.FAILED_TO_CONNECT;
import static com.github.q115.goalie_android.Constants.FAILED_TO_Send;
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

public class RESTUpdateUserInfo {
    private RESTUpdateUserInfo.Listener mList;
    private String mBio;
    private String mUsername;
    private String mPushID;

    public RESTUpdateUserInfo(String username, String bio, String pushID) {
        mBio = bio;
        mUsername = username;
        mPushID = pushID;
    }

    public interface Listener {
        void onSuccess();

        void onFailure(String errMsg);
    }

    public void setListener(RESTUpdateUserInfo.Listener mList) {
        this.mList = mList;
    }

    public void execute() {
        final String url = URL + "/updateuserinfo";
        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                UserHelper.getInstance().getOwnerProfile().bio = mBio;
                UserHelper.getInstance().addUser(UserHelper.getInstance().getOwnerProfile());
                PreferenceHelper.getInstance().setPushID(mPushID);

                if (mList != null)
                    mList.onSuccess();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mList == null)
                    return;

                if (error == null || error.networkResponse == null) {
                    mList.onFailure(FAILED_TO_CONNECT);
                } else if (error.networkResponse.headers != null && error.networkResponse.headers.containsKey("response")) {
                    String msgErr = error.networkResponse.headers.get("response") == null ? FAILED
                            : error.networkResponse.headers.get("response");
                    mList.onFailure(msgErr);
                } else {
                    mList.onFailure(FAILED_TO_Send);
                }
            }
        }) {
            @Override
            public HashMap<String, String> getHeaders() {
                HashMap<String, String> mHeaders = new HashMap<>();
                mHeaders.put("Content-Type", "application/json");
                mHeaders.put("username", mUsername);
                return mHeaders;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("bio", mBio);
                params.put("username", mUsername);
                params.put("pushID", mPushID);
                return new JSONObject(params).toString().getBytes();
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                ASYNC_CONNECTION_NORMAL_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                0));
        VolleyRequestQueue.getInstance().addToRequestQueue(req);
    }
}