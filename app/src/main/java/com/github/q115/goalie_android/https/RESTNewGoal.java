package com.github.q115.goalie_android.https;

import android.util.ArrayMap;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.utils.UserHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.github.q115.goalie_android.Constants.ASYNC_CONNECTION_EXTENDED_TIMEOUT;
import static com.github.q115.goalie_android.Constants.FAILED;
import static com.github.q115.goalie_android.Constants.FAILED_TO_CONNECT;
import static com.github.q115.goalie_android.Constants.FAILED_TO_Send;
import static com.github.q115.goalie_android.Constants.URL;

/**
 * Created by Qi on 8/12/2017.
 */

public class RESTNewGoal {
    private RESTNewGoal.Listener mList;
    private String mUsername;
    private String mTitle;
    private long mStart;
    private long mEnd;
    private long mWager;
    private String mEncouragement;
    private String mReferee;

    public RESTNewGoal(String username, String title, long start, long end, long wager, String encouragement, String referee) {
        mUsername = username;
        mStart = start;
        mEnd = end;
        mWager = wager;
        mEncouragement = encouragement;
        mReferee = referee;
        mTitle = title;
    }

    public interface Listener {
        void onSuccess();

        void onFailure(String errMsg);
    }

    public void setListener(RESTNewGoal.Listener mList) {
        this.mList = mList;
    }

    public void execute() {
        final String url = URL + "/newgoal";
        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Goal goal = new Goal(response, mUsername, mTitle, mStart, mEnd, mWager, mEncouragement, Goal.GoalCompleteResult.Ongoing, mReferee);
                UserHelper.getInstance().addGoal(goal);

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
                params.put("start", String.valueOf(mStart));
                params.put("end", String.valueOf(mEnd));
                params.put("wager", String.valueOf(mWager));
                params.put("encouragement", mEncouragement);
                params.put("referee", mReferee);
                params.put("title", mTitle);
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
