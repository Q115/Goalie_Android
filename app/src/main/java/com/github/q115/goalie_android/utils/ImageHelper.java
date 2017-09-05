package com.github.q115.goalie_android.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.Diagnostic.DiagnosticFlag;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

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
public class ImageHelper {
    public enum ImageType {
        PNG
    }

    private static ImageHelper mInstance;

    private String mImageDirectory;

    public static synchronized ImageHelper getInstance() {
        if (mInstance == null) {
            mInstance = new ImageHelper();
        }

        return mInstance;
    }

    public void initialize(Context context) {
        if (context != null)
            mImageDirectory = new File(context.getFilesDir() + "/images").getPath();
    }

    public void saveImageToPrivateSorageSync(String imageName, Bitmap bitmapImage, ImageType imageType) {
        if (bitmapImage == null || imageName == null)
            return;

        String filePath = getImagePrivateStorageDirectory(imageName + imageTypeToExtension(imageType));
        File newFile = new File(filePath);
        if (!newFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            newFile.getParentFile().mkdirs();
        }

        try {
            FileOutputStream DestinationFile = new FileOutputStream(filePath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, Constants.IMAGE_JPG_QUALITY, DestinationFile);
        } catch (FileNotFoundException fnf) {
            Diagnostic.logError(DiagnosticFlag.ImageHelper, "Error saving image to InternalStorage: " + fnf.toString());
        }
    }

    public Bitmap loadImageFromPrivateSorageSync(String imageName, ImageType imageType) {
        if (!isImageOnPrivateStorage(imageName, imageType)) {
            return null;
        }

        String filePath = getImagePrivateStorageDirectory(imageName + imageTypeToExtension(imageType));
        try {
            FileInputStream fs = new FileInputStream(filePath);
            return BitmapFactory.decodeStream(fs);
        } catch (FileNotFoundException fnf) {
            Diagnostic.logError(DiagnosticFlag.ImageHelper, "Error load image from InternalStorage (sync): " + fnf.toString());
            return null;
        }
    }

    public boolean deleteImageFromPrivateStorage(String imageName, ImageType imageType) {
        String filePath = getImagePrivateStorageDirectory(imageName + imageTypeToExtension(imageType));
        File file = new File(filePath);
        return file.exists() && file.delete();
    }

    public boolean isImageOnPrivateStorage(String imageName, ImageType imageType) {
        String filePath = getImagePrivateStorageDirectory(imageName + imageTypeToExtension(imageType));
        return new File(filePath).exists();
    }

    // Path to local image directory
    // <returns>/data/data/yourapp/files/images</returns>
    public String getImagePrivateStorageDirectory(String imageNameWithType) {
        return new File(mImageDirectory, imageNameWithType).getPath();
    }

    public static Drawable getRoundedCornerDrawable(Resources resources, Bitmap bitmap, float scale) {
        if (resources == null || bitmap == null)
            return null;
        RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(resources, bitmap);
        round.setCornerRadius(round.getIntrinsicWidth() / scale);
        round.setAntiAlias(true);

        return round;
    }

    public static int dpToPx(Resources resources, int dp) {
        return (int) (dp * resources.getDisplayMetrics().density);
    }

    public static int pxToDp(Resources resources, int px) {
        return (int) (px / resources.getDisplayMetrics().density);
    }

    // Instead of loading the whole image, just a sample to avoid Out Of Memory error. width & height in pixel
    public static Bitmap decodeSampledBitmapFromUri(ContentResolver contentResolver,
                                                    android.net.Uri uri, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options);
            options.inSampleSize = getInstance().calculateInSampleSize(options, width, height);

            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options);
        } catch (FileNotFoundException fnfe) {
            Diagnostic.logError(DiagnosticFlag.ImageHelper, "decodeSampledBitmapFromUri() FAILED" + fnfe.toString());
        }
        return null;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if(options == null)
            return 1;

        // Raw height and width of image
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // String represenation of this image file extension. ie: .png
    private String imageTypeToExtension(ImageType imageType) {
        switch (imageType) {
            case PNG:
                return ".png";
            default:
                return "";
        }
    }
}
