package com.github.q115.goalie_android.presenterTest;

import android.content.res.Resources;
import android.test.mock.MockContext;
import android.util.DisplayMetrics;
import android.view.View;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.ui.my_goals.new_goal.NewGoalPresenter;
import com.github.q115.goalie_android.ui.my_goals.new_goal.NewGoalView;
import com.github.q115.goalie_android.ui.my_goals.new_goal.SublimePickerDialog;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static test_util.RESTUtil.getValidFriendUsername;

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

public class NewGoalPresenterUnitTest extends BaseTest {
    private NewGoalPresenter mPresenter;

    @Mock
    private NewGoalView mView;

    @Mock
    private MockContext mContext;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mView = mock(NewGoalView.class);
        mPresenter = spy(new NewGoalPresenter(mView));
    }

    @Test
    public void onStart() {
        mPresenter.start();
        verify(mView).updateTime(false, "(Not Set)");
        verify(mView).updateWager(5, 100, 5);
        verify(mView).updateReferee(false, 0);

        /*
        // expands menu
        mPresenter.toggleFAB();
        verify(mView, never()).closeFABMenu();
        verify(mView).showFABMenu();
        assertTrue(mPresenter.isFABOpen());

        // collapse menu
        mPresenter.toggleFAB();
        verify(mView).closeFABMenu();
        assertFalse(mPresenter.isFABOpen());
        */
    }

    @Test
    public void saveRestore() {
        mPresenter.restore(mPresenter.save());
    }

    @Test
    public void getOptions() {
        assertTrue(mPresenter.getOptions().first);
    }

    @Test
    public void onWagerClicked() {
        View.OnClickListener onWagerClicked = mPresenter.onWagerClicked();
        Resources res = mock(Resources.class);
        when(res.getDisplayMetrics())
                .thenReturn(new DisplayMetrics());
        when(mContext.getResources())
                .thenReturn(res);

        View view = new View(mContext);
        view.setId(R.id.goal_wager_plus);
        onWagerClicked.onClick(view);
        verify(mView).updateWager(10, 100, 10);
        onWagerClicked.onClick(view);
        verify(mView).updateWager(15, 100, 15);
        onWagerClicked.onClick(view);
        verify(mView).updateWager(20, 100, 20);

        view.setId(R.id.goal_wager_minus);
        onWagerClicked.onClick(view);
        verify(mView, times(2)).updateWager(15, 100, 15);
    }

    @Test
    public void onTimePicked() {
        SublimePickerDialog.Callback sublimePickerDialogCallback = mPresenter.onTimePicked();
        sublimePickerDialogCallback.onDateTimeRecurrenceSet(new SelectedDate(new GregorianCalendar()), 1, 1, null, null, 100);
    }

    @Test
    public void refereeArray() throws Exception {
        String[] strings = mPresenter.refereeArray();
        assertEquals(strings.length, 1);
        assertEquals(strings[0], "");

        UserHelper.getInstance().addUser(new User("user"));
        UserHelper.getInstance().addUser(new User("self"));
        UserHelper.getInstance().getOwnerProfile().username = "self";

        strings = mPresenter.refereeArray();
        assertEquals(strings.length, 3);
        assertEquals(strings[0], "");
        assertEquals(strings[1], "self");
        assertEquals(strings[2], "user");
    }

    @Test
    public void resetReferee() throws Exception {
        boolean isFromSpinner = true;
        mPresenter.resetReferee(isFromSpinner);
        verify(mView).resetReferee(isFromSpinner);

        isFromSpinner = false;
        mPresenter.resetReferee(isFromSpinner);
        verify(mView).resetReferee(isFromSpinner);
    }

    @Test
    public void setGoal() throws Exception {
        String title = "title";
        String encouragement = "encouragement";
        String referee = getValidFriendUsername();

        when(mContext.getString(R.string.error_goal_no_title))
                .thenReturn("error_goal_no_title");
        when(mContext.getString(R.string.error_goal_invalid_date))
                .thenReturn("error_goal_invalid_date");
        when(mContext.getString(R.string.error_goal_no_referee))
                .thenReturn("error_goal_no_referee");
        when(mContext.getString(R.string.username_error))
                .thenReturn("username_error");

        mPresenter.setGoal(mContext, "", encouragement, referee);
        verify(mView).onSetGoal(false, "error_goal_no_title");

        mPresenter.setGoal(mContext, title, encouragement, referee);
        verify(mView).onSetGoal(false, "error_goal_invalid_date");

        SublimePickerDialog.Callback sublimePickerDialogCallback = mPresenter.onTimePicked();
        Calendar endDate = Calendar.getInstance(Locale.getDefault());
        endDate.add(Calendar.YEAR, 9);
        sublimePickerDialogCallback.onDateTimeRecurrenceSet(
                new SelectedDate(endDate), 1, 1, null, null, 100);
        mPresenter.setGoal(mContext, title, encouragement, "");
        verify(mView).onSetGoal(false, "error_goal_no_referee");

        mPresenter.setGoal(mContext, title, encouragement, "ref");
        verify(mView).onSetGoal(false, "username_error");

        UserHelper.getInstance().getOwnerProfile().username = getValidFriendUsername();
        mPresenter.setGoal(mContext, title, encouragement, referee);
        verify(mView).updateProgress(true);
        Thread.sleep(1000);
        verify(mView).onSetGoal(true, "");
    }
}
