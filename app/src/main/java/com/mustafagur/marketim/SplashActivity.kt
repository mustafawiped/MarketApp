package com.mustafagur.marketim

import DatabaseHelper
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mustafagur.marketim.FragmentAdapters.ItemsFragmentAdapter
import com.mustafagur.marketim.MainActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        settings()
        load()
    }

    @SuppressLint("Range")
    private fun settings() {
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val control1 = sharedPreferences.getBoolean("otomatiksilme", false)
        val control3 = sharedPreferences.getBoolean("oncekiyilotomatiksil", false)
        if (control1) {
            val database = DatabaseHelper(this)
            val cursor = database.getAllData()
            val mevcutTRH = Calendar.getInstance().time
            val TRHformat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val sktString = cursor.getString(cursor.getColumnIndex("urunskt"))
                    val sonKtarihi = TRHformat.parse(sktString)
                    if (((sonKtarihi.time - mevcutTRH.time) / (24L * 60L * 60L * 1000L)).toInt() <= 0) {
                        var id = cursor.getInt(cursor.getColumnIndex("id"))
                        database.deleteData(id)
                    }
                } while (cursor.moveToNext())
            }
        }
        if (control3) {
            val database = DatabaseHelper(this)
            val cursor = database.getAllData()
            val TRHformat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val edString = cursor.getString(cursor.getColumnIndex("uruneklenmet"))
                    val sktString = cursor.getString(cursor.getColumnIndex("urunskt"))
                    val eklenmeTarihi = TRHformat.parse(edString)
                    val cal = Calendar.getInstance()
                    cal.time = eklenmeTarihi
                    val eklenmeYili = cal.get(Calendar.YEAR)
                    val suankiYil = Calendar.getInstance().get(Calendar.YEAR)
                    val bugun = Calendar.getInstance().time
                    val sktTarihi = TRHformat.parse(sktString)
                    if (eklenmeYili != suankiYil && bugun.after(sktTarihi)) {
                        val id = cursor.getInt(cursor.getColumnIndex("id"))
                        database.deleteData(id)
                    }
                } while (cursor.moveToNext())
            }
        }
    }

    private fun load() {
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
