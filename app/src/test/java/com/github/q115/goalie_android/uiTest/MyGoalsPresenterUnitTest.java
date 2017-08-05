package com.github.q115.goalie_android.uiTest;

import android.widget.Button;

import com.github.q115.goalie_android.R;
import com.github.q115.goalie_android.ui.my_goals.MyGoalsPresenter;
import com.github.q115.goalie_android.ui.my_goals.MyGoalsView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by Qi on 8/5/2017.
 */

public class MyGoalsPresenterUnitTest {
    private MyGoalsPresenter presenter;

    @Mock
    private MyGoalsView view;

    @Before
    public void setup() {
        view = mock(MyGoalsView.class);
        presenter = new MyGoalsPresenter(view);
        verify(view).setPresenter(presenter);
    }

    @Test
    public void newGoalsClick() {
        // expands menu
        presenter.toggleFAB();
        verify(view, never()).closeFABMenu();
        verify(view).showFABMenu();
        assertTrue(presenter.isFABOpen());

        // collapse menu
        presenter.toggleFAB();
        verify(view).closeFABMenu();
        assertFalse(presenter.isFABOpen());
    }
}
