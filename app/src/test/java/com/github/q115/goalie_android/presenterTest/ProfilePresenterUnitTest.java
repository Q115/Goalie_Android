package com.github.q115.goalie_android.presenterTest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.ui.profile.ProfilePresenter;
import com.github.q115.goalie_android.ui.profile.ProfileView;
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
public class ProfilePresenterUnitTest extends BaseTest {
    private ProfilePresenter mPresenter;

    @Mock
    private ProfileView mView;

    @Before
    public void setup() {
        mView = mock(ProfileView.class);
        mPresenter = spy(new ProfilePresenter(getValidUsername(), mView));
    }

    @Test
    public void onStart() throws Exception {
        UserHelper.getInstance().getOwnerProfile().username = "fake";
        mPresenter.start();
        verify(mView).setupForOwner(false);

        User user = new User(getValidUsername(), "bio", 999, 0);
        UserHelper.getInstance().setOwnerProfile(user);
        UserHelper.getInstance().getOwnerProfile().username = getValidUsername();
        mPresenter.start();
        verify(mView).setupForOwner(true);
        verify(mView).setupView(user.username, user.bio, user.reputation);
    }

    @Test
    public void newProfileImageSelected() throws Exception {
        Bitmap image = null;
        mPresenter.newProfileImageSelected(image);
        verify(mView, never()).updateProgress(true);

        image = BitmapFactory.decodeFile("../test.png");
        mPresenter.newProfileImageSelected(image);
        verify(mView).updateProgress(true);
        Thread.sleep(2500);
        verify(mView).updateProgress(false);
        verify(mView).uploadSuccess(image);
    }
}
