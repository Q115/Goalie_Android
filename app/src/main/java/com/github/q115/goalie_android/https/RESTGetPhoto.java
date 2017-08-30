package com.github.q115.goalie_android.https;

import android.graphics.Bitmap;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.ImageHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

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

public class RESTGetPhoto {
    private RESTGetPhoto.Listener mList;
    private String mUsername;

    public RESTGetPhoto(String username) {
        this.mUsername = username;
    }

    public interface Listener {
        void onSuccess(Bitmap photo);

        void onFailure(String errMsg);
    }

    public void setListener(RESTGetPhoto.Listener mList) {
        this.mList = mList;
    }

    public static String getURL(String username) {
        try {
            return URL + "/photo/" + URLEncoder.encode(username, "utf-8") + ".png";
        } catch (UnsupportedEncodingException e) {
            return URL + "/photo/" + username + ".png";
        }
    }

    public void execute() {
        ImageRequest req = new ImageRequest(getURL(mUsername),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap photo) {
                        if (photo != null) {
                            ImageHelper.getInstance().saveImageToPrivateSorageSync(mUsername, photo, ImageHelper.ImageType.PNG);

                            User user = UserHelper.getInstance().getAllContacts().get(mUsername);
                            if (user != null)
                                user.profileBitmapImage = photo;
                        }

                        if (mList != null)
                            mList.onSuccess(photo);
                    }
                }, 0, 0, null, null,
                new Response.ErrorListener() {
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
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                ASYNC_CONNECTION_EXTENDED_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                0));
        VolleyRequestQueue.getInstance().addToRequestQueue(req);
    }
}