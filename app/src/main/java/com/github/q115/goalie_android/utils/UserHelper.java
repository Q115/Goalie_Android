package com.github.q115.goalie_android.utils;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.GoalFeed;
import com.github.q115.goalie_android.models.Goal_Table;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.models.User_Table;
import com.github.q115.goalie_android.utils.ImageHelper.ImageType;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

/*
 * Copyright 2017 Qi Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class UserHelper {
    // username -> User
    private SortedMap<String, User> mAllContacts;

    public SortedMap<String, User> getAllContacts() {
        return mAllContacts;
    }

    // User information of device owner
    private User mOwnerProfile;

    public User getOwnerProfile() {
        return mOwnerProfile;
    }

    public boolean setOwnerProfile(User user) {
        if (addUser(user)) {
            PreferenceHelper.getInstance().setAccountUsername(user.username);
            mOwnerProfile = user;
            return true;
        }
        return false;
    }

    private static UserHelper mInstance;

    private UserHelper() {
    }

    public static synchronized UserHelper getInstance() {
        if (mInstance == null) {
            mInstance = new UserHelper();
        }

        return mInstance;
    }

    public void initialize() {
        mAllContacts = new TreeMap<>();
        mOwnerProfile = new User();
    }

    // Load profile image of friends from storage
    public void LoadContacts() {
        for (User value : mAllContacts.values()) {
            // TODO: load on demand vs all at once & load async
            if (ImageHelper.getInstance().isImageOnPrivateStorage(value.username, ImageType.PNG)) {
                value.profileBitmapImage = ImageHelper.getInstance().loadImageFromPrivateSorageSync(value.username, ImageType.PNG);
            }

            if (value.username.equals(mOwnerProfile.username))
                mOwnerProfile = value;
        }
    }

    // Insert/replace user into database
    public boolean addUser(User user) {
        try {
            if (mAllContacts.containsKey(user.username)) {
                User oldUser = mAllContacts.get(user.username);
                oldUser.reputation = user.reputation;
                oldUser.bio = user.bio.length() > 0 ? user.bio : oldUser.bio;
                oldUser.profileBitmapImage = user.profileBitmapImage != null ? user.profileBitmapImage : oldUser.profileBitmapImage;
                oldUser.lastPhotoModifiedTime = user.lastPhotoModifiedTime > 0 ? user.lastPhotoModifiedTime : oldUser.lastPhotoModifiedTime;
                oldUser.activieGoals = user.activieGoals != null ? user.activieGoals : oldUser.activieGoals;
                oldUser.finishedGoals = user.finishedGoals != null ? user.finishedGoals : oldUser.finishedGoals;

                oldUser.save();
            } else {
                mAllContacts.put(user.username, user);
                user.save();
            }
            return true;
        } catch (Exception ex) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.UserHelper, "Error adding user: " + ex.toString());
            return false;
        }
    }

    public boolean deleteUser(String username) {
        try {
            getAllContacts().remove(username);
            SQLite.delete().from(User.class).where(User_Table.username.eq(username)).execute();
            ImageHelper.getInstance().deleteImageFromPrivateStorage(username, ImageType.PNG);

            return true;
        } catch (Exception ex) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.UserHelper, "Error adding goal: " + ex.toString());
            return false;
        }
    }

    public static boolean isUsernameValid(String username) {
        boolean isValid = (username != null && username.length() >= 4 && username.length() <= Constants.MAX_USERNAME_LENGTH) && !username.equals("admin");
        return isValid && !username.contains(":") && !username.contains(" ") && !username.contains("/") && !username.contains("\\");
    }
}
