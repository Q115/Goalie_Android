package com.github.q115.goalie_android.ui.my_goals.new_goal;

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
import com.github.q115.goalie_android.ui.BasePresenter;
import com.github.q115.goalie_android.utils.UserHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Qi on 8/11/2017.
 */

public class NewGoalPresenter implements BasePresenter {
    private final NewGoalView mNewGoalView;
    private boolean isResetNotHandled = false;
    private String mBeforeText;
    private String mAfterText;

    public TextWatcher mTextChangedListener = textChanged();
    public AdapterView.OnItemSelectedListener mSelectionChangedListener = selectionChanged();

    private int mWagerIncrement = 1;
    private static final int WAGER_INCREMENT = 5;


    public NewGoalPresenter(@NonNull NewGoalView newGoalView) {
        mNewGoalView = newGoalView;
        mNewGoalView.setPresenter(this);
    }

    public void start() {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
        mNewGoalView.updateTime(true, df.format(new Date(System.currentTimeMillis())));

        mNewGoalView.updateWager((long) (mWagerIncrement * WAGER_INCREMENT * 0.01 * UserHelper.getInstance().getOwnerProfile().reputation),
                UserHelper.getInstance().getOwnerProfile().reputation, mWagerIncrement * WAGER_INCREMENT);
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

                DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
                mNewGoalView.updateTime(viewID == R.id.goal_start_btn, df.format(new Date(epoch)));
            }
        };
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
                mBeforeText = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                resetReferee(false);
                // mAfterText = editable.toString();

                //  if (!mBeforeText.equals(mAfterText))
                //     resetReferee(false);
            }
        };
    }

    public void resetReferee(boolean isFromSpinner) {
        mNewGoalView.resetReferee(isFromSpinner);
    }

    public void setGoal() {
    }
}
