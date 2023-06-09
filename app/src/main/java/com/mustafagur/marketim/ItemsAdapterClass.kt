package com.mustafagur.marketim

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mustafagur.marketim.FragmentAdapters.DataClass2
import kotlin.collections.ArrayList

class ItemsAdapterClass(val list: ArrayList<DataClass2>, private val context: Context) : BaseAdapter() {

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
        itemname.text = veri.urunAdi
        itemadet.text = "Adet\n"+veri.urunAdedi.toString()
        itemskt.text = "Skt: " + veri.urunSkt
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
