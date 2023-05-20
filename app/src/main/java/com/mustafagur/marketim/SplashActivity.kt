package com.mustafagur.marketim

import DatabaseHelper
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mustafagur.marketim.FragmentAdapters.ItemsFragmentAdapter

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val dbHelper = DatabaseHelper(this)
        val cursor = dbHelper.getAllData()
        val fragmentManager = supportFragmentManager
        val fragmentList = fragmentManager.fragments
        for (fragment in fragmentList) {
            if (fragment is ItemsFragmentAdapter) {
                fragment.updateList(cursor)
            }
        }
        Thread.sleep(2000)
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
