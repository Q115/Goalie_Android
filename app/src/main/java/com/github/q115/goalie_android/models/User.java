package com.github.q115.goalie_android.models;

import android.graphics.Bitmap;

import com.github.q115.goalie_android.MainDB;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;

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
    public long reputation;

    @ColumnIgnore
    public Bitmap profileBitmapImage;

    @ColumnIgnore
    public ArrayList<Goal> activieGoals;

    @ColumnIgnore
    public ArrayList<Goal> finishedGoals;

    public User() {
        username = "";
        bio = "";
        reputation = 100;
        lastPhotoModifiedTime = 0;
        profileBitmapImage = null;
        activieGoals = null;
        finishedGoals = null;
    }

    public User(String username) {
        this();
        this.username = username;
    }

    public User(String username, long reputation) {
        this();
        this.username = username;
        this.reputation = reputation;
    }

    public User(String username, String bio, long reputation, long lastPhotoModifiedTime) {
        this();
        this.username = username;
        this.reputation = reputation;
        this.bio = bio;
        this.lastPhotoModifiedTime = lastPhotoModifiedTime;
    }

    public void addActivitGoal(Goal goal) {
        if (activieGoals == null)
            activieGoals = new ArrayList<>();
        activieGoals.add(goal);
    }

    public void addCompleteGoal(Goal goal) {
        if (finishedGoals == null)
            finishedGoals = new ArrayList<>();
        finishedGoals.add(goal);
    }
}
