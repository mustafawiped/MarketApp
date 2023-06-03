package com.mustafagur.marketim.FragmentAdapters

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mustafagur.marketim.R
import java.util.*

class SettingsFragmentAdapter : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var listView: ListView
    private lateinit var text: TextView
    private lateinit var spinnerListeleme: Spinner
    private lateinit var spinnerAyar: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        listView = view.findViewById(R.id.hrcList)
        text = view.findViewById(R.id.mesaj2)
        spinnerListeleme = view.findViewById(R.id.spinner)
        spinnerAyar = view.findViewById(R.id.spinner2)

        val spinnerValues = resources.getStringArray(R.array.spinner_values)
        val spinner2Values = resources.getStringArray(R.array.spinner2_values)

        val spinnerListelemeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerValues
        )
        spinnerListelemeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerListeleme.adapter = spinnerListelemeAdapter
        spinnerListeleme.onItemSelectedListener = this

        val spinnerAyarAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinner2Values
        )
        spinnerAyarAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAyar.adapter = spinnerAyarAdapter
        spinnerAyar.onItemSelectedListener = this

        return view
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = parent?.getItemAtPosition(position) as? String
        val textView = view as? TextView
        textView?.setTextColor(Color.BLACK)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // bir şey seçilmediğinde olacaklar.
    }
}
