package com.github.q115.goalie_android.ui.main.feeds;

import androidx.annotation.NonNull;

import com.github.q115.goalie_android.ui.BasePresenter;

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

public class FeedsPresenter implements BasePresenter {
    private final FeedsView mFeedsView;

    public FeedsPresenter(@NonNull FeedsView feedsView) {
        mFeedsView = feedsView;
        mFeedsView.setPresenter(this);
    }

    public void start() {
    }

    public void reload() {
        mFeedsView.reload();
    }
}
