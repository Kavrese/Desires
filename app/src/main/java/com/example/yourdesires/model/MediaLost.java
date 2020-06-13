package com.example.yourdesires.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
@Table(database = MyDataBase.class)
public class MediaLost extends BaseModel {
    @Column
    int id_desires;
    @Column
    @PrimaryKey
    int media_id;
    @Column
    String uri;

    public int getMedia_id() {
        return media_id;
    }

    public String getUri() {
        return uri;
    }

    public int getId_desires() {
        return id_desires;
    }

    public void setId_desires(int id_desires) {
        this.id_desires = id_desires;
    }

    public void setMedia_id(int media_id) {
        this.media_id = media_id;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
