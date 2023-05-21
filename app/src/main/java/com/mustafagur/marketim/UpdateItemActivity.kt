package com.mustafagur.marketim

import DatabaseHelper
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.mustafagur.marketim.R
import java.io.ByteArrayOutputStream
import java.util.*

class UpdateItemActivity : AppCompatActivity() {

    private lateinit var urunAdi: EditText
    private lateinit var urunFiyat: EditText
    private lateinit var urunAdet: EditText
    private lateinit var urunSKT: EditText
    private lateinit var urunImg: ImageView

    var urunid = 0
    var urunadi = ""
    var urunfiyati = 0.0
    var urunadedi = 0
    var urunskt = ""
    var urunimg: ByteArray? = null

    private fun init() {
        urunAdi = findViewById(R.id.updateUrunAdi)
        urunFiyat = findViewById(R.id.updateUrunFiyati)
        urunAdet = findViewById(R.id.updateUrunAdedi)
        urunSKT = findViewById(R.id.updateUrunSKT)
        urunImg = findViewById(R.id.updateLogo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_item)
        init()
        urunid = intent.getIntExtra("urunid", 0)
        urunadi = intent.getStringExtra("urunadi").toString()
        urunfiyati = intent.getDoubleExtra("urunfiyati", 0.0)
        urunadedi = intent.getByteExtra("urunadedi", 0).toInt()
        urunskt = intent.getStringExtra("urunskt").toString()
        urunimg = intent.getByteArrayExtra("urunfotografi")
        urunAdi.setText(urunadi)
        urunFiyat.setText(urunfiyati.toString())
        urunAdet.setText(urunadedi.toString())
        urunSKT.setText(urunskt)
        val bitmap = urunimg?.let { BitmapFactory.decodeByteArray(urunimg, 0, it.size) }
        if (bitmap != null) {
            urunImg.setImageBitmap(bitmap)
        } else {
            urunImg.setImageResource(R.drawable.logo)
        }
    }

    private val cameraActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                urunImg.setImageBitmap(imageBitmap)
            }
        }

    private val galleryActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImage = result.data?.data
                val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                urunImg.setImageBitmap(imageBitmap)
            }
        }

    fun resmiDegistir(view: View) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Resim mi çekmek istersiniz yoksa galeriden fotoğraf mı seçmek istersiniz?")
            .setCancelable(true)
            .setPositiveButton("Kamera") { dialog, id ->
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraActivityResult.launch(takePictureIntent)
                dialog.dismiss()
            }
            .setNegativeButton("Galeri") { dialog, id ->
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryActivityResult.launch(intent)
                dialog.dismiss()
            }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun resmiKaldir(view: View) {
        urunImg.setImageResource(R.drawable.logo)
    }

    fun kaydetUrun(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Marketim | Bildiri")
        builder.setMessage(""+urunadi+" isimli ürünü güncellemek istediğine emin misin?")
        builder.setPositiveButton("Evet") { dialog, which ->
            val dbHelper = DatabaseHelper(this)
            val product = urunAdi.text.toString()
            val price = urunFiyat.text.toString()
            val amount = urunAdet.text.toString().toByte()
            val exd = urunSKT.text.toString()
            val note = ""
            val drawable = urunImg.drawable
            val bitmap = (drawable as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val imageByteArray = stream.toByteArray()
            val result = dbHelper.updateData(urunid.toLong(), product, price, amount, imageByteArray, exd, note)
            if (result != -1)
                Toast.makeText(this,"Başarıyla $urunadi isimli ürün güncellendi.",Toast.LENGTH_LONG).show()
            else
                Toast.makeText(this,"Bir hata oluştu, lütfen Geliştirici Ekibine bildirin.",Toast.LENGTH_LONG).show()
            val go = Intent(this@UpdateItemActivity, MainActivity::class.java)
            startActivity(go)
        }
        builder.setNegativeButton("Hayır") { dialog, which -> }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
    fun showDatePickerDialog(view: View) {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            urunSKT.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }
}