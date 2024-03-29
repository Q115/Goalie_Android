package com.github.q115.goalie_android.ui.new_goal;

/*
 * Copyright 2015 Vikram Kakkar
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

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appeaser.sublimepickerlibrary.SublimePicker;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeListenerAdapter;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.github.q115.goalie_android.R;

public class SublimePickerDialog extends DialogFragment {
    public interface Callback {
        void onDateTimeRecurrenceSet(SelectedDate selectedDate, int hourOfDay, int minute, int viewID);
    }

    private Callback mCallback;

    // identify which picker (start or end)
    private int viewID;

    private final SublimeListenerAdapter mListener;

    public SublimePickerDialog() {
        mListener = new SublimeListenerAdapter() {
            @Override
            public void onCancelled() {
                dismiss();
            }

            @Override
            public void onDateTimeRecurrenceSet(SublimePicker sublimeMaterialPicker,
                                                SelectedDate selectedDate,
                                                int hourOfDay, int minute,
                                                SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
                                                String recurrenceRule) {
                if (mCallback != null) {
                    mCallback.onDateTimeRecurrenceSet(selectedDate, hourOfDay, minute, viewID);
                }
                dismiss();
            }
        };
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SublimePicker mSublimePicker = (SublimePicker) getActivity()
                .getLayoutInflater().inflate(R.layout.sublime_picker, container);

        Bundle arguments = getArguments();
        SublimeOptions options = null;

        // Options can be null, in which case, default options are used.
        if (arguments != null) {
            options = arguments.getParcelable("SUBLIME_OPTIONS");
            viewID = arguments.getInt("viewID");
        }

        mSublimePicker.initializePicker(options, mListener);
        return mSublimePicker;
    }
}
