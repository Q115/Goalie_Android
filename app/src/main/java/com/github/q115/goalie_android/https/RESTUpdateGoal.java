package com.github.q115.goalie_android.https;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.utils.UserHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.github.q115.goalie_android.Constants.ASYNC_CONNECTION_EXTENDED_TIMEOUT;
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

public class RESTUpdateGoal {
    private RESTUpdateGoal.Listener mList;
    private String mUsername;
    private String mGuid;
    private Goal.GoalCompleteResult mGoalCompleteResult;

    public RESTUpdateGoal(String username, String guid, Goal.GoalCompleteResult goalCompleteResult) {
        mUsername = username;
        mGuid = guid;
        mGoalCompleteResult = goalCompleteResult;
    }

    public interface Listener {
        void onSuccess();

        void onFailure(String errMsg);
    }

    public void setListener(RESTUpdateGoal.Listener mList) {
        this.mList = mList;
    }

    public void execute() {
        final String url = URL + "/updategoal";
        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // remove goal if completed
                if (mGoalCompleteResult == Goal.GoalCompleteResult.Failed || mGoalCompleteResult == Goal.GoalCompleteResult.Success
                        || mGoalCompleteResult == Goal.GoalCompleteResult.Cancelled) {
                    ArrayList<Goal> list = UserHelper.getInstance().getRequests();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).guid.equals(mGuid)) {
                            list.get(i).activityDate = System.currentTimeMillis();
                            list.remove(i);
                            break;
                        }
                    }
                } else {
                    ArrayList<Goal> list = UserHelper.getInstance().getRequests();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).guid.equals(mGuid)) {
                            list.get(i).goalCompleteResult = mGoalCompleteResult;
                            list.get(i).activityDate = System.currentTimeMillis();
                            break;
                        }
                    }
                }

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
                mHeaders.put("Username", mUsername);
                mHeaders.put("Authorization", Constants.KEY);
                return mHeaders;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", mUsername);
                params.put("guid", mGuid);
                params.put("goalCompleteResult", String.valueOf(mGoalCompleteResult.ordinal()));
                return new JSONObject(params).toString().getBytes();
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                ASYNC_CONNECTION_EXTENDED_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                0));
        VolleyRequestQueue.getInstance().addToRequestQueue(req);
    }
}