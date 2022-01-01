package com.example.myapplication;

import android.net.Uri;

public class PictureItem {

    private Uri image;

    public PictureItem(Uri image) {
        this.image = image;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }
}