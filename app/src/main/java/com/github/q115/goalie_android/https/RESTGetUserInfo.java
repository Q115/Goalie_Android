package com.github.q115.goalie_android.https;

import android.util.ArrayMap;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.UserHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import static com.github.q115.goalie_android.Constants.ASYNC_CONNECTION_EXTENDED_TIMEOUT;
import static com.github.q115.goalie_android.Constants.FAILED;
import static com.github.q115.goalie_android.Constants.FAILED_TO_CONNECT;
import static com.github.q115.goalie_android.Constants.FAILED_TO_Send;
import static com.github.q115.goalie_android.Constants.URL;

/**
 * Created by Qi on 3/5/2017.
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
                        finishedGoalsList.add(new Goal(guid, mUsername, title, start, end, wager, encouragement, Goal.GoalCompleteResult.Success, referee));
                    }

                    Collections.sort(finishedGoalsList, new Comparator<Goal>() {
                        @Override
                        public int compare(Goal a1, Goal a2) {
                            return (int) (a2.startDate - a1.startDate);
                        }
                    });

                    User s = new User(mUsername, bio, reputation, lastPhotoModifiedTime);
                    s.finishedGoals = finishedGoalsList;
                    UserHelper.getInstance().addUser(s);

                    if (mList != null)
                        mList.onSuccess();
                } catch (Exception e) {
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
                mHeaders.put("username", mUsername);
                return mHeaders;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                ASYNC_CONNECTION_EXTENDED_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                0));
        VolleyRequestQueue.getInstance().addToRequestQueue(req);
    }
}
