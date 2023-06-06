package com.mustafagur.marketim

import DatabaseHelper
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.mustafagur.marketim.FragmentAdapters.ItemsFragmentAdapter
import com.mustafagur.marketim.FragmentAdapters.MainFragmentAdapter
import com.mustafagur.marketim.FragmentAdapters.SettingsFragmentAdapter

/*  D E V E L O P E D    B Y    M U S T A F A W I P E D  */
class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var mainMenu: TextView
    private lateinit var productsMenu: TextView
    private lateinit var settingsMenu: TextView

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

    @SuppressLint("Range", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainMenu = findViewById(R.id.homeMenu)
        productsMenu = findViewById(R.id.productsMenu)
        settingsMenu = findViewById(R.id.settingsMenu)
        dbHelper = DatabaseHelper(this)
        val viewP: ViewPager2 = findViewById(R.id.viewPager)
        val mainW: TextView = findViewById(R.id.homeMenu)
        val itemsW: TextView = findViewById(R.id.productsMenu)
        val settingsW: TextView = findViewById(R.id.settingsMenu)
        viewP.adapter = MyPagerAdapter(this)
        mainW.setOnClickListener { viewP.currentItem = 0 }
        itemsW.setOnClickListener { viewP.currentItem = 1 }
        settingsW.setOnClickListener { viewP.currentItem = 2 }
        viewP.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateMenuColors(position)
            }
        })
        var detaylart: TextView = findViewById(R.id.detaylarText)
        detaylart.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.beta_dialog, null)
            val builder = AlertDialog.Builder(this).setView(dialogView).setCancelable(true)
            val dialog = builder.create()
            val buttonOk: Button = dialogView.findViewById(R.id.button_ok)
            buttonOk.setOnClickListener {
                dialog.dismiss()
            }
            dialog.setCanceledOnTouchOutside(true)
            dialog.show()
        }
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
    private fun updateMenuColors(position: Int) {
        val beyaz = Color.WHITE
        val siyah = Color.BLACK
        when (position) {
            0 -> {
                mainMenu.setTextColor(beyaz)
                productsMenu.setTextColor(siyah)
                settingsMenu.setTextColor(siyah)
                mainMenu.setBackgroundResource(R.drawable.edittext_background)
                productsMenu.setBackgroundResource(R.drawable.layout_background)
                settingsMenu.setBackgroundResource(R.drawable.layout_background)
            }
            1 -> {
                productsMenu.setTextColor(beyaz)
                mainMenu.setTextColor(siyah)
                settingsMenu.setTextColor(siyah)
                productsMenu.setBackgroundResource(R.drawable.edittext_background)
                mainMenu.setBackgroundResource(R.drawable.layout_background)
                settingsMenu.setBackgroundResource(R.drawable.layout_background)
            }
            2 -> {
                settingsMenu.setTextColor(beyaz)
                productsMenu.setTextColor(siyah)
                mainMenu.setTextColor(siyah)
                settingsMenu.setBackgroundResource(R.drawable.edittext_background)
                productsMenu.setBackgroundResource(R.drawable.layout_background)
                mainMenu.setBackgroundResource(R.drawable.layout_background)
            }
        }
    }
}

