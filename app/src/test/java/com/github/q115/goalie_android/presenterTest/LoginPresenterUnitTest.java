package com.github.q115.goalie_android.presenterTest;

import android.test.mock.MockContext;

import com.github.q115.goalie_android.MainApplication;
import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.https.VolleyRequestQueue;
import com.github.q115.goalie_android.ui.login.LoginPresenter;
import com.github.q115.goalie_android.ui.login.LoginView;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Qi on 8/6/2017.
 */

@RunWith(RobolectricTestRunner.class)
public class LoginPresenterUnitTest {
    private LoginPresenter mPresenter;

    @Mock
    private LoginView mView;

    @Mock
    private MockContext mContext;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        VolleyRequestQueue.getInstance().initialize(RuntimeEnvironment.application);

        mView = mock(LoginView.class);
        mPresenter = new LoginPresenter(mView);
        verify(mView).setPresenter(mPresenter);
    }

    @Test
    public void register() {
        when(mContext.getString(R.string.username_error))
                .thenReturn("username_error");
        when(mContext.getString(R.string.welcome))
                .thenReturn("welcome");

        mPresenter.register(mContext, "username");
        verify(mView).updateProgress(true);
    }
}