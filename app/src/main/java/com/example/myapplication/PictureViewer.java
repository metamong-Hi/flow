package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

public class PictureViewer extends LinearLayout {

    ImageView imageView;
    public PictureViewer(Context context) {
        super(context);

        init(context);
    }
    public PictureViewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_photo,this,true);

        imageView = (ImageView) findViewById(R.id.photo_item);
    }

    public void setItem(Uri pictureItem){
        Glide.with(this).load(pictureItem).into(imageView);
    }
}