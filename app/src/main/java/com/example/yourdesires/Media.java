package com.example.yourdesires;

import android.net.Uri;

public class Media {
    private Uri img;
    public Media(Uri img){
        this.img = img;
    }

    public Uri getImg() {
        return img;
    }
}
