package com.mustafagur.marketim.FragmentAdapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mustafagur.marketim.R
import java.util.*

class MainFragmentAdapter : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val greeting = when (currentHour) {
            in 6..11 -> "Günaydınlar!"
            in 12..17 -> "Tünaydın!"
            in 18..20 -> "İyi akşamlar!"
            else -> "İyi geceler!"
        }
        val textView = view.findViewById<TextView>(R.id.mesaj)
        textView.text = greeting

        return view
    }
}



