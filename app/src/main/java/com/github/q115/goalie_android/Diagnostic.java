package com.github.q115.goalie_android;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Diagnostic {
    public enum DiagnosticFlag {
        MainApplication, ImageHelper, UserHelper, Notification, Preferences, Other
    }

    public static void logError(DiagnosticFlag diagnosticFlag, String message) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.getDefault());
        Log.e(diagnosticFlag.toString(), String.format("(%s): %s", timeFormatter.format(System.currentTimeMillis()), message));
    }

    public static void logDebug(DiagnosticFlag diagnosticFlag, String message) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.getDefault());
        Log.d(diagnosticFlag.toString(), String.format("(%s): %s", timeFormatter.format(System.currentTimeMillis()), message));
    }
}
