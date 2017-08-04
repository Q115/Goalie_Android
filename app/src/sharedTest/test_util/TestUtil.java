package test_util;

import com.github.q115.goalie_android.Diagnostic;
import com.github.q115.goalie_android.models.Goal;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.utils.UserHelper;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class TestUtil {
    public static boolean isGoalEqual(Goal goalA, Goal goalB) {
        boolean isEqual;
        isEqual = goalA.guid.equals(goalB.guid);
        isEqual &= goalA.title.equals(goalB.title);
        isEqual &= goalA.deadline == goalB.deadline;
        isEqual &= goalA.wager == goalB.wager;
        return isEqual;
    }

    public static boolean isUserEqual(User userA, User userB) {
        boolean isEqual;
        isEqual = userA.username.equals(userB.username);
        isEqual &= userA.bio.equals(userB.bio);
        isEqual &= userA.lastPhotoModifiedTime == userB.lastPhotoModifiedTime;
        isEqual &= userA.points == userB.points;
        isEqual &= ((userA.profileBitmapImage != null && userB.profileBitmapImage != null) || userA.profileBitmapImage == userB.profileBitmapImage);
        return isEqual;
    }

    public static void ReadDatabase() {
        try {
            List<User> users = SQLite.select().from(User.class).queryList();
            //populate contacts
            for (User user : users) {
                UserHelper.getInstance().getAllContacts().put(user.username, user);
            }
        } catch (OutOfMemoryError outOfMemoryException) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.MainApplication, "Too many entries: " + outOfMemoryException.toString());
        } catch (Exception ex) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.MainApplication, "Error loading database: " + ex.toString());
        }
    }
}