package com.mustafagur.marketim

import DatabaseHelper
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
        if (veri.urunKalanGun.toInt() > 0) {
            itemadet.text = "Kalan Gün\n"+veri.urunKalanGun
            itemadet.setTextColor(Color.rgb(255,64,129))
            itemname.setTextColor(Color.BLACK)
            itemskt.setTextColor(Color.BLACK)
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
        } else {
            itemadet.text = "SKT 'si Geçti"
            itemadet.setTextColor(Color.RED)
            itemname.setTextColor(Color.RED)
            itemskt.setTextColor(Color.RED)
            view.setOnClickListener {
                val dialogView = LayoutInflater.from(context).inflate(R.layout.sure_dialog, null)
                val builder = AlertDialog.Builder(context).setView(dialogView).setCancelable(true)
                val dialog = builder.create()
                val buttonOk: Button = dialogView.findViewById(R.id.button_sil)
                buttonOk.setOnClickListener {
                    val db = DatabaseHelper(context)
                    db.deleteData(veri.id)
                    Toast.makeText(context,"Başarıyla ürün silindi.",Toast.LENGTH_SHORT).show()
                    var intent = Intent(context,MainActivity::class.java)
                    context.startActivity(intent)
                }
                val buttonSilme: Button = dialogView.findViewById(R.id.button_silme)
                buttonSilme.setOnClickListener {
                    dialog.dismiss()
                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra("urunid", veri.id)
                    intent.putExtra("urunadi", veri.urunAdi)
                    intent.putExtra("urunfiyati", veri.urunFiyati)
                    intent.putExtra("urunadedi", veri.urunAdedi.toString())
                    intent.putExtra("urunskt", veri.urunSkt)
                    intent.putExtra("urunfotografi", veri.urunFotografi)
                    context.startActivity(intent)
                }
                dialog.setCanceledOnTouchOutside(true)
                dialog.show()
            }
        }
        itemskt.text = "Son Kullanma Tarihi: " + veri.urunSkt
        return view
    }
}