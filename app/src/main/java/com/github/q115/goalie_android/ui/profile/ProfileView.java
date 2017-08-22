package com.github.q115.goalie_android.ui.profile;

import android.graphics.Bitmap;

import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.ui.BaseView;

import java.util.ArrayList;

/**
 * Created by Qi on 8/6/2017.
 */

public interface ProfileView extends BaseView<ProfilePresenter> {
    void setupForOwner(boolean isOwner);

    void setupView(String username, String bio, long reputation);

    void updateProgress(boolean shouldShow);

    void showUploadError(String msg);

    void uploadSuccess(Bitmap bitmap);

    void reloadList(boolean shouldReloadList);
}
