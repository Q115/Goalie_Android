package com.github.q115.goalie_android;

import android.app.Application;
import android.content.Intent;

import com.github.q115.goalie_android.https.MainVolleyRequestQueue;
import com.github.q115.goalie_android.models.User;
import com.github.q115.goalie_android.services.InstanceIDService;
import com.github.q115.goalie_android.utils.ImageHelper;
import com.github.q115.goalie_android.utils.PreferenceHelper;
import com.github.q115.goalie_android.utils.UserHelper;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class MainApplication extends Application {
    private static MainApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        // instantiates DBFlow
        FlowManager.init(new FlowConfig.Builder(this).build());

        // instantiate volley request
        MainVolleyRequestQueue.getInstance().initialize(this);

        // start push notification service
        startService(new Intent(this, InstanceIDService.class));

        // instantiate app data
        initialize();
    }

    public void initialize() {
        if (mInstance == null) {
            mInstance = new MainApplication();

            ImageHelper.getInstance().initialize(this);
            UserHelper.getmInstance().initalize();
            PreferenceHelper.getInstance().initialize(this);
            ReadDatabase();
            UserHelper.getmInstance().LoadContacts();
        }
    }

    private void ReadDatabase() {
        try {
            List<User> users = SQLite.select().from(User.class).queryList();
            //populate contacts
            for (User user : users) {
                UserHelper.getmInstance().getAllContacts().put(user.username, user);
            }
        } catch (OutOfMemoryError outOfMemoryException) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.MainApplication, "Too many entries: " + outOfMemoryException.toString());
        } catch (Exception ex) {
            Diagnostic.logError(Diagnostic.DiagnosticFlag.MainApplication, "Error loading database: " + ex.toString());
        }
    }
}
