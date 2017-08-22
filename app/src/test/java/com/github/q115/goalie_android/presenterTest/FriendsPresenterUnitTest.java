package com.github.q115.goalie_android.presenterTest;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.test.mock.MockContext;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.ui.friends.FriendsListPresenter;
import com.github.q115.goalie_android.ui.friends.FriendsListView;
import com.github.q115.goalie_android.ui.friends.FriendsPresenter;
import com.github.q115.goalie_android.ui.friends.FriendsView;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static test_util.TestUtil.getValidUsername;

/**
 * Created by Qi on 8/21/2017.
 */
@RunWith(RobolectricTestRunner.class)
public class FriendsPresenterUnitTest extends BaseTest {
    private FriendsPresenter mPresenter;

    @Mock
    private FriendsView mView;

    @Mock
    private MockContext mContext;

    private FriendsListPresenter mListPresenter;

    @Mock
    private FriendsListView mListView;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mView = mock(FriendsView.class);
        mPresenter = spy(new FriendsPresenter(mContext, mView));

        mListView = mock(FriendsListView.class);
        mListPresenter = spy(new FriendsListPresenter(mListView));
    }

    @Test
    public void sendSMSInvite() {
        when(mContext.getContentResolver())
                .thenReturn(new ContentResolver(mContext) {
                    @Nullable
                    @Override
                    public String[] getStreamTypes(@NonNull Uri url, @NonNull String mimeTypeFilter) {
                        return super.getStreamTypes(url, mimeTypeFilter);
                    }
                });

        mPresenter.sendSMSInvite(Uri.EMPTY);
        verify(mView, never()).sendSMSInvite("");

        mPresenter.sendSMSInvite(null);
        verify(mView, never()).sendSMSInvite("");
    }

    @Test
    public void delete() {
        User user = new User("username");
        UserHelper.getInstance().addUser(user);
        assertNotNull(UserHelper.getInstance().getAllContacts().get(user.username));
        mListPresenter.delete(user.username);
        assertNull(UserHelper.getInstance().getAllContacts().get(user.username));
        verify(mListView).reload(true);
    }

    @Test
    public void onRefresh() throws Exception {
        mListPresenter.refresh(getValidUsername());
        Thread.sleep(2000);
        verify(mListView).reload(true);
    }

    @Test
    public void onAddContactDialog() throws Exception {
        mListPresenter.onAddContactDialog(getValidUsername());
        verify(mListView, never()).reload(false);

        User user = new User(getValidUsername());
        UserHelper.getInstance().addUser(user);
        mListPresenter.onAddContactDialog(getValidUsername());
        verify(mListView).onAddContactDialog(user);

        Thread.sleep(2000);
        verify(mListView).reload(true);
    }
}
