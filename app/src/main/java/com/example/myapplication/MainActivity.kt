package com.example.myapplication;
import android.app.ActionBar
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firstIntent = Intent(this, galleryActivity::class.java) // 인텐트를 생성
        hello1.setOnClickListener { // 버튼 클릭시 할 행동
            Log.d("MainActivity", "1번")
            startActivity(firstIntent)  // 화면 전환하기
        }

        Log.d("MainActivity", "2번")
        val secondIntent = Intent(this, PhoneActivity::class.java) // 인텐트를 생성
        hello2.setOnClickListener { // 버튼 클릭시 할 행동
            Log.d("MainActivity", "2번")
            startActivity(secondIntent)  // 화면 전환하기
        }

        val thirdIntent = Intent(this, StopWatchActivity::class.java) // 인텐트를 생성
        hello3.setOnClickListener { // 버튼 클릭시 할 행동
            Log.d("MainActivity", "3번")
            startActivity(thirdIntent)  // 화면 전환하기
        }
    }
}