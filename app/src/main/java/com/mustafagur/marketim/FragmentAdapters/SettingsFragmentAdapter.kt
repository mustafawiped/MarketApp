package com.mustafagur.marketim.FragmentAdapters

import DatabaseHelper
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.mustafagur.marketim.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SettingsFragmentAdapter: Fragment() {

    private lateinit var itemList: ArrayList<DataClass3_Expenses>
    private lateinit var listView: ListView
    private lateinit var text: TextView
    private var selectedDay = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        itemList = ArrayList<DataClass3_Expenses>()
        listView = view.findViewById(R.id.hrcList)
        text = view.findViewById(R.id.mesaj2)
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        selectedDay = sharedPreferences.getInt("selectedDay", -1)
        if (selectedDay == -1) {
            showDateSelectionDialog()
        } else {
            calculateExpensesForSelectedDay()
        }
        val btn: Button = view.findViewById(R.id.aylikkontrolbtn)
        btn.setOnClickListener {
            val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val daysInMonth = getDaysInMonth(currentMonth, currentYear)
            val daysList = ArrayList<String>()
            for (i in 1..daysInMonth) {
                daysList.add(i.toString())
            }
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Aylık olarak harcamaların hesaplamaları hangi günler aralığında yapılsın?")
            builder.setItems(daysList.toTypedArray()) { _, which ->
                selectedDay = daysList[which].toInt()
                val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().putInt("selectedDay", selectedDay).apply()
                calculateExpensesForSelectedDay()
            }
            builder.create().show()
        }
        return view
    }

    private fun showDateSelectionDialog() {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val daysInMonth = getDaysInMonth(currentMonth, currentYear)
        val daysList = ArrayList<String>()
        for (i in 1..daysInMonth) {
            daysList.add(i.toString())
        }
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Aylık olarak harcamaların hesaplamaları hangi günler aralığında yapılsın?")
        builder.setItems(daysList.toTypedArray()) { _, which ->
            selectedDay = daysList[which].toInt()
            val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().putInt("selectedDay", selectedDay).apply()
            calculateExpensesForSelectedDay()
        }
        builder.create().show()
    }

    @SuppressLint("Range")
    private fun calculateExpensesForSelectedDay() {
        val dbHelper = DatabaseHelper(requireContext())
        itemList.clear()
        if (selectedDay != -1) {
            var toplam = 0.0
            val currentDate = Calendar.getInstance()
            var bugununGUN = currentDate.get(Calendar.DAY_OF_MONTH)
            var bugununAY = currentDate.get(Calendar.MONTH) + 1
            var bugunYIL = currentDate.get(Calendar.YEAR)
            val cursor = dbHelper.getAllData()
            if (cursor != null && cursor.moveToFirst()) {
                while (cursor.moveToNext()) {
                    val dateStr = cursor.getString(cursor.getColumnIndex("uruneklenmet"))
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val date = dateFormat.parse(dateStr)
                    var tarih = dateFormat.format(date)
                    val bol = tarih.split("/")
                    var VERIgun = bol[0].toInt()
                    var VERIay = bol[1].toInt()
                    var VERIyil = bol[2].toInt()
                    Log.e("logaritam","bugununGUN: $bugununGUN")
                    Log.e("logaritam","bugununAY: $bugununAY")
                    Log.e("logaritam","veri gun: $VERIgun")
                    Log.e("logaritam","veri ay: $VERIay")
                    Log.e("logaritam","selecetd: $selectedDay")
                    if ((bugununGUN >= selectedDay && VERIgun >= selectedDay && bugununAY == VERIay) || (selectedDay > bugununGUN && selectedDay > VERIgun && bugununAY > VERIay) || (bugununGUN == VERIgun && bugununAY == VERIay) || (bugununGUN < selectedDay && VERIgun < selectedDay && VERIay == bugununAY)) {
                        val id = cursor.getInt(cursor.getColumnIndex("id"))
                        val urunadi = cursor.getString(cursor.getColumnIndex("urunadi"))
                        val urunfiat = cursor.getDouble(cursor.getColumnIndex("urunfiyati"))
                        val urunadet = cursor.getInt(cursor.getColumnIndex("urunadedi"))
                        val urunskt = cursor.getString(cursor.getColumnIndex("urunskt"))
                        val urunimg = cursor.getBlob(cursor.getColumnIndex("urunfotografi"))
                        val data = DataClass3_Expenses()
                        data.id = id
                        data.urunAdi = urunadi
                        data.urunFiyati = urunfiat
                        data.urunAdedi = urunadet
                        data.urunSkt = urunskt
                        data.urunFotografi = urunimg
                        data.urunUD = dateStr
                        itemList.add(data)
                        toplam += (urunfiat * urunadet.toDouble())
                    }
                }
                cursor.close()
                val adapter = ExpensesAdapterClass(itemList, requireContext())
                adapter.notifyDataSetChanged()
                listView.adapter = adapter
                val format = DecimalFormat("0.#")
                val sonuc = format.format(toplam)
                text.setText("Bu Ay Harcanan Tutar: $sonuc TL")
            }
        }
    }

    private fun getDaysInMonth(month: Int, year: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
}