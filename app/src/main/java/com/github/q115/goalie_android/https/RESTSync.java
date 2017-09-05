package com.github.q115.goalie_android.https;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.GoalFeed;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.github.q115.goalie_android.Constants.ASYNC_CONNECTION_EXTENDED_TIMEOUT;
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

public class RESTSync extends RESTBase<String> {
    private RESTSync.Listener mListener;
    private long mLastSyncedTimeEpoch;
    private static boolean isSyncing;

    public static boolean isSyncing() {
        return isSyncing;
    }

    public RESTSync(String username, long lastSyncedTimeEpoch) {
        this.mUsername = username;
        mLastSyncedTimeEpoch = lastSyncedTimeEpoch;
    }

    public interface Listener extends RESTBaseListener {
        void onSuccess();

        void onFailure(String errMsg);
    }

    public void setListener(RESTSync.Listener mList) {
        super.setListener(mList);
        this.mListener = mList;
    }

    public void execute() {
        final String url = URL + "/sync";
        isSyncing = true;
        StringRequest req = new StringRequest(Request.Method.POST, url, this, this) {
            @Override
            public HashMap<String, String> getHeaders() {
                return getDefaultHeaders();
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", mUsername);
                params.put("lastSyncedTime", String.valueOf(mLastSyncedTimeEpoch));
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
        isSyncing = false;
        super.onErrorResponse(error);
    }

    @Override
    public void onResponse(String response) {
        boolean isSuccessfull;
        try {
            JSONObject jsonObject = new JSONObject(new String(response.getBytes("ISO-8859-1"), "UTF-8"));

            setupFeeds(jsonObject.getJSONArray("feed"));
            setupMyGoals(jsonObject.getJSONArray("my"));
            setupRequests(jsonObject.getJSONArray("referee"));

            // self info
            UserHelper.getInstance().getOwnerProfile().reputation =
                    jsonObject.getJSONObject("info").getLong("reputation");
            UserHelper.getInstance().setOwnerProfile(UserHelper.getInstance().getOwnerProfile());

            // commit synced time
            PreferenceHelper.getInstance().setLastSyncedTimeEpoch(jsonObject.getLong("time"));
            isSuccessfull = true;
        } catch (Exception e) {
            isSuccessfull = false;
            Diagnostic.logError(Diagnostic.DiagnosticFlag.Other, "Failed to sync onResult");
        }

        isSyncing = false;
        if (mListener != null && isSuccessfull)
            mListener.onSuccess();
        else if (mListener != null)
            mListener.onFailure(FAILED);
    }

    private void setupFeeds(JSONArray jsonAll) throws Exception {
        ArrayList<GoalFeed> goalFeedList = new ArrayList<>();

        for (int i = 0; i < jsonAll.length(); i++) {
            JSONObject jsonObj = jsonAll.getJSONObject(i);
            String guid = jsonObj.getString("guid");
            String createdUsername = jsonObj.getString("createdUsername");
            long wager = jsonObj.getLong("wager");
            long upvoteCount = jsonObj.getLong("upvoteCount");

            int goalCompleteResultInt = jsonObj.getInt("goalCompleteResult");
            Goal.GoalCompleteResult goalCompleteResult = Goal.GoalCompleteResult.None;
            if (goalCompleteResultInt < Goal.GoalCompleteResult.values().length)
                goalCompleteResult = Goal.GoalCompleteResult.values()[goalCompleteResultInt];

            GoalFeed goalFeed = new GoalFeed(guid, wager, createdUsername, upvoteCount, goalCompleteResult);
            goalFeedList.add(goalFeed);
        }

        UserHelper.getInstance().setFeeds(goalFeedList);
    }

    private void setupMyGoals(JSONArray jsonMy) throws Exception {
        HashMap<String, Goal> goalHash = new HashMap<>();

        for (int i = 0; i < jsonMy.length(); i++) {
            JSONObject jsonObj = jsonMy.getJSONObject(i);
            String guid = jsonObj.getString("guid");

            int goalCompleteResultInt = jsonObj.getInt("goalCompleteResult");
            Goal.GoalCompleteResult goalCompleteResult = Goal.GoalCompleteResult.None;
            if (goalCompleteResultInt < Goal.GoalCompleteResult.values().length)
                goalCompleteResult = Goal.GoalCompleteResult.values()[goalCompleteResultInt];

            Goal goal = new Goal(guid, goalCompleteResult);
            goalHash.put(goal.guid, goal);
        }

        // check if activieGoals changed
        for (int i = 0; i < UserHelper.getInstance().getOwnerProfile().activieGoals.size(); i++) {
            Goal goal = UserHelper.getInstance().getOwnerProfile().activieGoals.get(i);
            Goal fetchedGoal = goalHash.get(goal.guid);
            if (fetchedGoal != null) {
                if (goal.goalCompleteResult != fetchedGoal.goalCompleteResult) {
                    goal.goalCompleteResult = fetchedGoal.goalCompleteResult;
                    UserHelper.getInstance().modifyGoal(goal);

                    if (goal.goalCompleteResult != Goal.GoalCompleteResult.Pending
                            && goal.goalCompleteResult != Goal.GoalCompleteResult.Ongoing) {
                        UserHelper.getInstance().getOwnerProfile().activieGoals.remove(i);
                        i--;
                        UserHelper.getInstance().getOwnerProfile().finishedGoals.add(goal);
                    }
                }
            }
        }
    }

    private void setupRequests(JSONArray jsonMyRequests) throws Exception {
        ArrayList<Goal> requests = new ArrayList<>();

        for (int i = 0; i < jsonMyRequests.length(); i++) {
            JSONObject jsonObj = jsonMyRequests.getJSONObject(i);
            String guid = jsonObj.getString("guid");
            String createdUsername = jsonObj.getString("createdUsername");
            String title = jsonObj.getString("title");
            long startDate = jsonObj.getLong("startDate");
            long endDate = jsonObj.getLong("endDate");
            long wager = jsonObj.getLong("wager");
            String encouragement = jsonObj.getString("encouragement");
            long activityDate = jsonObj.getLong("activityDate");

            int goalCompleteResultInt = jsonObj.getInt("goalCompleteResult");
            Goal.GoalCompleteResult goalCompleteResult = Goal.GoalCompleteResult.None;
            if (goalCompleteResultInt < Goal.GoalCompleteResult.values().length)
                goalCompleteResult = Goal.GoalCompleteResult.values()[goalCompleteResultInt];

            Goal goal = new Goal(guid, createdUsername, title, startDate, endDate,
                    wager, encouragement, goalCompleteResult, mUsername, activityDate);
            requests.add(goal);

            if (!UserHelper.getInstance().getAllContacts().containsKey(goal.createdByUsername)) {
                UserHelper.getInstance().addUser(new User(goal.createdByUsername));
            }
        }

        UserHelper.getInstance().setRequests(requests);
    }
}