package com.mustafagur.marketim.FragmentAdapters

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.mustafagur.marketim.ItemActivity
import com.mustafagur.marketim.R

class ItemsFragmentAdapter: Fragment() {
    private lateinit var btnFragmentItems: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_items, container, false)
        btnFragmentItems = view.findViewById(R.id.btnFragmentItems)
        btnFragmentItems.setOnClickListener {
            urunEkle(it)
        }
        return view
    }
    private fun urunEkle(view: View) {
        val intent = Intent(requireContext(), ItemActivity::class.java)
        startActivity(intent)
    }
}
