package com.example.yourdesires.model;

import android.net.Uri;
import android.widget.Toast;

import androidx.core.view.accessibility.AccessibilityViewCommand;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Table(database = MyDataBase.class)
public class Lost extends BaseModel {

    @Column
    @PrimaryKey
    int id;
    @Column
    String desires;
    @Column
    String tag1;
    @Column
    String tag2;
    @Column
    String op;
    @Column
    int status;
    @Column
    String data;

    public int getId(){return id;}

    public void setId(int id) {
        this.id = id;
    }

    public String getDesires() {
        return desires;}

    public void setDesires(String desires) { this.desires = desires;}

    public String getData() {
        return data;
    }

    public String getTag1() {
        return tag1;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTag2() {
        return tag2;
    }

    public String getOp() {
        return op;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setOp(String op) {
        this.op = op;
    }
}
