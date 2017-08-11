package com.github.q115.goalie_android.ui.friends;

import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.ui.BaseView;

/**
 * Created by Qi on 8/6/2017.
 */

public interface FriendsListView extends BaseView<FriendsListPresenter> {
    void onAddContactDialog(User user);

    void reload(boolean shouldReloadList);
}
