package com.github.q115.goalie_android;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = MainDB.NAME, version = MainDB.VERSION)
public class MainDB {
    static final String NAME = "MainDB";
    static final int VERSION = 1;
}