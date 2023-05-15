package com.mustafagur.marketim

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.mustafagur.marketim.FragmentAdapters.ItemsFragmentAdapter
import com.mustafagur.marketim.FragmentAdapters.MainFragmentAdapter
import com.mustafagur.marketim.FragmentAdapters.SettingsFragmentAdapter
import com.mustafagur.marketim.Fragments.ItemsFragment
import com.mustafagur.marketim.Fragments.MainFragment

/*  D E V E L O P E D    B Y    M U S T A F A W I P E D  */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewP: ViewPager2 = findViewById(R.id.viewPager)
        val mainW: TextView = findViewById(R.id.homeMenu)
        val itemsW: TextView = findViewById(R.id.productsMenu)
        val settingsW: TextView = findViewById(R.id.settingsMenu)
        viewP.adapter = MyPagerAdapter(this)
        mainW.setOnClickListener { viewP.currentItem = 0 }
        itemsW.setOnClickListener { viewP.currentItem = 1 }
        settingsW.setOnClickListener { viewP.currentItem = 2 }
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
