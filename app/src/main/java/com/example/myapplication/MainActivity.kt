package com.example.myapplication;
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_main)
                val firstIntent = Intent(this, galleryActivity::class.java) // 인텐트를 생성

                hello1.setOnClickListener { // 버튼 클릭시 할 행동
                        startActivity(firstIntent)  // 화면 전환하기
                }
                val secondIntent = Intent(this, PhoneActivity::class.java) // 인텐트를 생성

                hello2.setOnClickListener { // 버튼 클릭시 할 행동
                        startActivity(secondIntent)  // 화면 전환하기
                }
        }
}