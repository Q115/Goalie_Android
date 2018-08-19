package com.github.q115.goalie_android.ui.new_goal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.https.RESTNewGoal;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.ui.BasePresenter;
import com.github.q115.goalie_android.utils.UserHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;

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

public class NewGoalFragmentPresenter implements BasePresenter {
    private static final int WAGER_PERCENTAGE_INCREMENT = 5;

    private final NewGoalFragmentView mNewGoalView;
    private long mStart;
    private long mEnd;
    private long mAlarmTimeBeforeEnd;
    private int mCurrentRefereeSelection;
    private int mWagerIncrement;

    public NewGoalFragmentPresenter(@NonNull NewGoalFragmentView newGoalView) {
        mNewGoalView = newGoalView;
        mNewGoalView.setPresenter(this);

        mStart = System.currentTimeMillis();
        mCurrentRefereeSelection = 0;
        mWagerIncrement = 1;
    }

    public void start() {
        mNewGoalView.updateTime(mEnd == 0 ? "(Not Set)" : getFormatedTimeString(mEnd));
        mNewGoalView.updateWager(getWagering(),
                UserHelper.getInstance().getOwnerProfile().reputation, mWagerIncrement * WAGER_PERCENTAGE_INCREMENT);

        if (mCurrentRefereeSelection != 0)
            mNewGoalView.updateRefereeOnSpinner(mCurrentRefereeSelection);
    }

    public void restore(HashMap<String, String> values) {
        mStart = Long.parseLong(values.get("start"));
        mEnd = Long.parseLong(values.get("end"));
        mCurrentRefereeSelection = Integer.parseInt(values.get("currentSelection"));
        mWagerIncrement = Integer.parseInt(values.get("wagerIncrement"));
    }

    public HashMap<String, String> getSaveHash() {
        HashMap<String, String> values = new HashMap<>();
        values.put("start", String.valueOf(mStart));
        values.put("end", String.valueOf(mEnd));
        values.put("currentSelection", String.valueOf(mCurrentRefereeSelection));
        values.put("wagerIncrement", String.valueOf(mWagerIncrement));

        return values;
    }

    public String[] getRefereeArray(Context context) {
        TreeMap<String, User> tempHashMap = new TreeMap<>(UserHelper.getInstance().getAllContacts());
        // include self currently to allow self goals. Uncomment below to remove self as a referee
        //tempHashMap.remove(UserHelper.getInstance().getOwnerProfile().username);
        tempHashMap.put("", null);
        tempHashMap.put(context.getString(R.string.new_username), null);

        return tempHashMap.keySet().toArray(new String[UserHelper.getInstance().getAllContacts().size()]);
    }

    public String[] getAlarmReminderArray() {
        return new String[]{"15 Minutes Before", "1 Hour Before", "1 Day Before", "None"};
    }

    public long getAlarmMillisecondBeforeEndDate(int option) {
        switch (option) {
            case 0:
                mAlarmTimeBeforeEnd = 15 * 60000;
                break;
            case 1:
                mAlarmTimeBeforeEnd = 60 * 60000;
                break;
            case 2:
                mAlarmTimeBeforeEnd = 24 * 60 * 60000;
                break;
            case 3:
                mAlarmTimeBeforeEnd = 0;
                break;
            default:
                mAlarmTimeBeforeEnd = 0;
                break;
        }
        return mAlarmTimeBeforeEnd;
    }

    private String getFormatedTimeString(long epoch) {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
        return df.format(new Date(epoch));
    }

    private long getWagering() {
        return (long) (mWagerIncrement * WAGER_PERCENTAGE_INCREMENT
                * 0.01 * UserHelper.getInstance().getOwnerProfile().reputation);
    }

    public View.OnClickListener getTimePickerClickedListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewGoalView.showTimePicker(view.getId());
            }
        };
    }

    public SublimeOptions getSublimePickerOptions(int viewID) {
        SublimeOptions options = new SublimeOptions();
        int displayOptions = 0;

        displayOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;
        displayOptions |= SublimeOptions.ACTIVATE_TIME_PICKER;

        // disable
        displayOptions &= ~SublimeOptions.ACTIVATE_RECURRENCE_PICKER;

        options.setDisplayOptions(displayOptions);
        options.setCanPickDateRange(false);
        setSublimeDateOptions(options, viewID);

        return options;
    }

    // Set correct date and time based on previous selection.
    private void setSublimeDateOptions(SublimeOptions options, int viewID) {
        long epoch = 0;
        if (viewID == R.id.goal_end_btn) {
            epoch = mEnd;
        }

        if (epoch != 0) {   // User has made a selection before.
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(epoch);
            options.setDateParams(c);
            options.setTimeParams(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false);
        }
    }

    public SublimePickerDialog.Callback getTimePickerCallbackListener() {
        return new SublimePickerDialog.Callback() {
            @Override
            public void onDateTimeRecurrenceSet(SelectedDate selectedDate, int hourOfDay, int minute, int viewID) {
                int year = selectedDate.getEndDate().get(Calendar.YEAR);
                int month = selectedDate.getEndDate().get(Calendar.MONTH);
                int day = selectedDate.getEndDate().get(Calendar.DATE);
                GregorianCalendar date = new GregorianCalendar(year, month, day, hourOfDay, minute, 0);
                long epoch = date.getTimeInMillis();

                if (viewID == R.id.goal_end_btn)
                    mEnd = epoch;

                mNewGoalView.updateTime(getFormatedTimeString(epoch));
            }
        };
    }

    public View.OnClickListener getWagerClickedListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long wagering;
                int percent;
                if (view.getId() == R.id.goal_wager_minus && mWagerIncrement > 1) {
                    mWagerIncrement--;
                } else if (view.getId() == R.id.goal_wager_plus && mWagerIncrement < 100 / WAGER_PERCENTAGE_INCREMENT) {
                    mWagerIncrement++;
                } else
                    return;

                wagering = getWagering();
                percent = mWagerIncrement * WAGER_PERCENTAGE_INCREMENT;
                mNewGoalView.updateWager(wagering, UserHelper.getInstance().getOwnerProfile().reputation, percent);
            }
        };
    }

    public AdapterView.OnItemSelectedListener getRefereeSelectionChangedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // do nothing if same selection
                if (mCurrentRefereeSelection == i)
                    return;

                mCurrentRefereeSelection = i;

                // new username
                if (mCurrentRefereeSelection == 1) {
                    mNewGoalView.resetReferee(true);
                    mNewGoalView.showNewUsernameDialog();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //intentionally left blank
            }
        };
    }

    public void setGoal(Context context, String title, String encouragement, String referee, long alarmMillisecondBeforeEnd, boolean isGoalPublic) {
        if (checkGoalIsValid(context, title, referee, alarmMillisecondBeforeEnd)) {
            mNewGoalView.updateProgress(true);
            RESTNewGoal rest = new RESTNewGoal(UserHelper.getInstance().getOwnerProfile().username,
                    title, mStart, mEnd, getWagering(), encouragement, referee, isGoalPublic);
            rest.setListener(new RESTNewGoal.Listener() {
                @Override
                public void onSuccess(String guid) {
                    mNewGoalView.updateProgress(false);
                    mNewGoalView.onSetGoal(true, "");
                    setAlarmTime(guid);
                }

                @Override
                public void onFailure(String errMsg) {
                    mNewGoalView.updateProgress(false);
                    mNewGoalView.onSetGoal(false, errMsg);
                }
            });
            rest.execute();
        }
    }

    private boolean checkGoalIsValid(Context context, String title, String referee, long alarmMillisecondBeforeEnd) {
        if (title.isEmpty()) {
            mNewGoalView.onSetGoal(false, context.getString(R.string.error_goal_no_title));
            return false;
        }
        if (mEnd <= mStart) {
            mNewGoalView.onSetGoal(false, context.getString(R.string.error_goal_invalid_date));
            return false;
        }

        if (mEnd - alarmMillisecondBeforeEnd <= mStart) {
            mNewGoalView.onSetGoal(false, context.getString(R.string.error_alarm_invalid_date));
            return false;
        }

        if (referee.isEmpty()) {
            mNewGoalView.onSetGoal(false, context.getString(R.string.error_goal_no_referee));
            return false;
        }
        if (!UserHelper.isUsernameValid(referee)) {
            mNewGoalView.onSetGoal(false, context.getString(R.string.username_error));
            return false;
        }

        long wagering = getWagering();
        if (wagering <= 0) {
            mNewGoalView.onSetGoal(false, context.getString(R.string.wager_invalid));
            return false;
        }

        return true;
    }

    private void setAlarmTime(String guid) {
        if (mAlarmTimeBeforeEnd > 0)
            mNewGoalView.setAlarmTime(mEnd - mAlarmTimeBeforeEnd, guid);
    }
}
