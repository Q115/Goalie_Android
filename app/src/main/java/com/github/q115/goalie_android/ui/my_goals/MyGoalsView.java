package com.github.q115.goalie_android.ui.my_goals;

import com.github.q115.goalie_android.ui.BaseView;

/**
 * Created by Qi on 8/4/2017.
 */

public interface MyGoalsView extends BaseView<MyGoalsPresenter> {
    void showFABMenu();

    void closeFABMenu();
}
