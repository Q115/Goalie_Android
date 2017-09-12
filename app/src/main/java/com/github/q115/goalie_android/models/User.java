package com.github.q115.goalie_android.models;

import android.graphics.Bitmap;

import com.github.q115.goalie_android.MainDB;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.HashMap;

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
    public HashMap<String, Goal> activeGoals; // username -> goal

    @ColumnIgnore
    public HashMap<String, Goal> finishedGoals; // username -> goal

    public User() {
        username = "";
        bio = "";
        reputation = 100;
        lastPhotoModifiedTime = 0;
        profileBitmapImage = null;
        activeGoals = new HashMap<>();
        finishedGoals = new HashMap<>();
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
}
