package com.example.yourdesires;

import android.net.Uri;

public class Media {
    private Uri img;
    private String name_desires;
    public Media(Uri img,String name_desires){
        this.img = img;
        this.name_desires = name_desires;
    }

    public String getName_desires() {
        return name_desires;
    }

    public Uri getImg() {
        return img;
    }
}
