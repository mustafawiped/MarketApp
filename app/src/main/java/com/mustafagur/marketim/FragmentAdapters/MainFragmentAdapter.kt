package com.mustafagur.marketim.FragmentAdapters

import DatabaseHelper
import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mustafagur.marketim.ItemsAdapterClass
import com.mustafagur.marketim.R
import com.mustafagur.marketim.SktAdapterClass
import java.text.SimpleDateFormat
import java.util.*

class MainFragmentAdapter : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var list: ArrayList<DataClass2>
    private lateinit var listView: ListView

    @SuppressLint("MissingInflatedId")
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
        listView = view.findViewById(R.id.sktyaklasanList)
        list = ArrayList<DataClass2>()
        dbHelper = DatabaseHelper(requireContext())
        sktyaklasanlar()
        return view
    }

    @SuppressLint("Range")
    private fun sktyaklasanlar() {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val cursor: Cursor? = dbHelper.getAllData()
        list.clear()
        cursor?.let {
            while (cursor.moveToNext()) {
                val expiryDateString = cursor.getString(cursor.getColumnIndex("urunskt"))
                val expiryDate = dateFormat.parse(expiryDateString)
                if (expiryDate != null && expiryDate.time - currentDate.time < 90L * 24L * 60L * 60L * 1000L) {
                    val productName = cursor.getString(cursor.getColumnIndex("urunadi"))
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    var daysRemaining = ((expiryDate.time - currentDate.time) / (24L * 60L * 60L * 1000L)).toString()
                    var control = daysRemaining.toIntOrNull()
                    if (control != null && control <= 0) daysRemaining = "SKT Geçti"
                    val urunimg = cursor.getBlob(cursor.getColumnIndex("urunfotografi"))
                    val urunskt = cursor.getString(cursor.getColumnIndex("urunskt"))
                    val data = DataClass2()
                    data.id = id
                    data.urunAdi = productName
                    data.urunKalanGun = daysRemaining
                    data.urunSkt = urunskt
                    data.urunFotografi = urunimg
                    list.add(data)
                }
            }
            cursor.close()
            val adapterclass = SktAdapterClass(list, requireContext())
            adapterclass.notifyDataSetChanged()
            listView.adapter = adapterclass
        }
    }
}



