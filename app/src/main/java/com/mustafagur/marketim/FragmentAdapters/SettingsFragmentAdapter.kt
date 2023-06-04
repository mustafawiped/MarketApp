package com.mustafagur.marketim.FragmentAdapters

import DatabaseHelper
import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.mustafagur.marketim.R
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragmentAdapter : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var listView: ListView
    private lateinit var text: TextView
    private lateinit var spinnerListeleme: Spinner
    private lateinit var spinnerAyar: Spinner
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var dataList: ArrayList<DataClass3_Expenses>
    private lateinit var adapter: ExpensesAdapterClass

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

        val spinnerListelemeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerValues)
        spinnerListelemeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerListeleme.adapter = spinnerListelemeAdapter
        spinnerListeleme.onItemSelectedListener = this

        val spinnerAyarAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item, spinner2Values)
        spinnerAyarAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAyar.adapter = spinnerAyarAdapter
        spinnerAyar.onItemSelectedListener = this

        databaseHelper = DatabaseHelper(requireContext())
        fetchDataFromDatabase()

        return view
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = parent?.getItemAtPosition(position) as? String
        val textView = view as? TextView
        textView?.setTextColor(Color.BLACK)

        when (selectedItem) {
            "Son 30 Günün Harcamaları" -> filterDataByLast30Days()
            "Tüm Harcamaları Göster" -> fetchDataFromDatabase()
            "Ocak Ayı Harcamaları" -> filterDataByMonth(Calendar.JANUARY)
            "Şubat Ayı Harcamaları" -> filterDataByMonth(Calendar.FEBRUARY)
            "Mart Ayı Harcamaları" -> filterDataByMonth(Calendar.MARCH)
            "Nisan Ayı Harcamaları" -> filterDataByMonth(Calendar.APRIL)
            "Mayıs Ayı Harcamaları" -> filterDataByMonth(Calendar.MAY)
            "Haziran Ayı Harcamaları" -> filterDataByMonth(Calendar.JUNE)
            "Temmuz Ayı Harcamaları" -> filterDataByMonth(Calendar.JULY)
            "Ağustos Ayı Harcamaları" -> filterDataByMonth(Calendar.AUGUST)
            "Eylül Ayı Harcamaları" -> filterDataByMonth(Calendar.SEPTEMBER)
            "Ekim Ayı Harcamaları" -> filterDataByMonth(Calendar.OCTOBER)
            "Kasım Ayı Harcamaları" -> filterDataByMonth(Calendar.NOVEMBER)
            "Aralık Ayı Harcamaları" -> filterDataByMonth(Calendar.DECEMBER)
            "En Yüksek Fiyata Göre Sırala" -> applyFilters()
            "En Düşük Fiyata Göre Sırala" -> applyFilters()
            "En Yakın Tarihe Göre Sırala" -> applyFilters()
            "En Geç Tarihe Göre Sırala" -> applyFilters()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // bir şey seçilmediğinde olacaklar.
    }

    @SuppressLint("Range")
    private fun fetchDataFromDatabase() {
        val cursor: Cursor? = databaseHelper.getAllData()
        dataList = ArrayList<DataClass3_Expenses>()
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    var uid = cursor.getInt(cursor.getColumnIndex("id"))
                    var uadi = cursor.getString(cursor.getColumnIndex("urunadi"))
                    var ufiyat = cursor.getDouble(cursor.getColumnIndex("urunfiyati"))
                    var uadet = cursor.getInt(cursor.getColumnIndex("urunadedi"))
                    var ufoto = cursor.getBlob(cursor.getColumnIndex("urunfotografi"))
                    var uskt = cursor.getString(cursor.getColumnIndex("urunskt"))
                    var uet = cursor.getString(cursor.getColumnIndex("uruneklenmet"))
                    var data = DataClass3_Expenses()
                    data.id = uid
                    data.urunAdi = uadi
                    data.urunFiyati = ufiyat * uadet
                    data.urunAdedi = uadet
                    data.urunFotografi = ufoto
                    data.urunSkt = uskt
                    data.urunUD = uet
                    dataList.add(data)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }

        if (dataList.isNotEmpty()) {
            adapter = ExpensesAdapterClass(dataList, requireContext())
            applyFilters()
            listView.adapter = adapter

            if (dataList.isEmpty()) {
                text.text = "Herhangi bir harcama yok."
            } else {
                val toplamfiyat = dataList.sumByDouble { it.urunFiyati }
                text.text = "Toplam Harcama: $toplamfiyat TL"
            }
        } else {
            text.text = "Herhangi bir harcama yok."
        }
    }


    @SuppressLint("Range")
    private fun filterDataByMonth(month: Int) {
        val calendar = Calendar.getInstance()
        val cursor: Cursor? = databaseHelper.getAllData()
        val filteredList = ArrayList<DataClass3_Expenses>()
        val addList = ArrayList<DataClass3_Expenses>()
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    var uid = cursor.getInt(cursor.getColumnIndex("id"))
                    var uadi = cursor.getString(cursor.getColumnIndex("urunadi"))
                    var ufiyat = cursor.getDouble(cursor.getColumnIndex("urunfiyati"))
                    var uadet = cursor.getInt(cursor.getColumnIndex("urunadedi"))
                    var ufoto = cursor.getBlob(cursor.getColumnIndex("urunfotografi"))
                    var uskt = cursor.getString(cursor.getColumnIndex("urunskt"))
                    var uet = cursor.getString(cursor.getColumnIndex("uruneklenmet"))
                    var data = DataClass3_Expenses()
                    data.id = uid
                    data.urunAdi = uadi
                    data.urunFiyati = ufiyat * uadet
                    data.urunAdedi = uadet
                    data.urunFotografi = ufoto
                    data.urunSkt = uskt
                    data.urunUD = uet
                    filteredList.add(data)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        for (data in filteredList) {
            calendar.time = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(data.urunUD) ?: Date()
            if (calendar.get(Calendar.MONTH) == month) {
                addList.add(data)
            }
        }
        dataList = addList
        applyFilters()
        Log.e("TAG","çalıştı 3 ${dataList.size}")
    }
    @SuppressLint("Range")
    private fun filterDataByLast30Days() {
        val calendar = Calendar.getInstance()
        val today = calendar.time
        val cursor: Cursor? = databaseHelper.getAllData()
        val filteredList = ArrayList<DataClass3_Expenses>()
        val addList = ArrayList<DataClass3_Expenses>()
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    var uid = cursor.getInt(cursor.getColumnIndex("id"))
                    var uadi = cursor.getString(cursor.getColumnIndex("urunadi"))
                    var ufiyat = cursor.getDouble(cursor.getColumnIndex("urunfiyati"))
                    var uadet = cursor.getInt(cursor.getColumnIndex("urunadedi"))
                    var ufoto = cursor.getBlob(cursor.getColumnIndex("urunfotografi"))
                    var uskt = cursor.getString(cursor.getColumnIndex("urunskt"))
                    var uet = cursor.getString(cursor.getColumnIndex("uruneklenmet"))
                    var data = DataClass3_Expenses()
                    data.id = uid
                    data.urunAdi = uadi
                    data.urunFiyati = ufiyat * uadet
                    data.urunAdedi = uadet
                    data.urunFotografi = ufoto
                    data.urunSkt = uskt
                    data.urunUD = uet
                    filteredList.add(data)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        calendar.add(Calendar.DAY_OF_MONTH, -30)
        val last30Days = calendar.time
        for (data in filteredList) {
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(data.urunUD) ?: Date()
            if (date in last30Days..today) {
                addList.add(data)
            }
        }
        dataList = addList
        applyFilters()
        Log.e("TAG","çalıştı 1 ${dataList.size}")
    }

    private fun applyFilters() {
        val selectedAyar = spinnerAyar.selectedItem as? String
        when (selectedAyar) {
            "En Düşük Fiyata Göre Sırala" -> dataList.sortBy { it.urunFiyati }
            "En Yüksek Fiyata Göre Sırala" -> dataList.sortByDescending { it.urunFiyati }
            "En Yakın Tarihe Göre Sırala" -> dataList.sortBy { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.urunSkt) }
            "En Geç Tarihe Göre Sırala" -> dataList.sortByDescending { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.urunSkt) }
        }
        if (dataList.isNotEmpty()) {
            if (dataList.isEmpty()) {
                dataList.clear()
                text.text = "Herhangi bir harcama yok."
            } else {
                val toplamfiyat = dataList.sumByDouble { it.urunFiyati }
                text.text = "Toplam Harcama: $toplamfiyat TL"
            }
        } else {
            dataList.clear()
            text.text = "Herhangi bir harcama yok."
        }
        adapter = ExpensesAdapterClass(dataList, requireContext())
        listView.adapter = adapter
        adapter.notifyDataSetChanged()
        Log.e("TAG","çalıştı 2 ${dataList.size}")
    }
}
