package com.github.q115.goalie_android.https;

import android.util.ArrayMap;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.GoalFeed;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.github.q115.goalie_android.Constants.ASYNC_CONNECTION_EXTENDED_TIMEOUT;
import static com.github.q115.goalie_android.Constants.FAILED;
import static com.github.q115.goalie_android.Constants.FAILED_TO_CONNECT;
import static com.github.q115.goalie_android.Constants.FAILED_TO_Send;
import static com.github.q115.goalie_android.Constants.URL;

/**
 * Created by Qi on 8/13/2017.
 */

public class RESTGetFeeds {
    private String mUsername;
    private RESTGetFeeds.Listener mList;

    public RESTGetFeeds(String username) {
        this.mUsername = username;
    }

    public interface Listener {
        void onSuccess(ArrayList<GoalFeed> goalFeedList);

        void onFailure(String errMsg);
    }

    public void setListener(RESTGetFeeds.Listener mList) {
        this.mList = mList;
    }

    public void execute() {
        String url;
        try {
            url = URL + "/getfeeds?username=" + URLEncoder.encode(mUsername, "utf-8");
        } catch (UnsupportedEncodingException e) {
            url = URL + "/getfeeds?username=" + mUsername;
        }

        StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ArrayList<GoalFeed> goalFeedList = new ArrayList<>();
                    JSONArray jsonObject = new JSONArray(new String(response.getBytes("ISO-8859-1"), "UTF-8"));
                    for (int i = 0; i < jsonObject.length(); i++) {
                        JSONObject jsonObj = jsonObject.getJSONObject(i);
                        String guid = jsonObj.getString("guid");
                        String createdUsername = jsonObj.getString("createdUsername");
                        long wager = jsonObj.getLong("wager");
                        long upvoteCount = jsonObj.getLong("upvoteCount");
                        Goal.GoalCompleteResult goalCompleteResult = Goal.GoalCompleteResult.values()[jsonObj.getInt("goalCompleteResult")];

                        GoalFeed goalFeed = new GoalFeed(guid, wager, createdUsername, upvoteCount, goalCompleteResult);
                        goalFeedList.add(goalFeed);
                    }

                    mList.onSuccess(goalFeedList);
                } catch (Exception e) {
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
            public ArrayMap<String, String> getHeaders() {
                ArrayMap<String, String> mHeaders = new ArrayMap<>();
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
