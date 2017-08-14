package com.github.q115.goalie_android.ui.my_goals.new_goal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.https.RESTNewGoal;
import com.github.q115.goalie_android.ui.BasePresenter;
import com.github.q115.goalie_android.utils.UserHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Qi on 8/11/2017.
 */

public class NewGoalPresenter implements BasePresenter {
    private final NewGoalView mNewGoalView;
    private boolean mShouldIgnore;
    private long mStart;
    private long mEnd;
    private int mCurrentSelection = 0;
    private int mWagerIncrement = 1;
    private static final int WAGER_INCREMENT = 5;

    private TextWatcher mTextChangedListener = textChanged();

    public TextWatcher getTextChangedListener() {
        return mTextChangedListener;
    }

    private AdapterView.OnItemSelectedListener mSelectionChangedListener = selectionChanged();

    public AdapterView.OnItemSelectedListener getSelectionChangedListener() {
        return mSelectionChangedListener;
    }

    public NewGoalPresenter(@NonNull NewGoalView newGoalView) {
        mNewGoalView = newGoalView;
        mStart = System.currentTimeMillis();
        mNewGoalView.setPresenter(this);
    }

    public void start() {
        mNewGoalView.updateTime(true, timeString(mStart));
        mNewGoalView.updateTime(false, mEnd == 0 ? "(Not Set)" : timeString(mEnd));
        mNewGoalView.updateWager((long) (mWagerIncrement * WAGER_INCREMENT * 0.01 * UserHelper.getInstance().getOwnerProfile().reputation),
                UserHelper.getInstance().getOwnerProfile().reputation, mWagerIncrement * WAGER_INCREMENT);
        mNewGoalView.updateReferee(mCurrentSelection != 0, mCurrentSelection);
    }

    public void restore(HashMap<String, String> values) {
        mStart = Long.parseLong(values.get("start"));
        mEnd = Long.parseLong(values.get("end"));
        mCurrentSelection = Integer.parseInt(values.get("currentSelection"));
        mWagerIncrement = Integer.parseInt(values.get("wagerIncrement"));
    }

    public HashMap<String, String> save() {
        HashMap<String, String> values = new HashMap<>();
        values.put("start", String.valueOf(mStart));
        values.put("end", String.valueOf(mEnd));
        values.put("currentSelection", String.valueOf(mCurrentSelection));
        values.put("wagerIncrement", String.valueOf(mWagerIncrement));

        return values;
    }

    // Validates & returns SublimePicker options
    public Pair<Boolean, SublimeOptions> getOptions() {
        SublimeOptions options = new SublimeOptions();
        int displayOptions = 0;

        displayOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;
        displayOptions |= SublimeOptions.ACTIVATE_TIME_PICKER;

        // disable
        displayOptions &= ~SublimeOptions.ACTIVATE_RECURRENCE_PICKER;

        options.setDisplayOptions(displayOptions);

        // disable the date range selection feature
        options.setCanPickDateRange(false);

        // If 'displayOptions' is zero, the chosen options are not valid
        return new Pair<>(displayOptions != 0 ? Boolean.TRUE : Boolean.FALSE, options);
    }

    public SublimePickerDialog.Callback onTimePicked() {
        return new SublimePickerDialog.Callback() {
            @Override
            public void onCancelled() {
            }

            @Override
            public void onDateTimeRecurrenceSet(SelectedDate selectedDate,
                                                int hourOfDay, int minute,
                                                SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
                                                String recurrenceRule, int viewID) {

                long epoch = selectedDate.getEndDate().getTimeInMillis();
                epoch -= selectedDate.getEndDate().get(Calendar.HOUR) * 60 * 60 * 1000;
                epoch -= selectedDate.getEndDate().get(Calendar.MINUTE) * 60 * 1000;
                epoch -= selectedDate.getEndDate().get(Calendar.SECOND) * 1000;
                epoch -= selectedDate.getEndDate().get(Calendar.MILLISECOND);
                epoch += (hourOfDay * 60 + minute) * 60 * 1000;

                if (viewID == R.id.goal_start_btn)
                    mStart = epoch;
                else
                    mEnd = epoch;

                mNewGoalView.updateTime(viewID == R.id.goal_start_btn, timeString(epoch));
            }
        };
    }

    private String timeString(long epoch) {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
        return df.format(new Date(epoch));
    }

    public View.OnClickListener onWagerClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long wagering;
                int percent;
                if (view.getId() == R.id.goal_wager_minus) {
                    if (mWagerIncrement > 1) {
                        mWagerIncrement--;
                        wagering = (long) (mWagerIncrement * WAGER_INCREMENT * 0.01 * UserHelper.getInstance().getOwnerProfile().reputation);
                        percent = mWagerIncrement * WAGER_INCREMENT;
                    } else
                        return;
                } else {
                    if (mWagerIncrement < 100 / WAGER_INCREMENT) {
                        mWagerIncrement++;
                        wagering = (long) (mWagerIncrement * WAGER_INCREMENT * 0.01 * UserHelper.getInstance().getOwnerProfile().reputation);
                        percent = mWagerIncrement * WAGER_INCREMENT;
                    } else
                        return;
                }
                mNewGoalView.updateWager(wagering, UserHelper.getInstance().getOwnerProfile().reputation, percent);
            }
        };
    }

    public String[] refereeArray() {
        String[] friends = UserHelper.getInstance().getAllContacts().keySet()
                .toArray(new String[UserHelper.getInstance().getAllContacts().size()]);
        String[] friendsWithNull = new String[friends.length + 1];
        friendsWithNull[0] = "";
        System.arraycopy(friends, 0, friendsWithNull, 1, friends.length);
        return friendsWithNull;
    }

    private AdapterView.OnItemSelectedListener selectionChanged() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mCurrentSelection == i)
                    return;

                mCurrentSelection = i;

                if (mShouldIgnore) {
                    return;
                }

                resetReferee(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

    private TextWatcher textChanged() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mShouldIgnore = mCurrentSelection != 0;
                resetReferee(false);
            }
        };
    }

    public void resetReferee(boolean isFromSpinner) {
        mNewGoalView.resetReferee(isFromSpinner);
    }

    public void setGoal(Context context, String title, String encouragement, String referee) {
        if (title.isEmpty()) {
            mNewGoalView.onSetGoal(false, context.getString(R.string.error_goal_no_title));
            return;
        }
        if (referee.isEmpty()) {
            mNewGoalView.onSetGoal(false, context.getString(R.string.error_goal_no_referee));
            return;
        }
        if (mEnd <= mStart) {
            mNewGoalView.onSetGoal(false, context.getString(R.string.error_goal_invalid_date));
            return;
        }

        long wagering = (long) (mWagerIncrement * WAGER_INCREMENT * 0.01 * UserHelper.getInstance().getOwnerProfile().reputation);

        mNewGoalView.updateProgress(true);
        RESTNewGoal rest = new RESTNewGoal(UserHelper.getInstance().getOwnerProfile().username, title, mStart, mEnd, wagering, encouragement, referee);
        rest.setListener(new RESTNewGoal.Listener() {
            @Override
            public void onSuccess() {
                mNewGoalView.updateProgress(false);
                mNewGoalView.onSetGoal(true, "");
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
