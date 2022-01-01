package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class timeViewer extends LinearLayout {

    TextView textView;
    public timeViewer(Context context) {
        super(context);

        init(context);
    }

    public timeViewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.timeitem,this,true);

        textView = (TextView)findViewById(R.id.timeView);
    }

    public void setItem(TimeItem singerItem){
        textView.setText(singerItem.getTime());
    }
}