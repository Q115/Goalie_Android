package com.github.q115.goalie_android.https;

import android.graphics.Bitmap;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.ImageRequest;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.ImageHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

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

public class RESTGetPhoto extends RESTBase<Bitmap> {
    private RESTGetPhoto.Listener mListener;

    public RESTGetPhoto(String username) {
        this.mUsername = username;
    }

    public interface Listener extends RESTBaseListener {
        void onSuccess(Bitmap photo);

        void onFailure(String errMsg);
    }

    public void setListener(RESTGetPhoto.Listener mList) {
        super.setListener(mList);
        this.mListener = mList;
    }

    public static String getURL(String username) {
        try {
            return URL + "/photo/" + URLEncoder.encode(username, "utf-8") + ".png";
        } catch (UnsupportedEncodingException e) {
            return URL + "/photo/" + username + ".png";
        }
    }

    public void execute() {
        ImageRequest req = new ImageRequest(getURL(mUsername), this, 0, 0, null, null, this) {
            @Override
            public HashMap<String, String> getHeaders() {
                return getDefaultHeaders();
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                ASYNC_CONNECTION_EXTENDED_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                0));

        VolleyRequestQueue.getInstance().addToRequestQueue(req);
    }

    @Override
    public void onResponse(Bitmap photo) {
        if (photo != null) {
            ImageHelper.getInstance().saveImageToPrivateSorageSync(mUsername, photo,
                    ImageHelper.ImageType.PNG);

            User user = UserHelper.getInstance().getAllContacts().get(mUsername);
            if (user == null) {
                User newUser = new User(mUsername);
                UserHelper.getInstance().addUser(newUser);
                newUser.profileBitmapImage = photo;
            } else
                user.profileBitmapImage = photo;
        }

        if (mListener != null)
            mListener.onSuccess(photo);
    }
}