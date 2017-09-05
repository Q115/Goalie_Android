package com.github.q115.goalie_android.https;

import android.graphics.Bitmap;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.utils.ImageHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;

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

public class RESTUploadPhoto extends RESTBase<String> {
    private RESTUploadPhoto.Listener mListener;
    private Bitmap mProfileImage;
    private String mBoundary;

    public RESTUploadPhoto(Bitmap profileImage, String username) {
        this.mProfileImage = profileImage;
        this.mUsername = username;
        this.mBoundary = "ANDROID_BOUNDARY_STRING";
    }

    public interface Listener extends RESTBaseListener {
        void onSuccess();

        void onFailure(String errMsg);
    }

    public void setListener(RESTUploadPhoto.Listener mList) {
        super.setListener(mList);
        this.mListener = mList;
    }

    public void execute() {

        final String url = URL + "/uploadphoto";
        StringRequest req = new StringRequest(Request.Method.POST, url, this, this) {
            @Override
            public HashMap<String, String> getHeaders() {
                HashMap<String, String> headers = getDefaultHeaders();
                headers.put("Content-Type", "multipart/form-data;boundary=" + mBoundary);
                return headers;
            }

            @Override
            public byte[] getBody() {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    String imageDispositionString = String.format("Content-Disposition: form-data; " +
                            "name=\"%s\"; filename=\"%s.png\"\r\n", mUsername, mUsername);
                    byte[] imageDisposition = getBytesFromString(imageDispositionString);
                    byte[] imageBoundary = getBytesFromString("\r\n" + "--" + mBoundary + "\r\n");
                    byte[] imageType = getBytesFromString("Content-Type: application/octet-stream\r\n\r\n");
                    byte[] image = getBytesFromBitmap(mProfileImage);

                    byteArrayOutputStream.write(imageBoundary);
                    byteArrayOutputStream.write(imageDisposition);
                    byteArrayOutputStream.write(imageType);
                    byteArrayOutputStream.write(image);
                    byteArrayOutputStream.write(imageBoundary);
                } catch (Exception e) {
                    Diagnostic.logError(Diagnostic.DiagnosticFlag.Other, "Failed to send body of photo");
                }

                return byteArrayOutputStream.toByteArray();
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                Constants.ASYNC_CONNECTION_EXTENDED_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                0));
        VolleyRequestQueue.getInstance().addToRequestQueue(req);
    }

    private byte[] getBytesFromBitmap(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 99, baos);
        return baos.toByteArray();
    }

    private byte[] getBytesFromString(String string) {
        return string.getBytes(Charset.defaultCharset());
    }

    @Override
    public void onResponse(String response) {
        UserHelper.getInstance().getOwnerProfile().profileBitmapImage = mProfileImage;
        ImageHelper.getInstance().deleteImageFromPrivateStorage(mUsername + "Temp",
                ImageHelper.ImageType.PNG);
        ImageHelper.getInstance().saveImageToPrivateSorageSync(mUsername, mProfileImage,
                ImageHelper.ImageType.PNG);

        if (mListener != null)
            mListener.onSuccess();
    }
}
