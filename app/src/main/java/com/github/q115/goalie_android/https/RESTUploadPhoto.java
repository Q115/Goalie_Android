package com.github.q115.goalie_android.https;

import android.graphics.Bitmap;
import android.util.ArrayMap;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.utils.ImageHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import static com.github.q115.goalie_android.Constants.FAILED;
import static com.github.q115.goalie_android.Constants.FAILED_TO_CONNECT;
import static com.github.q115.goalie_android.Constants.FAILED_TO_Send;
import static com.github.q115.goalie_android.Constants.URL;


/**
 * Created by Qi on 11/27/2016.
 */

public class RESTUploadPhoto {
    private RESTUploadPhoto.Listener mList;

    private Bitmap mProfileImage;
    private String mUsername;
    private String mBoundary;

    public RESTUploadPhoto(Bitmap profileImage, String username) {
        this.mProfileImage = profileImage;
        this.mUsername = username;
        this.mBoundary = "ANDROID_BOUNDARY_STRING";
    }

    public interface Listener {
        void onSuccess();

        void onFailure(String errMsg);
    }

    public void setListener(RESTUploadPhoto.Listener mList) {
        this.mList = mList;
    }

    public void execute() {

        final String url = URL + "/uploadphoto";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UserHelper.getInstance().getOwnerProfile().profileBitmapImage = mProfileImage;
                        ImageHelper.getInstance().deleteImageFromPrivateStorage(mUsername + "Temp", ImageHelper.ImageType.PNG);
                        ImageHelper.getInstance().saveImageToPrivateSorageSync(mUsername, mProfileImage, ImageHelper.ImageType.PNG);
                        mList.onSuccess();
                    }
                },
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
            public ArrayMap<String, String> getHeaders() {
                ArrayMap<String, String> mHeaders = new ArrayMap<>();
                mHeaders.put("username", mUsername);
                mHeaders.put("Content-Type", "multipart/form-data;boundary=" + mBoundary);
                return mHeaders;
            }

            @Override
            public byte[] getBody() {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    byte[] image = getStringImage(mProfileImage);
                    byteArrayOutputStream.write(("\r\n" + "--" + mBoundary + "\r\n").getBytes(Charset.defaultCharset()));
                    byteArrayOutputStream.write(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\n", mUsername, mUsername).getBytes(Charset.defaultCharset()));
                    byteArrayOutputStream.write(("Content-Type: application/octet-stream\r\n\r\n").getBytes(Charset.defaultCharset()));
                    byteArrayOutputStream.write(image);
                    byteArrayOutputStream.write(("\r\n" + "--" + mBoundary + "\r\n").getBytes(Charset.defaultCharset()));
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

    private byte[] getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 99, baos);
        return baos.toByteArray();
    }
}
