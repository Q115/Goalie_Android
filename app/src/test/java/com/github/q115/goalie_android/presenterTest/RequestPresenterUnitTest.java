package com.github.q115.goalie_android.presenterTest;

import com.github.q115.goalie_android.BaseTest;
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
public class RequestPresenterUnitTest extends BaseTest {
    private RequestsPresenter mPresenter;

    @Mock
    private RequestsView mView;

    @Before
    public void setup() {
        mView = mock(RequestsView.class);
        mPresenter = spy(new RequestsPresenter(mView));
    }

    @Test
    public void onRefresh() throws Exception {
        mPresenter.onRefresherRefresh();
        Thread.sleep(1000);
        verify(mView).syncComplete(false, "Unauthorized");

        UserHelper.getInstance().getOwnerProfile().username = getValidUsername();
        mPresenter.onRefresherRefresh();
        Thread.sleep(1000);
        verify(mView).syncComplete(true, "");
    }

    @Test
    public void reload() {
        mPresenter.reload();
        verify(mView).reload();
    }
}