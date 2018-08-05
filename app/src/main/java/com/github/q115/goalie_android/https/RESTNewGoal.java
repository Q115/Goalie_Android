package com.github.q115.goalie_android.https;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.GoalHelper;
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

public class RESTNewGoal extends RESTBase<String> {
    private RESTNewGoal.Listener mListener;
    private final String mTitle;
    private final long mStart;
    private final long mEnd;
    private final long mWager;
    private final String mEncouragement;
    private final String mReferee;
    private final boolean isGoalPublic;

    public RESTNewGoal(String username, String title, long start, long end, long wager,
                       String encouragement, String referee, boolean isGoalPublic) {
        mUsername = username;
        mStart = start;
        mEnd = end;
        mWager = wager;
        mEncouragement = encouragement;
        mReferee = referee;
        mTitle = title;
        this.isGoalPublic = isGoalPublic;
    }

    public interface Listener extends RESTBaseListener {
        void onSuccess(String guid);

        void onFailure(String errMsg);
    }

    public void setListener(RESTNewGoal.Listener mList) {
        super.setListener(mList);
        this.mListener = mList;
    }

    public void execute() {
        final String url = URL + "/newgoal";

        StringRequest req = new StringRequest(Request.Method.POST, url, this, this) {
            @Override
            public HashMap<String, String> getHeaders() {
                return getDefaultHeaders();
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", mUsername);
                params.put("start", String.valueOf(mStart));
                params.put("end", String.valueOf(mEnd));
                params.put("wager", String.valueOf(mWager));
                params.put("encouragement", mEncouragement);
                params.put("referee", mReferee);
                params.put("title", mTitle);
                params.put("isGoalPublic", isGoalPublic ? "1" : "0");
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
    public void onResponse(String guid) {
        if (UserHelper.getInstance().getAllContacts().get(mReferee) == null) {
            UserHelper.getInstance().addUser(new User(mReferee));

            RESTGetPhoto sm = new RESTGetPhoto(mReferee);
            sm.setListener(null);
            sm.execute();
        }

        Goal goal = new Goal(guid, mUsername, mTitle, mStart, mEnd, mWager, mEncouragement,
                Goal.GoalCompleteResult.Pending, mReferee, System.currentTimeMillis());
        GoalHelper.getInstance().addGoal(goal);

        if (!mReferee.equals(mUsername)) {
            UserHelper.getInstance().getOwnerProfile().reputation -= mWager;
            UserHelper.getInstance().setOwnerProfile(UserHelper.getInstance().getOwnerProfile());
        }

        if (mListener != null)
            mListener.onSuccess(guid);
    }
}
