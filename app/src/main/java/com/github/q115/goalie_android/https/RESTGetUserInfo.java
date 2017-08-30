package com.github.q115.goalie_android.https;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.UserHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

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

public class RESTGetUserInfo {
    private String mUsername;
    private RESTGetUserInfo.Listener mList;

    public RESTGetUserInfo(String username) {
        this.mUsername = username;
    }

    public interface Listener {
        void onSuccess();

        void onFailure(String errMsg);
    }

    public void setListener(RESTGetUserInfo.Listener mList) {
        this.mList = mList;
    }

    public void execute() {
        String url;
        try {
            url = URL + "/getuserinfo?username=" + URLEncoder.encode(mUsername, "utf-8");
        } catch (UnsupportedEncodingException e) {
            url = URL + "/getuserinfo?username=" + mUsername;
        }

        StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(response.getBytes("ISO-8859-1"), "UTF-8"));

                    Long lastPhotoModifiedTime = jsonObject.getLong("lastPhotoModifiedTime");
                    String bio = jsonObject.getString("bio");
                    Long reputation = jsonObject.getLong("reputation");
                    JSONArray finishedGoals = jsonObject.getJSONArray("goals");

                    ArrayList<Goal> finishedGoalsList = new ArrayList<>();
                    for (int i = 0; i < finishedGoals.length(); i++) {
                        JSONObject jsonObj = finishedGoals.getJSONObject(i);
                        String guid = jsonObj.getString("guid");
                        String title = jsonObj.getString("title");
                        long start = jsonObj.getLong("start");
                        long end = jsonObj.getLong("end");
                        long wager = jsonObj.getLong("wager");
                        String encouragement = jsonObj.getString("encouragement");
                        String referee = jsonObj.getString("referee");
                        long activityDate = jsonObj.getLong("activityDate");
                        finishedGoalsList.add(new Goal(guid, mUsername, title, start, end, wager, encouragement, Goal.GoalCompleteResult.Success, referee, activityDate));
                    }

                    User s = new User(mUsername, bio, reputation, lastPhotoModifiedTime);
                    s.finishedGoals = finishedGoalsList;
                    UserHelper.getInstance().addUser(s);

                    if (mList != null)
                        mList.onSuccess();
                } catch (Exception e) {
                    Diagnostic.logError(Diagnostic.DiagnosticFlag.Other, "Failed to parse userinfo");
                    if (mList != null)
                        mList.onFailure(FAILED);
                }
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
                mHeaders.put("Username", mUsername);
                mHeaders.put("Authorization", Constants.KEY);
                return mHeaders;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                ASYNC_CONNECTION_NORMAL_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                0));
        VolleyRequestQueue.getInstance().addToRequestQueue(req);
    }
}
