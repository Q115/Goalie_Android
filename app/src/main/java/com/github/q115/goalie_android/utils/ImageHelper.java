package com.github.q115.goalie_android.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
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
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir("images", Context.MODE_PRIVATE);

        mImageDirectory = directory.getPath();
    }

    public void saveImageToPrivateSorageSync(String imageName, Bitmap bitmapImage, ImageType imageType) {
        if (bitmapImage == null || imageName == null)
            return;

        String filePath = getImagePrivateStorageDirectory(imageName + imageTypeToExtension(imageType));
        try {
            FileOutputStream DestinationFile = new FileOutputStream(filePath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, Constants.ImageJPGQuality, DestinationFile);
            return;
        } catch (FileNotFoundException fnf) {
            Diagnostic.logError(DiagnosticFlag.ImageHelper, "Error saving image to InternalStorage: " + fnf.toString());
        }
    }

    /// <summary>
    /// Load the image from storage by first checking if it's on local storage
    /// </summary>
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

    /// <summary>
    /// Delete the image on private local storage
    /// </summary>
    public boolean deleteImageFromPrivateStorage(String imageName, ImageType imageType) {
        String filePath = getImagePrivateStorageDirectory(imageName + imageTypeToExtension(imageType));
        File file = new File(filePath);
        if (file.exists())
            return file.delete();
        else
            return false;
    }

    /// <summary>
    /// Check if given image is already on phone
    /// </summary>
    public boolean isImageOnPrivateStorage(String imageName, ImageType imageType) {
        String filePath = getImagePrivateStorageDirectory(imageName + imageTypeToExtension(imageType));
        return new File(filePath).exists();
    }

    /// <summary>
    /// Path to local image directory
    /// </summary>
    /// <returns>/data/data/yourapp/app_data/images</returns>
    public String getImagePrivateStorageDirectory(String imageNameWithType) {
        return new File(mImageDirectory, imageNameWithType).getPath();
    }

    /// <summary>
    /// change bitmap to jpg byte array
    /// </summary>
    public static byte[] bitmapToByte(Bitmap img) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, Constants.ImageJPGQuality, stream);
        return stream.toByteArray();
    }

    /// <summary>
    /// Get rounded images
    /// </summary>
    public static Drawable getRoundedCornerBitmap(Resources resources, Bitmap bitmap, float scale) {
        RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(resources, bitmap);
        round.setCornerRadius(round.getIntrinsicWidth() / scale);
        round.setAntiAlias(true);

        return round;
    }

    public static Bitmap drawableToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }

    public static int dpToPx(Resources resources, int dp) {
        return (int) (dp * resources.getDisplayMetrics().density);
    }

    public static int pxToDp(Resources resources, int px) {
        return (int) (px / resources.getDisplayMetrics().density);
    }

    /// <summary>
    /// Instead of loading the whole image, just a sample to avoid Out Of Memory error
    ///     width & height in pixel
    /// </summary>
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = getInstance().calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /// <summary>
    /// Instead of loading the whole image, just a sample to avoid Out Of Memory error
    ///     width & height in pixel
    /// </summary>
    public static Bitmap decodeSampledBitmapFromUri(ContentResolver contentResolver, android.net.Uri uri, int width, int height) {
        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        try {
            BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options);

            // Calculate inSampleSize
            options.inSampleSize = getInstance().calculateInSampleSize(options, width, height);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options);
        } catch (FileNotFoundException fnfe) {
            Diagnostic.logError(DiagnosticFlag.ImageHelper, "decodeSampledBitmapFromUri() FAILED" + fnfe.toString());
        }
        return null;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

    /// <summary>
    /// String represenation of this image file extendsion. ie: .png
    /// </summary>
    private String imageTypeToExtension(ImageType imageType) {
        switch (imageType) {
            case PNG:
                return ".png";
            default:
                return "";
        }
    }
}
