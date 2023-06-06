package com.mustafagur.marketim

import DatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mustafagur.marketim.FragmentAdapters.ItemsFragmentAdapter
import com.mustafagur.marketim.MainActivity
import java.util.*

class SplashActivity : AppCompatActivity() {

    private val beklemesure: Long = 2000 // bekleme süresi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        load()
    }

    private fun load() {
            Handler().postDelayed({
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, beklemesure)
        }
    }
