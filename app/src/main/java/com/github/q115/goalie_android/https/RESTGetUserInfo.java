package com.github.q115.goalie_android.https;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
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

public class RESTGetUserInfo extends RESTBase<String> {
    private RESTGetUserInfo.Listener mListener;

    public RESTGetUserInfo(String username) {
        this.mUsername = username;
    }

    public interface Listener extends RESTBaseListener {
        void onSuccess();

        void onFailure(String errMsg);
    }

    public void setListener(RESTGetUserInfo.Listener mList) {
        super.setListener(mList);
        this.mListener = mList;
    }

    public void execute() {
        String url;
        try {
            url = URL + "/getuserinfo?username=" + URLEncoder.encode(mUsername, "utf-8");
        } catch (UnsupportedEncodingException e) {
            url = URL + "/getuserinfo?username=" + mUsername;
        }

        StringRequest req = new StringRequest(Request.Method.GET, url, this, this) {
            @Override
            public HashMap<String, String> getHeaders() {
                return getDefaultHeaders();
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                ASYNC_CONNECTION_NORMAL_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                0));
        VolleyRequestQueue.getInstance().addToRequestQueue(req);
    }

    @Override
    public void onResponse(String response) {
        boolean isSuccessful;
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

                Goal goal = new Goal(guid, mUsername, title, start, end, wager,
                        encouragement, Goal.GoalCompleteResult.Success, referee, activityDate);
                finishedGoalsList.add(goal);
            }

            User s = new User(mUsername, bio, reputation, lastPhotoModifiedTime);
            s.finishedGoals = finishedGoalsList;
            UserHelper.getInstance().addUser(s);

            isSuccessful = true;
        } catch (Exception e) {
            isSuccessful = false;
            Diagnostic.logError(Diagnostic.DiagnosticFlag.Other, "Failed to parse userinfo");
        }

        if (mListener != null && isSuccessful)
            mListener.onSuccess();
        else if (mListener != null)
            mListener.onFailure(FAILED);
    }
}
