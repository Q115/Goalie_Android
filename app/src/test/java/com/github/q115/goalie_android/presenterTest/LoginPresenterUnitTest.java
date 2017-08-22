package com.github.q115.goalie_android.presenterTest;

import android.test.mock.MockContext;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.https.VolleyRequestQueue;
import com.github.q115.goalie_android.ui.login.LoginPresenter;
import com.github.q115.goalie_android.ui.login.LoginView;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Qi on 8/6/2017.
 */

@RunWith(RobolectricTestRunner.class)
public class LoginPresenterUnitTest extends BaseTest {
    private LoginPresenter mPresenter;

    @Mock
    private LoginView mView;

    @Mock
    private MockContext mContext;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mView = mock(LoginView.class);
        mPresenter = new LoginPresenter(mView);
        verify(mView).setPresenter(mPresenter);
    }

    @Test
    public void register() throws Exception {
        when(mContext.getString(R.string.username_error))
                .thenReturn("username_error");
        when(mContext.getString(R.string.welcome))
                .thenReturn("welcome");

        String username = UUID.randomUUID().toString().substring(20);
        mPresenter.register(mContext, username);
        verify(mView).updateProgress(true);
        Thread.sleep(2000);
        verify(mView).updateProgress(false);
        verify(mView).registerSuccess("welcome");

        // username is taken
        mPresenter.register(mContext, username);
        Thread.sleep(2000);
        verify(mView).showRegisterError("Already Registered");

        // bad username
        mPresenter.register(mContext, " x ");
        Thread.sleep(2000);
        verify(mView).showRegisterError("username_error");
    }
}