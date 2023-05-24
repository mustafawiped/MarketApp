package com.mustafagur.marketim

import DatabaseHelper
import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.mustafagur.marketim.FragmentAdapters.ItemsFragmentAdapter
import com.mustafagur.marketim.FragmentAdapters.MainFragmentAdapter
import com.mustafagur.marketim.FragmentAdapters.SettingsFragmentAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/*  D E V E L O P E D    B Y    M U S T A F A W I P E D  */
class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onStart() {
        super.onStart()
        listeyiGuncelle()
    }

    private fun listeyiGuncelle() {
        dbHelper = DatabaseHelper(this)
        val cursor = dbHelper.getAllData()
        val fragmentManager = supportFragmentManager
        val fragmentList = fragmentManager.fragments
        for (fragment in fragmentList) {
            if (fragment is ItemsFragmentAdapter) {
                fragment.updateList(cursor)
                return
            }
        }
    }

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper = DatabaseHelper(this)
        val viewP: ViewPager2 = findViewById(R.id.viewPager)
        val mainW: TextView = findViewById(R.id.homeMenu)
        val itemsW: TextView = findViewById(R.id.productsMenu)
        val settingsW: TextView = findViewById(R.id.settingsMenu)
        viewP.adapter = MyPagerAdapter(this)
        mainW.setOnClickListener { viewP.currentItem = 0 }
        itemsW.setOnClickListener { viewP.currentItem = 1 }
        settingsW.setOnClickListener { viewP.currentItem = 2 }
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
    inner class MyPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MainFragmentAdapter()
                1 -> ItemsFragmentAdapter()
                2 -> SettingsFragmentAdapter()
                else -> MainFragmentAdapter()
            }
        }
    }
}
