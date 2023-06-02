package com.mustafagur.marketim.FragmentAdapters

import DatabaseHelper
import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.mustafagur.marketim.DataClass
import com.mustafagur.marketim.ItemActivity
import com.mustafagur.marketim.ItemsAdapterClass
import com.mustafagur.marketim.R

class ItemsFragmentAdapter : Fragment() {
    private lateinit var btnFragmentItems: Button
    private lateinit var listView: ListView
    private lateinit var itemList: ArrayList<DataClass>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_items, container, false)
        btnFragmentItems = view.findViewById(R.id.btnFragmentItems)
        listView = view.findViewById(R.id.ItemsList)
        itemList = ArrayList<DataClass>()
        btnFragmentItems.setOnClickListener {
            urunEkle(it)
        }
        val dbHelper = DatabaseHelper(requireContext())
        val cursor = dbHelper.getAllData()
        updateList(cursor)
        dbHelper.close()
        val search: EditText = view.findViewById(R.id.searchTxt)
        search.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    val searchText = search.text.toString()
                    val dbHelper = DatabaseHelper(requireContext())
                    val cursor = dbHelper.searchData(searchText)
                    updateList(cursor)
                    dbHelper.close()
                    true
                }
                else -> false
            }
        }

        return view
    }

    @SuppressLint("Range")
    fun updateList(cursor: Cursor?) {
        itemList.clear()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val urunid = cursor.getInt(cursor.getColumnIndex("id"))
                val urunadi = cursor.getString(cursor.getColumnIndex("urunadi"))
                val urunfiyat = cursor.getString(cursor.getColumnIndex("urunfiyati"))
                val urunadet = cursor.getInt(cursor.getColumnIndex("urunadedi"))
                val urunimg = cursor.getBlob(cursor.getColumnIndex("urunfotografi"))
                val urunskt = cursor.getString(cursor.getColumnIndex("urunskt"))
                val data = DataClass()
                data.id = urunid
                data.urunAdi = urunadi
                data.urunFiyati = urunfiyat.toDouble()
                data.urunAdedi = urunadet
                data.urunFotografi = urunimg
                data.urunSkt = urunskt
                itemList.add(data)
            } while (cursor.moveToNext())
            cursor.close()
        }
        val adapterclass = ItemsAdapterClass(itemList, requireContext())
        adapterclass.notifyDataSetChanged()
        listView.adapter = adapterclass
    }

    public fun ItemSearch(view: View, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_NULL) {
            val editTextSearch = view as EditText
            val searchText = editTextSearch.text.toString()

            return true
        }
        return false
    }

    private fun urunEkle(view: View) {
        val intent = Intent(requireContext(), ItemActivity::class.java)
        startActivity(intent)
    }
}

