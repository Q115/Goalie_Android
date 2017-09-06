package com.github.q115.goalie_android.https;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.utils.GoalHelper;

import org.json.JSONObject;

import java.util.ArrayList;
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

public class RESTUpdateGoal extends RESTBase<String> {
    private RESTUpdateGoal.Listener mListener;
    private String mGuid;
    private Goal.GoalCompleteResult mGoalCompleteResult;

    public RESTUpdateGoal(String username, String guid, Goal.GoalCompleteResult goalCompleteResult) {
        mUsername = username;
        mGuid = guid;
        mGoalCompleteResult = goalCompleteResult;
    }

    public interface Listener extends RESTBaseListener {
        void onSuccess();

        void onFailure(String errMsg);
    }

    public void setListener(RESTUpdateGoal.Listener mList) {
        super.setListener(mList);
        this.mListener = mList;
    }

    public void execute() {
        final String url = URL + "/updategoal";
        StringRequest req = new StringRequest(Request.Method.POST, url, this, this) {
            @Override
            public HashMap<String, String> getHeaders() {
                return getDefaultHeaders();
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

    @Override
    public void onResponse(String response) {
        // remove goal if completed
        if (mGoalCompleteResult == Goal.GoalCompleteResult.Failed
                || mGoalCompleteResult == Goal.GoalCompleteResult.Success
                || mGoalCompleteResult == Goal.GoalCompleteResult.Cancelled) {
            ArrayList<Goal> list = GoalHelper.getInstance().getRequests();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).guid.equals(mGuid)) {
                    list.get(i).activityDate = System.currentTimeMillis();
                    list.remove(i);
                    break;
                }
            }
        } else {
            ArrayList<Goal> list = GoalHelper.getInstance().getRequests();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).guid.equals(mGuid)) {
                    list.get(i).goalCompleteResult = mGoalCompleteResult;
                    list.get(i).activityDate = System.currentTimeMillis();
                    break;
                }
            }
        }

        if (mListener != null)
            mListener.onSuccess();
    }
}