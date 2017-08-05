package com.github.q115.goalie_android;

import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.github.q115.goalie_android.utils.ImageHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Qi on 8/3/2017.
 */

@RunWith(AndroidJUnit4.class)
public class ImageHelperInstrumentedTest {
    @Before
    public void init() throws Exception {
        ImageHelper.getInstance().initialize(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void initalization() throws Exception {
        assertNotNull(ImageHelper.getInstance().getImagePrivateStorageDirectory(""));
    }

    @Test
    public void saveAndDeleteImage() throws Exception {
        String imageName = "newImage";
        ImageHelper.getInstance().deleteImageFromPrivateStorage(imageName, ImageHelper.ImageType.PNG);

        // save a null image, nothing happens
        ImageHelper.getInstance().saveImageToPrivateSorageSync(imageName, null, ImageHelper.ImageType.PNG);
        assertFalse(ImageHelper.getInstance().isImageOnPrivateStorage(imageName, ImageHelper.ImageType.PNG));

        // save a valid image
        Bitmap newImage = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        ImageHelper.getInstance().saveImageToPrivateSorageSync(imageName, newImage, ImageHelper.ImageType.PNG);
        assertTrue(ImageHelper.getInstance().isImageOnPrivateStorage(imageName, ImageHelper.ImageType.PNG));

        // saved image is the same
        Bitmap loadedImage = ImageHelper.getInstance().loadImageFromPrivateSorageSync(imageName, ImageHelper.ImageType.PNG);
        assertEquals(newImage.getByteCount(), loadedImage.getByteCount());

        // delete that image
        ImageHelper.getInstance().deleteImageFromPrivateStorage(imageName, ImageHelper.ImageType.PNG);
        assertFalse(ImageHelper.getInstance().isImageOnPrivateStorage(imageName, ImageHelper.ImageType.PNG));
    }

    @Test
    public void bitmapToByte() throws Exception {
        Bitmap newImage = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

        // compressed image shouldn't be bigger
        assertTrue(newImage.getByteCount() >= ImageHelper.bitmapToByte(newImage).length);
    }
}