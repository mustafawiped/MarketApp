package com.mustafagur.marketim.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.mustafagur.marketim.ItemActivity
import com.mustafagur.marketim.MainActivity
import com.mustafagur.marketim.R

class ItemsFragment : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_items)
    }
    fun urunEkle(view: View) {
        val intent = Intent(this@ItemsFragment, ItemActivity::class.java)
        startActivity(intent)
    }
}