package com.github.q115.goalie_android;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;

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
public class Diagnostic {
    public enum DiagnosticFlag {
        MainApplication, ImageHelper, UserHelper, Notification, Preferences, Other
    }

    public static void logError(DiagnosticFlag diagnosticFlag, String message) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.getDefault());
        Log.e(diagnosticFlag.toString(), String.format("(%s): %s",
                timeFormatter.format(System.currentTimeMillis()), message));
    }

    public static void logDebug(DiagnosticFlag diagnosticFlag, String message) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.getDefault());
        Log.d(diagnosticFlag.toString(), String.format("(%s): %s",
                timeFormatter.format(System.currentTimeMillis()), message));
    }
}
