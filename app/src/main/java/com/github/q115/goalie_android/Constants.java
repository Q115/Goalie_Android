package com.github.q115.goalie_android;

public class Constants {
    public static final String URL = BuildConfig.DEBUG ? "http://192.168.0.14:8080" : "https://google.com";

    public static final String PreferenceFileName = "PreferenceFileName";

    public static final String FAILED_TO_CONNECT = "Connection failure, please ensure internet connection is active.";
    public static final String FAILED_TO_Send = "Failed to reach server, please try again.";
    public static final String FAILED = "Failed";

    public static final int MaxUsernameLength = 30;
    public static final int MaxAboutmeLength = 80;

    public static final int PROFILE_IMAGE_SELECTED_ACTION = 11;
    public static final int PROFILE_IMAGE_TAKEN_ACTION = 12;

    public static final float RoundedProfile = 10f; // the bigger the value the more square the image
    public static final float CircleProfile = 2f; // 2 = circle, the bigger the value the more square the image

    public static final int ASYNC_CONNECTION_NORMAL_TIMEOUT = 9 * 1000; // 9 seconds
    public static final int ASYNC_CONNECTION_EXTENDED_TIMEOUT = 16 * 1000; // 16 seconds

    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 22;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 23;
    public static final int MAIL_ACTION = 101;

    //profile image sizes
    public static final int MaxProfileImageWidth = 600; //in px
    public static final int MaxProfileImageHeight = 600;
    public static final int ImageJPGQuality = 99;

    // notification
    public static final int ID_NOTIFICATION_BROADCAST = 607;
}
