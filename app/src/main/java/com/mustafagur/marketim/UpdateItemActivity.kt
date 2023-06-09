package com.mustafagur.marketim

import DatabaseHelper
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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
    var urunadedi = ""
    var urunskt = ""
    var urunimg: ByteArray? = null

    private fun init() {
        urunAdi = findViewById(R.id.kayitUadi)
        urunFiyat = findViewById(R.id.kayitUfiyat)
        urunAdet = findViewById(R.id.kayitUadet)
        urunSKT = findViewById(R.id.kayitUskt)
        urunImg = findViewById(R.id.kayitUlogo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_item)
        init()
        urunid = intent.getIntExtra("urunid", 0)
        urunadi = intent.getStringExtra("urunadi").toString()
        urunfiyati = intent.getDoubleExtra("urunfiyati", 0.0)
        urunadedi = intent.getStringExtra("urunadedi").toString()
        urunskt = intent.getStringExtra("urunskt").toString()
        urunimg = intent.getByteArrayExtra("urunfotografi")
        urunAdi.setText(urunadi)
        urunFiyat.setText(urunfiyati.toString())
        urunAdet.setText(urunadedi)
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
        val dialogView = LayoutInflater.from(this).inflate(R.layout.sure_dialog, null)
        val builder = AlertDialog.Builder(this).setView(dialogView).setCancelable(true)
        val dialog = builder.create()
        val baslik: TextView = dialogView.findViewById(R.id.text_title)
        val icerik: TextView = dialogView.findViewById(R.id.text_content)
        baslik.setText("Marketim | Güncelleme Uyarı")
        icerik.setText("Bu ürünü güncellemek istediğinize emin misiniz? Bunun bir geri dönüşü olmayacak.")
        val buttonOk: Button = dialogView.findViewById(R.id.button_sil)
        val buttonNo: Button = dialogView.findViewById(R.id.button_silme)
        buttonOk.setText("Evet, eminim")
        buttonNo.setText("Hayır, bekle")
        buttonOk.setOnClickListener {
            if (urunid == 0) {
                Toast.makeText(this, "Hata oluştu. Hata Kodu: ERR2 | Lütfen Geliştirici Ekibine bildirin.", Toast.LENGTH_LONG).show()
            } else {
                val dbHelper = DatabaseHelper(this)
                val product = urunAdi.text.toString()
                val price = urunFiyat.text.toString()
                val amount = urunAdet.text.toString().toByteOrNull()
                val exd = urunSKT.text.toString()

                if (product.isEmpty() || price.isEmpty() || amount.toString().isEmpty() || exd.isEmpty()) {
                    Toast.makeText(this@UpdateItemActivity, "Ürün adı, fiyatı, adedi veya skt 'sı boş olamaz.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (amount == null) {
                    Toast.makeText(this@UpdateItemActivity, "Ürün adedi en fazla 127 olabilir. Lütfen daha az adet miktarı girin.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if(product.length >= 20) {
                    Toast.makeText(this@UpdateItemActivity,"Ürün Adı 20 karakter veya daha fazla olamaz.",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val control1 = price.toDoubleOrNull()
                if(control1 == null) {
                    Toast.makeText(this@UpdateItemActivity,"Ürün Fiyatı ondalıklı veya sayısal bir veri olmalı.",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val drawable = urunImg.drawable
                val bitmap = (drawable as BitmapDrawable).bitmap
                val resizedBitmap = resizeImage(bitmap)
                val stream = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val imageByteArray = stream.toByteArray()
                val result = dbHelper.updateData(urunid.toLong(), product, price, amount, imageByteArray, exd)
                if (result != -1)
                    Toast.makeText(this, "Başarıyla $urunadi isimli ürün güncellendi.", Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(this, "Bir hata oluştu, lütfen Geliştirici Ekibine bildirin.", Toast.LENGTH_LONG).show()
                finish()
                
                val main = MainActivity()
                main.finish()
                val go = Intent(this@UpdateItemActivity, MainActivity::class.java)
                startActivity(go)
                dialog.dismiss()
                dbHelper.close()
            }
        }
        buttonNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }



    fun showDatePickerDialog(view: View) {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,R.style.CustomTimePickerDialog, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            urunSKT.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun resizeImage(image: Bitmap): Bitmap {
        val maxSize = 256
        var width = image.width
        var height = image.height
        val scale: Float = when {
            width > height -> maxSize.toFloat() / width.toFloat()
            height > width -> maxSize.toFloat() / height.toFloat()
            else -> maxSize.toFloat() / width.toFloat()
        }
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        return Bitmap.createBitmap(image, 0, 0, width, height, matrix, true)
    }
    fun backToMain(view: View) {
        finish()
    }
}