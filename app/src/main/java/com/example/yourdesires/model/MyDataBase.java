package com.example.yourdesires.model;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = MyDataBase.NAME, version = MyDataBase.VERSION)
public class MyDataBase {
    public static final String NAME = "MyDataBaseOne";
    public static final int VERSION = 3;
}
