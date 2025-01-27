package com.github.q115.goalie_android.presenterTest;

import android.content.Context;
import android.view.View;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.ui.new_goal.DateTimePickerDialog;
import com.github.q115.goalie_android.ui.new_goal.NewGoalFragmentPresenter;
import com.github.q115.goalie_android.ui.new_goal.NewGoalFragmentView;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static test_util.RESTUtil.getValidFriendUsername;

import java.util.Calendar;
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
@Config(manifest = Config.NONE)
public class NewGoalPresenterUnitTest extends BaseTest {
    private NewGoalFragmentPresenter mPresenter;

    @Mock
    private NewGoalFragmentView mView;

    @Mock
    private Context mContext = RuntimeEnvironment.getApplication().getApplicationContext();

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mView = mock(NewGoalFragmentView.class);
        mPresenter = spy(new NewGoalFragmentPresenter(mView));
    }

    @Test
    public void onStart() {
        mPresenter.start();
        verify(mView).updateTime("(Not Set)");
        verify(mView).updateWager(5, 100, 5);
        verify(mView, never()).updateRefereeOnSpinner(0);
    }

    @Test
    public void saveRestore() {
        mPresenter.restore(mPresenter.getSaveHash());
    }

    @Test
    public void onWagerClicked() {
        View.OnClickListener onWagerClicked = mPresenter.getWagerClickedListener();

        View view = mock(View.class);
        when(view.getId()).thenReturn(R.id.goal_wager_plus);
        onWagerClicked.onClick(view);
        verify(mView).updateWager(10, 100, 10);
        onWagerClicked.onClick(view);
        verify(mView).updateWager(15, 100, 15);
        onWagerClicked.onClick(view);
        verify(mView).updateWager(20, 100, 20);

        when(view.getId()).thenReturn(R.id.goal_wager_minus);
        onWagerClicked.onClick(view);
        verify(mView, times(2)).updateWager(15, 100, 15);
    }

    @Test
    public void onTimePicked() {
        Calendar endDate = Calendar.getInstance(Locale.getDefault());
        endDate.add(Calendar.YEAR, 9);

        DateTimePickerDialog.DateTimePickerDialogCallback pickerDialogCallback = mPresenter.getTimePickerCallbackListener();
        pickerDialogCallback.onDateTimeSet(endDate.get(Calendar.YEAR), 1, 1, 1, 1);
    }

    @Test
    public void refereeArray() throws Exception {
        when(mContext.getString(R.string.new_username))
                .thenReturn("(New Username)");

        String[] strings = mPresenter.getRefereeArray(mContext);
        assertEquals(strings.length, 2);
        assertEquals(strings[0], "");
        assertEquals(strings[1], mContext.getString(R.string.new_username));

        UserHelper.getInstance().addUser(new User("user"));
        UserHelper.getInstance().addUser(new User("self"));
        UserHelper.getInstance().getOwnerProfile().username = "self";

        strings = mPresenter.getRefereeArray(mContext);
        assertEquals(strings.length, 4);
        assertEquals(strings[0], "");
        assertEquals(strings[1], mContext.getString(R.string.new_username));
        assertEquals(strings[2], "self");
        assertEquals(strings[3], "user");
    }

    @Test
    public void setGoalInvalid() throws Exception {
        String title = "title";
        String encouragement = "encouragement";
        String referee = getValidFriendUsername();
        boolean isGoalPublic = true;

        when(mContext.getString(R.string.error_goal_no_title))
                .thenReturn("error_goal_no_title");
        when(mContext.getString(R.string.error_goal_invalid_date))
                .thenReturn("error_goal_invalid_date");
        when(mContext.getString(R.string.error_goal_no_referee))
                .thenReturn("error_goal_no_referee");
        when(mContext.getString(R.string.username_error))
                .thenReturn("username_error");

        mPresenter.setGoal(mContext, "", encouragement, referee, 0, isGoalPublic);
        verify(mView).onSetGoal(false, "error_goal_no_title");

        mPresenter.setGoal(mContext, title, encouragement, referee, 0, isGoalPublic);
        verify(mView).onSetGoal(false, "error_goal_invalid_date");

        pickAValidDate();
        mPresenter.setGoal(mContext, title, encouragement, "", 0, isGoalPublic);
        verify(mView).onSetGoal(false, "error_goal_no_referee");

        mPresenter.setGoal(mContext, title, encouragement, "ref", 0, isGoalPublic);
        verify(mView).onSetGoal(false, "username_error");
    }

    @Test
    public void setGoal() throws Exception {
        String title = "title";
        String encouragement = "encouragement";
        String referee = getValidFriendUsername();

        pickAValidDate();
        UserHelper.getInstance().getOwnerProfile().username = getValidFriendUsername();
        mPresenter.setGoal(mContext, title, encouragement, referee, 0, true);
        verify(mView).updateProgress(true);
        verify(mView, timeout(Constants.ASYNC_CONNECTION_EXTENDED_TIMEOUT).times(1)).onSetGoal(true, "");
    }

    private void pickAValidDate() {
        Calendar endDate = Calendar.getInstance(Locale.getDefault());
        endDate.add(Calendar.YEAR, 9);

        DateTimePickerDialog.DateTimePickerDialogCallback pickerDialogCallback = mPresenter.getTimePickerCallbackListener();
        pickerDialogCallback.onDateTimeSet(endDate.get(Calendar.YEAR), 1, 1, 1, 1);
    }
}
