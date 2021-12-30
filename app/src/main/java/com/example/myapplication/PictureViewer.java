package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

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
        inflater.inflate(R.layout.picture,this,true);

        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void setItem(PictureItem pictureItem){
        imageView.setImageResource(pictureItem.getImage());
    }
}