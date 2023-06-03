package com.mustafagur.marketim

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mustafagur.marketim.FragmentAdapters.DataClass2

class SktAdapterClass(private val list: ArrayList<DataClass2>, private val context: Context) : BaseAdapter() {
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
            view = LayoutInflater.from(context).inflate(R.layout.design_itemslist, parent, false)
        }
        val veri = list[position]
        val itemname: TextView = view!!.findViewById(R.id.design_itemname)
        val itemadet: TextView = view.findViewById(R.id.design_itemcount)
        val itemskt: TextView = view.findViewById(R.id.design_itemskt)
        val itemimg: ImageView = view.findViewById(R.id.design_picture)
        if (veri.urunFotografi != null) {
            val bitmap = BitmapFactory.decodeByteArray(veri.urunFotografi, 0, veri.urunFotografi!!.size)
            itemimg.setImageBitmap(bitmap)
            itemimg.setBackgroundResource(R.drawable.image_background)
        } else {
            itemimg.setImageResource(R.drawable.logo)
            itemimg.setBackgroundResource(R.drawable.image_background)
        }
        if(veri.urunAdi.length > 11 ) {
            val textSizeInDp = 15f
            val textSizeInPx = (textSizeInDp * context.resources.displayMetrics.density + 0.5f).toInt()
            itemname.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInPx.toFloat())
        } else {
            val textSizeInDp = 20f
            val textSizeInPx = (textSizeInDp * context.resources.displayMetrics.density + 0.5f).toInt()
            itemname.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInPx.toFloat())
        }
        itemname.text = veri.urunAdi
        itemadet.text = "Kalan Gün\n"+veri.urunKalanGun
        itemadet.setTextColor(Color.RED)
        itemskt.text = "Son Kullanma Tarihi: " + veri.urunSkt
        itemskt.setTextColor(Color.BLACK)
        itemname.setTextColor(Color.BLACK)
        view.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("urunid", veri.id)
            intent.putExtra("urunadi", veri.urunAdi)
            intent.putExtra("urunfiyati", veri.urunFiyati)
            intent.putExtra("urunadedi", veri.urunAdedi.toString())
            intent.putExtra("urunskt", veri.urunSkt)
            intent.putExtra("urunfotografi", veri.urunFotografi)
            context.startActivity(intent)
        }
        return view
    }
}