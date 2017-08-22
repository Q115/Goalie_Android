package com.github.q115.goalie_android.presenterTest;

import android.graphics.Bitmap;

import com.github.q115.goalie_android.BaseTest;
import com.github.q115.goalie_android.https.RESTSync;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.ui.my_goals.MyGoalsPresenter;
import com.github.q115.goalie_android.ui.my_goals.MyGoalsView;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static test_util.TestUtil.getValidUsername;

/**
 * Created by Qi on 8/5/2017.
 */

public class MyGoalsPresenterUnitTest extends BaseTest {
    private MyGoalsPresenter mPresenter;

    @Mock
    private MyGoalsView mView;

    @Before
    public void setup() {
        mView = mock(MyGoalsView.class);
        mPresenter = spy(new MyGoalsPresenter(mView));
    }

    @Test
    public void newGoalsClick() {
        // expands menu
        mPresenter.toggleFAB();
        verify(mView, never()).closeFABMenu();
        verify(mView).showFABMenu();
        assertTrue(mPresenter.isFABOpen());

        // collapse menu
        mPresenter.toggleFAB();
        verify(mView).closeFABMenu();
        assertFalse(mPresenter.isFABOpen());
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

    @Test
    public void showDialog() {
        String title = "title";
        String end = "end";
        String start = "start";
        String reputation = "reputation";
        String encouragement = "encouragement";
        String referee = "referee";
        Bitmap profileImage = null;
        Goal.GoalCompleteResult goalCompleteResult = Goal.GoalCompleteResult.Success;
        String guid = "guid";

        mPresenter.showDialog(title, end, start, reputation, encouragement, referee, profileImage, goalCompleteResult, guid);
        verify(mView).showDialog(title, end, start, reputation, encouragement, referee, profileImage, goalCompleteResult, guid);
    }
}
