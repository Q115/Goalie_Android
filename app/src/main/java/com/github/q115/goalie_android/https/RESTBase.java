package com.github.q115.goalie_android.https;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.q115.goalie_android.Constants;

import java.util.HashMap;

import static com.github.q115.goalie_android.Constants.FAILED;
import static com.github.q115.goalie_android.Constants.FAILED_TO_CONNECT;
import static com.github.q115.goalie_android.Constants.FAILED_TO_SEND;

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

abstract class RESTBase<T> implements Response.Listener<T>, Response.ErrorListener {
    interface RESTBaseListener {
        void onFailure(String errMsg);
    }

    private RESTBaseListener mListener;
    protected String mUsername = "";

    protected void setListener(RESTBaseListener listener) {
        mListener = listener;
    }

    protected HashMap<String, String> getDefaultHeaders() {
        HashMap<String, String> mHeaders = new HashMap<>();
        mHeaders.put("Content-Type", "application/json");
        mHeaders.put("Username", mUsername);
        mHeaders.put("Authorization", Constants.KEY);
        mHeaders.put("CustomKey", Constants.CUSTOM_KEY);
        return mHeaders;
    }

    abstract void execute();

    @Override
    public abstract void onResponse(T response);

    @Override
    public void onErrorResponse(VolleyError error) {
        if (mListener == null)
            return;
        if (error.networkResponse == null) {
            mListener.onFailure(FAILED_TO_CONNECT);
        } else if (error.networkResponse.headers != null
                && error.networkResponse.headers.containsKey("response")) {
            String msgErr = error.networkResponse.headers.get("response") == null ? FAILED
                    : error.networkResponse.headers.get("response");
            mListener.onFailure(msgErr);
        } else {
            mListener.onFailure(FAILED_TO_SEND);
        }
    }
}
