package com.github.q115.goalie_android.ui.new_goal;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class DateTimePickerDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    public DateTimePickerDialogCallback mListener;

    public interface DateTimePickerDialogCallback {
        void onDateTimeSet(int year, int month, int day, int hour, int minute);
    }

    private int year, month, day, hour, minute;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        final Calendar c = Calendar.getInstance();

        // Options can be null, in which case, default options are used.
        if (arguments != null) {
            long epoch = arguments.getLong("endEpoch");

            if (epoch != 0) {
                Date time = new Date(epoch);
                c.setTime(time);
            }
        }

        // current time
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(requireContext(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Bundle arguments = getArguments();
        final Calendar c = Calendar.getInstance();

        this.year = year;
        this.month = month;
        this.day = dayOfMonth;

        if (arguments != null) {
            long epoch = arguments.getLong("endEpoch");

            if (epoch != 0) {
                Date time = new Date(epoch);
                c.setTime(time);
            }
        }

        // Use the current time as the default values for the picker.
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        Dialog d = new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        d.show();
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;

        dismiss();
        mListener.onDateTimeSet(year, month, day, hour, minute);
    }
}
