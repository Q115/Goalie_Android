package com.github.q115.goalie_android.httpTest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.https.RESTGetPhoto;
import com.github.q115.goalie_android.https.RESTUploadPhoto;
import com.github.q115.goalie_android.utils.ImageHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.UUID;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

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
public class RESTPhotoTest extends BaseTest {
    private int operation1;
    private int operation2;

    @Test(timeout = 20000)
    public void downloadUploadPhoto() throws Exception {
        String username = UUID.randomUUID().toString();

        // register user
        RESTRegisterTest.registerUser(username, null);
        Thread.sleep(1000);

        // photo doesn't exist
        assertFalse(downloadPhoto(username));

        // upload test
        uploadPhoto(username);
        assertTrue(ImageHelper.getInstance().isImageOnPrivateStorage(username, ImageHelper.ImageType.PNG));
        assertTrue(UserHelper.getInstance().getOwnerProfile().profileBitmapImage != null);
        ImageHelper.getInstance().deleteImageFromPrivateStorage(username, ImageHelper.ImageType.PNG);

        // photo now should exist
        assertTrue(downloadPhoto(username));
        assertTrue(ImageHelper.getInstance().isImageOnPrivateStorage(username, ImageHelper.ImageType.PNG));
        assertTrue(UserHelper.getInstance().getOwnerProfile().profileBitmapImage != null);
    }

    private void uploadPhoto(String username) throws Exception {
        operation1 = 1;

        Bitmap bitmap = BitmapFactory.decodeFile("../test.png");
        RESTUploadPhoto sm = new RESTUploadPhoto(bitmap, username);
        sm.setListener(new RESTUploadPhoto.Listener() {
            @Override
            public void onSuccess() {
                operation1--;
            }

            @Override
            public void onFailure(String errMsg) {
                operation1--;
            }
        });
        sm.execute();

        while (operation1 != 0)
            Thread.sleep(1000);
    }


    private boolean downloadPhoto(String username) throws Exception {
        operation2 = 1;
        RESTGetPhoto sm = new RESTGetPhoto(username);
        sm.setListener(new RESTGetPhoto.Listener() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                operation2--;
            }

            @Override
            public void onFailure(String errMsg) {
                operation2 -= 2;
            }
        });
        sm.execute();

        while (operation2 > 0)
            Thread.sleep(1000);

        return operation2 == 0;
    }
}
