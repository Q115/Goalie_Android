package com.github.q115.goalie_android.utils;

import java.util.List;

public class ReloadListener {
    /**
     * DismissActivityListener
     */
    private static DismissActivityListener dismissActivityListener = null;

    public static void setDismissActivityListener(DismissActivityListener dismissActivityListener) {
        ReloadListener.dismissActivityListener = dismissActivityListener;
    }

    public static void callDismissActivityListener() {
        if (dismissActivityListener != null)
            dismissActivityListener.dismiss();
    }

    /**
     * Interface
     */
    public interface DismissActivityListener {
        void dismiss();
    }
}

