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

<<<<<<< HEAD
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }
    //hi

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
=======
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
>>>>>>> master
        }
}
//