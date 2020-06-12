package com.example.yourdesires;

import android.annotation.SuppressLint;
import android.app.Application;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.yourdesires.model.Lost;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.CursorResult;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.List;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
}