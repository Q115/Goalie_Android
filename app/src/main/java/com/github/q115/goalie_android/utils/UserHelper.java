package com.github.q115.goalie_android.utils;

import com.github.q115.goalie_android.Constants;
import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.ImageHelper.ImageType;

import java.util.HashMap;


public class UserHelper {
    /// <summary>
    /// username -> User
    /// </summary>
    private HashMap<String, User> mAllContacts;

    public HashMap<String, User> getAllContacts() {
        return mAllContacts;
    }

    /// <summary>
    /// User information of device owner
    /// </summary>
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
        mAllContacts = new HashMap<>();
        mOwnerProfile = new User();
    }

    /// <summary>
    /// Populate Friends and PendingFriends list from mAllContacts, and also download image if needed
    /// </summary>
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

    /// <summary>
    /// Insert/replace user into database
    /// </summary>
    public boolean addUser(User user) {
        try {
            if (mAllContacts.containsKey(user.username)) {
                User oldUser = mAllContacts.get(user.username);
                oldUser.points = user.points;
                oldUser.bio = user.bio.length() > 0 ? user.bio : oldUser.bio;
                oldUser.profileBitmapImage = user.profileBitmapImage != null ? user.profileBitmapImage : oldUser.profileBitmapImage;
                oldUser.lastPhotoModifiedTime = user.lastPhotoModifiedTime > 0 ? user.lastPhotoModifiedTime : oldUser.lastPhotoModifiedTime;
                oldUser.save();
            } else {
                user.save();
                mAllContacts.put(user.username, user);
            }
            return true;
        } catch (Exception ex) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.UserHelper, "Error adding user: " + ex.toString());
            return false;
        }
    }

    public static boolean isUsernameValid(String username) {
        boolean isValid = (username != null && username.length() > 0 && username.length() <= Constants.MaxUsernameLength) && !username.equals("admin");
        return isValid && !username.contains(":") && !username.contains(" ") && !username.contains("/") && !username.contains("\\");
    }
}
