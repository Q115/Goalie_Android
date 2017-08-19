package com.github.q115.goalie_android.https;

import android.util.ArrayMap;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
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
import static com.github.q115.goalie_android.Constants.FAILED_TO_CONNECT;
import static com.github.q115.goalie_android.Constants.FAILED_TO_Send;
import static com.github.q115.goalie_android.Constants.URL;

/**
 * Created by Qi on 8/10/2017.
 */

public class RESTSync {
    private RESTSync.Listener mList;
    private String mUsername;
    private long mLastSyncedTimeEpoch;
    private static boolean isSyncing;

    public static boolean isSyncing() {
        return isSyncing;
    }

    public RESTSync(String username, long lastSyncedTimeEpoch) {
        mUsername = username;
        mLastSyncedTimeEpoch = lastSyncedTimeEpoch;
    }

    public interface Listener {
        void onSuccess();

        void onFailure(String errMsg);
    }

    public void setListener(RESTSync.Listener mList) {
        this.mList = mList;
    }

    public void execute() {
        final String url = URL + "/sync";
        isSyncing = true;
        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<GoalFeed> goalFeedList = new ArrayList<>();
                HashMap<String, Goal> goalHash = new HashMap<>();
                try {
                    JSONObject jsonObject = new JSONObject(new String(response.getBytes("ISO-8859-1"), "UTF-8"));

                    // feeds
                    JSONArray jsonAll = jsonObject.getJSONArray("feed");
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

                    // my goals
                    JSONArray jsonMy = jsonObject.getJSONArray("my");
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

                    // requests
                    UserHelper.getInstance().getRequests().clear();
                    JSONArray jsonMyRequests = jsonObject.getJSONArray("referee");
                    for (int i = 0; i < jsonMyRequests.length(); i++) {
                        JSONObject jsonObj = jsonMyRequests.getJSONObject(i);
                        String guid = jsonObj.getString("guid");
                        String createdUsername = jsonObj.getString("createdUsername");
                        String title = jsonObj.getString("title");
                        long startDate = jsonObj.getLong("startDate");
                        long endDate = jsonObj.getLong("endDate");
                        long wager = jsonObj.getLong("wager");
                        String encouragement = jsonObj.getString("encouragement");

                        int goalCompleteResultInt = jsonObj.getInt("goalCompleteResult");
                        Goal.GoalCompleteResult goalCompleteResult = Goal.GoalCompleteResult.None;
                        if (goalCompleteResultInt < Goal.GoalCompleteResult.values().length)
                            goalCompleteResult = Goal.GoalCompleteResult.values()[goalCompleteResultInt];

                        Goal goal = new Goal(guid, createdUsername, title, startDate, endDate,
                                wager, encouragement, goalCompleteResult, mUsername);
                        UserHelper.getInstance().getRequests().add(goal);

                        if (!UserHelper.getInstance().getAllContacts().containsKey(goal.createdByUsername)) {
                            UserHelper.getInstance().addUser(new User(goal.createdByUsername));
                        }
                    }

                    // commit time
                    PreferenceHelper.getInstance().setLastSyncedTimeEpoch(jsonObject.getLong("time"));
                } catch (Exception e) {
                    Diagnostic.logError(Diagnostic.DiagnosticFlag.Other, "Failed to sync onResult");
                }

                // check if activieGoals changed
                for (Goal goal : UserHelper.getInstance().getOwnerProfile().activieGoals) {
                    Goal fetchedGoal = goalHash.get(goal.guid);
                    if (fetchedGoal != null) {
                        if (goal.goalCompleteResult != fetchedGoal.goalCompleteResult) {
                            goal.goalCompleteResult = fetchedGoal.goalCompleteResult;
                            UserHelper.getInstance().modifyGoal(goal);

                            if (goal.goalCompleteResult != Goal.GoalCompleteResult.Pending && goal.goalCompleteResult != Goal.GoalCompleteResult.Ongoing) {
                                UserHelper.getInstance().getOwnerProfile().activieGoals.remove(goal);
                                UserHelper.getInstance().getOwnerProfile().activieGoals.add(goal);
                            }
                        }
                    }
                }

                UserHelper.getInstance().setFeeds(goalFeedList);
                isSyncing = false;
                if (mList != null)
                    mList.onSuccess();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isSyncing = false;
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
            public ArrayMap<String, String> getHeaders() {
                ArrayMap<String, String> mHeaders = new ArrayMap<>();
                mHeaders.put("Content-Type", "application/json");
                mHeaders.put("username", mUsername);
                return mHeaders;
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
}