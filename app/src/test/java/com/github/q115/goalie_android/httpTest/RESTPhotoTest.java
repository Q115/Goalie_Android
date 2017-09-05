package com.github.q115.goalie_android.httpTest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.github.q115.goalie_android.https.RESTGetPhoto;
import com.github.q115.goalie_android.https.RESTUploadPhoto;
import com.github.q115.goalie_android.utils.ImageHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.Pair;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;

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
@RunWith(RobolectricTestRunner.class)
public class RESTPhotoTest extends BaseREST {
    private Bitmap imageDownloaded;

    @Test
    public void downloadUploadPhoto() throws Exception {
        // photo doesn't exist
        downloadPhotoNotFound(username);

        // upload
        uploadPhoto(username);
        assertTrue(ImageHelper.getInstance().isImageOnPrivateStorage(username, ImageHelper.ImageType.PNG));
        assertTrue(UserHelper.getInstance().getOwnerProfile().profileBitmapImage != null);
        ImageHelper.getInstance().deleteImageFromPrivateStorage(username, ImageHelper.ImageType.PNG);

        // photo now should exist
        assertTrue(downloadPhoto(username));
        assertTrue(ImageHelper.getInstance().isImageOnPrivateStorage(username, ImageHelper.ImageType.PNG));
        assertTrue(UserHelper.getInstance().getOwnerProfile().profileBitmapImage != null);
    }

    @Test
    public void bitmapToByte() throws Exception {
        Bitmap newImage = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        RESTUploadPhoto sm = new RESTUploadPhoto(newImage, username);

        // compressed image shouldn't be bigger
        assertTrue(newImage.getByteCount() >= sm.getBytesFromBitmap(newImage).length);
    }

    private void downloadPhotoNotFound(String username) throws Exception {
        Pair<Integer, RESTGetPhoto.Listener> pair = createDownloadListener();

        RESTGetPhoto sm = new RESTGetPhoto(username);
        sm.setListener(pair.second);
        sm.execute();

        while (!isOperationCompleteList.get(pair.first)) {
            Thread.sleep(1000);
        }

        verify(pair.second).onFailure("Not Found");
    }

    private boolean downloadPhoto(String username) throws Exception {
        Pair<Integer, RESTGetPhoto.Listener> pair = createDownloadListener();

        RESTGetPhoto sm = new RESTGetPhoto(username);
        sm.setListener(pair.second);
        sm.execute();

        while (!isOperationCompleteList.get(pair.first)) {
            Thread.sleep(1000);
        }

        verify(pair.second).onSuccess(imageDownloaded);
        return imageDownloaded != null;
    }

    private void uploadPhoto(String username) throws Exception {
        Pair<Integer, RESTUploadPhoto.Listener> pair = createUploadListener();

        Bitmap bitmap = BitmapFactory.decodeFile("../test.png");
        RESTUploadPhoto sm = new RESTUploadPhoto(bitmap, username);
        sm.setListener(pair.second);
        sm.execute();

        while (!isOperationCompleteList.get(pair.first)) {
            Thread.sleep(1000);
        }

        verify(pair.second).onSuccess();
    }

    private synchronized Pair<Integer, RESTGetPhoto.Listener> createDownloadListener() {
        final Integer index = isOperationCompleteList.size();
        isOperationCompleteList.add(false);

        RESTGetPhoto.Listener listener = Mockito.spy(new RESTGetPhoto.Listener() {
            @Override
            public void onSuccess(Bitmap image) {
                imageDownloaded = image;
                isOperationCompleteList.set(index, true);
            }

            @Override
            public void onFailure(String errMsg) {
                isOperationCompleteList.set(index, true);
            }
        });

        return new Pair<>(index, listener);
    }

    private synchronized Pair<Integer, RESTUploadPhoto.Listener> createUploadListener() {
        final Integer index = isOperationCompleteList.size();
        isOperationCompleteList.add(false);

        RESTUploadPhoto.Listener listener = Mockito.spy(new RESTUploadPhoto.Listener() {
            @Override
            public void onSuccess() {
                isOperationCompleteList.set(index, true);
            }

            @Override
            public void onFailure(String errMsg) {
                isOperationCompleteList.set(index, true);
            }
        });

        return new Pair<>(index, listener);
    }
}
