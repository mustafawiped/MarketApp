package com.mustafagur.marketim

import DatabaseHelper
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {
    var urunid = 0
    var urunadi = ""
    var urunfiyati = 0.0
    var urunadedi = ""
    var urunskt = ""
    var urunimg: ByteArray? = null
    private lateinit var detayUadi: TextView
    private lateinit var detayUfiyat: TextView
    private lateinit var detayUadet: TextView
    private lateinit var detayUskt: TextView
    private lateinit var detayUkz: TextView
    private lateinit var detayUimg: ImageView

    private fun init() {
        detayUadi = findViewById(R.id.detailUrunAdi)
        detayUfiyat = findViewById(R.id.detailUrunFiyati)
        detayUadet = findViewById(R.id.detailUrunAdedi)
        detayUskt = findViewById(R.id.detailUrunSKT)
        detayUkz = findViewById(R.id.detailUrunKalanZ)
        detayUimg = findViewById(R.id.detailLogo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        init()
        urunid = intent.getIntExtra("urunid", 0)
        urunadi = intent.getStringExtra("urunadi").toString()
        urunfiyati = intent.getDoubleExtra("urunfiyati", 0.0)
        urunadedi = intent.getStringExtra("urunadedi").toString()
        urunskt = intent.getStringExtra("urunskt").toString()
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val expiryDate = dateFormat.parse(urunskt)
        var daysRemaining = ((expiryDate.time - currentDate.time) / (24L * 60L * 60L * 1000L)).toString()
        urunimg = intent.getByteArrayExtra("urunfotografi")
        detayUadi.setText("Ürün Adı: $urunadi")
        detayUfiyat.setText("Ürün Fiyatı: ${urunfiyati.toString()}")
        detayUadet.setText("Ürün Adedi: "+ urunadedi.toString())
        detayUkz.setText("Kullanılabilirlik Süresi: $daysRemaining Gün")
        detayUskt.setText("Ürün SKT: $urunskt")
        val bitmap = urunimg?.let { BitmapFactory.decodeByteArray(urunimg, 0, it.size) }
        if (bitmap != null) {
            Log.e("w","geliyo 3")
            detayUimg.setImageBitmap(bitmap)
        } else { detayUimg.setImageResource(R.drawable.logo)
            Log.e("sa","geliyo 4")
        }
    }

    fun urunuSil(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Marketim | Uyarı")
        builder.setMessage(""+urunadi+" isimli ürünü silmek istediğine emin misin?")
        builder.setPositiveButton("Evet") { dialog, which ->
            val db = DatabaseHelper(this)
            db.deleteData(urunid.toLong())
            db.close()
            Toast.makeText(this,"Başarıyla Ürün Silindi!",Toast.LENGTH_LONG).show()
            val go = Intent(this@DetailActivity, MainActivity::class.java)
            startActivity(go)
        }
        builder.setNegativeButton("Hayır") { dialog, which -> }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    fun urunuGuncelle(view: View) {
        val intent = Intent(this, UpdateItemActivity::class.java)
        intent.putExtra("urunid", urunid)
        intent.putExtra("urunadi", urunadi)
        intent.putExtra("urunfiyati", urunfiyati)
        intent.putExtra("urunadedi", urunadedi)
        intent.putExtra("urunskt", urunskt)
        intent.putExtra("urunfotografi", urunimg)
        startActivity(intent)
    }

    fun backToMain(view: View) {
        finish()
    }
}