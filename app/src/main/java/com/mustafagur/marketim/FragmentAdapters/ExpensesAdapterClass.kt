package com.mustafagur.marketim.FragmentAdapters

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mustafagur.marketim.DetailActivity
import com.mustafagur.marketim.R
import java.text.DecimalFormat

class ExpensesAdapterClass(private val list: ArrayList<DataClass3_Expenses>, private val context: Context) : BaseAdapter() {
    override fun getCount(): Int {
        return list.size
    }
    override fun getItem(position: Int): Any {
        return list[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.design_settingslist, parent, false)
        }
        val veri = list[position]
        val itemname: TextView = view!!.findViewById(R.id.design2_urunadi)
        val itemdetay: TextView = view.findViewById(R.id.design2_urundetay)
        val itemimg: ImageView = view.findViewById(R.id.design2_resim)
        if (veri.urunFotografi != null) {
            val bitmap = BitmapFactory.decodeByteArray(veri.urunFotografi, 0, veri.urunFotografi!!.size)
            itemimg.setImageBitmap(bitmap)
            itemimg.setBackgroundResource(R.drawable.image_background)
        } else {
            itemimg.setImageResource(R.drawable.logo)
            itemimg.setBackgroundResource(R.drawable.image_background)
        }
        itemname.setText(veri.urunAdi)
        itemdetay.setText("Harcanan Tutar: ${veri.urunFiyati} TL\nSatın Alım Tarihi: ${veri.urunUD}")
        view.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("urunid", veri.id)
            intent.putExtra("urunadi", veri.urunAdi)
            var urunfiyat = veri.urunFiyati / veri.urunAdedi
            intent.putExtra("urunfiyati", urunfiyat)
            intent.putExtra("urunadedi", veri.urunAdedi.toString())
            intent.putExtra("urunskt", veri.urunSkt)
            intent.putExtra("urunfotografi", veri.urunFotografi)
            context.startActivity(intent)
        }
        return view
    }
}