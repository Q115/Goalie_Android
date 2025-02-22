package com.github.q115.goalie_android.ui.new_goal;

import com.github.q115.goalie_android.ui.BaseView;

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

public interface NewGoalFragmentView extends BaseView<NewGoalFragmentPresenter> {
    void updateTime(String date);

    void updateWager(long wagering, long total, int percent);

    void updateRefereeOnSpinner(int position);

    void resetReferee(boolean isFromSpinner);

    void showNewUsernameDialog();

    void onSetGoal(boolean isSuccessful, String errMsg);

    void updateProgress(boolean shouldShow);

    void showTimePicker(long endEpoch);

    void setAlarmTime(long epoch, String guid);

    boolean isAlarmPermissionGranted();
}
