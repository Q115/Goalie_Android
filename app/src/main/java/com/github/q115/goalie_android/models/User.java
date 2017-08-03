package com.github.q115.goalie_android.models;

import android.graphics.Bitmap;

import com.github.q115.goalie_android.MainDB;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = MainDB.class)
public class User extends BaseModel {
    @Column
    @PrimaryKey
    public String username;

    @Column
    public String bio;

    @Column
    public long lastPhotoModifiedTime;

    @Column
    public long points;

    @ColumnIgnore
    public Bitmap profileBitmapImage;

    public User() {
        username = "";
        bio = "";
        points = 100;
        lastPhotoModifiedTime = 0;
        profileBitmapImage = null;
    }

    public User(String username) {
        this();
        this.username = username;
    }

    public User(String username, long points) {
        this();
        this.username = username;
        this.points = points;
    }

    public User(String username, String bio, long points, long lastPhotoModifiedTime) {
        this();
        this.username = username;
        this.points = points;
        this.bio = bio;
        this.lastPhotoModifiedTime = lastPhotoModifiedTime;
    }
}
