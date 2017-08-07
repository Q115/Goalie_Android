package com.github.q115.goalie_android.presenterTest;

import com.github.q115.goalie_android.ui.my_goals.MyGoalsPresenter;
import com.github.q115.goalie_android.ui.my_goals.MyGoalsView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by Qi on 8/5/2017.
 */

public class MyGoalsPresenterUnitTest {
    private MyGoalsPresenter mPresenter;

    @Mock
    private MyGoalsView mView;

    @Before
    public void setup() {
        mView = mock(MyGoalsView.class);
        mPresenter = new MyGoalsPresenter(mView);
        verify(mView).setPresenter(mPresenter);
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
}
