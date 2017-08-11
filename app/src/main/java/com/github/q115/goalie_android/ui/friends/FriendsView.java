package com.github.q115.goalie_android.ui.friends;

import com.github.q115.goalie_android.ui.BaseView;

/**
 * Created by Qi on 8/9/2017.
 */

public interface FriendsView extends BaseView<FriendsPresenter> {
    @Override
    void setPresenter(FriendsPresenter presenter);

    void sendSMSInvite(String phoneNum);
}
