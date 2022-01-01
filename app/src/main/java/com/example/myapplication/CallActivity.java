package com.example.myapplication;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.data.Call;


public class CallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);

        ImageView img = (ImageView)findViewById(R.id.img);
        TextView title = (TextView)findViewById(R.id.txtTitle);
        TextView content = (TextView)findViewById(R.id.txtContent);
        ImageButton btn = (ImageButton)findViewById(R.id.close);
        ImageButton btn2 = (ImageButton)findViewById(R.id.call);

        Intent intent = getIntent();
        int id = intent.getExtras().getInt("POSITION");

        Call dto = PhoneActivity.callList.get(id); //dto / data tranform object

        img.setImageResource(dto.getDiffImg());
        title.setText(dto.getName());
        content.setText(dto.getPhoneNum());

        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + dto.getPhoneNum()));
                startActivity(intent);
            }
        });
    }

}
