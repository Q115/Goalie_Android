package com.github.q115.goalie_android.presenterTest;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.ui.MainActivityPresenter;
import com.github.q115.goalie_android.ui.MainActivityView;
import com.github.q115.goalie_android.ui.requests.RequestsPresenter;
import com.github.q115.goalie_android.ui.requests.RequestsView;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static test_util.TestUtil.getValidUsername;

/**
 * Created by Qi on 8/21/2017.
 */
@RunWith(RobolectricTestRunner.class)
public class MainActivityPresenterUnitTest extends BaseTest {
    private MainActivityPresenter mPresenter;

    @Mock
    private MainActivityView mView;

    @Before
    public void setup() {
        mView = mock(MainActivityView.class);
        mPresenter = spy(new MainActivityPresenter(mView));
    }

    @Test
    public void onStart() throws Exception {
        PreferenceHelper.getInstance().setAccountUsername("");
        mPresenter.start();
        verify(mView).showLogin();

        PreferenceHelper.getInstance().setAccountUsername(getValidUsername());
        UserHelper.getInstance().getOwnerProfile().username = getValidUsername();
        mPresenter.start();
        Thread.sleep(2000);
        verify(mView).reloadAll();
    }
}