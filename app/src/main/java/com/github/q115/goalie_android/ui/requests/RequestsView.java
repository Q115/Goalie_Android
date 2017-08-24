package com.github.q115.goalie_android.ui.requests;

import android.graphics.Bitmap;

import com.github.q115.goalie_android.models.Goal;
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
public interface RequestsView extends BaseView<RequestsPresenter> {
    void syncComplete(boolean isSuccessful, String errMsg);

    void reload();

    void showDialog(String title, String end, String start, String reputation, String encouragment,
                    String referee, Bitmap profileImage, Goal.GoalCompleteResult goalCompleteResult, String guid);
}
