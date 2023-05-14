package com.mustafagur.marketim.Fragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.mustafagur.marketim.R

class MainFragment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main)
        Toast.makeText(this,"Merhaba",Toast.LENGTH_LONG).show()
    }
}