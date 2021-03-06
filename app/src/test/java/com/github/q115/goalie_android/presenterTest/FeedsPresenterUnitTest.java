package com.github.q115.goalie_android.presenterTest;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.ui.main.feeds.FeedsPresenter;
import com.github.q115.goalie_android.ui.main.feeds.FeedsView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

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
@RunWith(RobolectricTestRunner.class)
public class FeedsPresenterUnitTest extends BaseTest {
    private FeedsPresenter mPresenter;

    @Mock
    private FeedsView mView;

    @Before
    public void setup() {
        mView = mock(FeedsView.class);
        mPresenter = spy(new FeedsPresenter(mView));
    }

    @Test
    public void reload() {
        mPresenter.reload();
        verify(mView).reload();
    }
}
