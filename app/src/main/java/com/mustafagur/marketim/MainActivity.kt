package com.mustafagur.marketim

import DatabaseHelper
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.mustafagur.marketim.FragmentAdapters.ItemsFragmentAdapter
import com.mustafagur.marketim.FragmentAdapters.MainFragmentAdapter
import com.mustafagur.marketim.FragmentAdapters.SettingsFragmentAdapter

/*  D E V E L O P E D    B Y    M U S T A F A W I P E D  */
class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var mainMenu: TextView
    private lateinit var productsMenu: TextView
    private lateinit var settingsMenu: TextView

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var notificationsReceiver: NotificationsClass

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
                fragment.updateList(cursor,this,true)
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

        //Navigation Drawer kısmı.
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.settings_nd -> {
                    drawerLayout.closeDrawer(navigationView)
                    val intent = Intent(this,SettingsActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.backnotifi_nd -> {
                    drawerLayout.closeDrawer(navigationView)
                    val intent = Intent(this,FeedbackActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.premium_nd -> {
                    drawerLayout.closeDrawer(navigationView)
                    val dialogView = LayoutInflater.from(this).inflate(R.layout.beta_dialog, null)
                    val builder = AlertDialog.Builder(this).setView(dialogView).setCancelable(true)
                    val dialog = builder.create()
                    val buttonOk: Button = dialogView.findViewById(R.id.button_ok)
                    buttonOk.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.setCanceledOnTouchOutside(true)
                    dialog.show()
                    true
                }
                R.id.contact_nd -> {
                    drawerLayout.closeDrawer(navigationView)
                    val intent = Intent(this,AboutActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        var detaylart: TextView = findViewById(R.id.detaylarText)
        detaylart.setOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }
        notificationsReceiver = NotificationsClass()
        val intentFilter = IntentFilter("com.mustafagur.marketim.SEND_NOTIFICATION")
        registerReceiver(notificationsReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationsReceiver)
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