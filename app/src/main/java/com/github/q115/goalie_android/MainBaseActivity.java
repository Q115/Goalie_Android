package com.github.q115.goalie_android;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainBaseActivity extends AppCompatActivity {

    @Keep
    public MainBaseActivity() {
        super();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (isGestureMode(this)) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.transparent));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isGestureMode(AppCompatActivity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android");
            if (resourceId > 0) {
                return resources.getInteger(resourceId) == 2;
            }
        }
        return false;
    }
}
