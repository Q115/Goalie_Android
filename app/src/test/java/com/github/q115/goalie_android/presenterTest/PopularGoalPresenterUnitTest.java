package com.github.q115.goalie_android.presenterTest;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.ui.my_goals.popular_goal.PopularGoalPresenter;
import com.github.q115.goalie_android.ui.my_goals.popular_goal.PopularGoalView;
import com.github.q115.goalie_android.ui.requests.RequestsPresenter;
import com.github.q115.goalie_android.ui.requests.RequestsView;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static test_util.TestUtil.getValidUsername;

/**
 * Created by Qi on 8/21/2017.
 */

@RunWith(RobolectricTestRunner.class)
public class PopularGoalPresenterUnitTest extends BaseTest {
    private PopularGoalPresenter mPresenter;

    @Mock
    private PopularGoalView mView;

    @Before
    public void setup() {
        mView = mock(PopularGoalView.class);
        mPresenter = spy(new PopularGoalPresenter(mView));
    }

    @Test
    public void start() throws Exception {
        mPresenter.start();
    }
}
