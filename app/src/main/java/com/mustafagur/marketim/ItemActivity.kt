package com.mustafagur.marketim

import DatabaseHelper
import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ItemActivity : AppCompatActivity() {
    private lateinit var kayitUadi: EditText
    private lateinit var kayitUfiyat: EditText
    private lateinit var kayitUadet: EditText
    private lateinit var kayitUskt: EditText
    private lateinit var kayitUresim: ImageView
    private lateinit var selectedImage: Bitmap

    private val PERMISSION_REQUEST_CODE = 1
    private val CAMERA_REQUEST_CODE = 2
    private val GALLERY_REQUEST_CODE = 3

    private fun initialize() {
        kayitUadi = findViewById(R.id.kayitUadi)
        kayitUfiyat = findViewById(R.id.kayitUfiyat)
        kayitUadet = findViewById(R.id.kayitUadet)
        kayitUskt = findViewById(R.id.kayitUskt)
        kayitUresim = findViewById(R.id.kayitUlogo)
        selectedImage = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        initialize()
    }

    fun kaydetUrun(view: View) {
        val gelenUadi = kayitUadi.text.toString()
        val gelenUfiyat = kayitUfiyat.text.toString()
        val gelenUadet = kayitUadet.text.toString()
        val gelenUskt = kayitUskt.text.toString()

        if (gelenUadi.isEmpty() || gelenUfiyat.isEmpty() || gelenUadet.isEmpty() || gelenUskt.isEmpty()) {
            Toast.makeText(this, "Ürün adı, fiyatı, adedi veya skt boş olamaz.", Toast.LENGTH_LONG)
                .show()
            return
        }

        val forbiddenCharacters = listOf("@", "#", "$", "*", "%", ">", "<", "£")
        if (forbiddenCharacters.any { gelenUadi.contains(it) }) {
            Toast.makeText(this, "Ürün adı yasaklı karakterler içeriyor.", Toast.LENGTH_LONG).show()
            return
        }

        val control1 = gelenUfiyat.toIntOrNull()
        val control2 = gelenUadet.toIntOrNull()
        if (control1 == null || control2 == null) {
            Toast.makeText(
                this,
                "Lütfen ürün fiyatını veya ürün adedini sayısal bir değer olarak girin.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        var control3 = gelenUadet.toByteOrNull()
        if (control3 == null) {
            Toast.makeText(this@ItemActivity, "Bir ürünün en fazla 127 adedi tutulabilir.", Toast.LENGTH_SHORT).show()
            return
        }

        if(gelenUadi.length >= 20) {
            Toast.makeText(this@ItemActivity,"Ürün Adı 20 karakter veya daha fazla olamaz.",Toast.LENGTH_SHORT).show()
            return
        }

        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val bugununTarihi = format.format(Date())

        val db = DatabaseHelper(this)
        val imageByteArray = convertBitmapToByteArray(selectedImage)
        db.insertData(gelenUadi, gelenUfiyat, gelenUadet.toByte(), imageByteArray, gelenUskt, bugununTarihi)
        db.close()
        Toast.makeText(this, "Ürün başarıyla kaydedildi.", Toast.LENGTH_LONG).show()
        val bb = Intent(this@ItemActivity,MainActivity::class.java)
        startActivity(bb)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun fotografEkle(view: View) {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_CODE
            )
        } else {
            showImageSourceDialog()
        }
    }


    private fun showImageSourceDialog() {
        val options = arrayOf<CharSequence>("Fotoğraf Çek", "Galeriden Seç", "İptal")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Fotoğraf Kaynağını Seçin")
        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                "Fotoğraf Çek" -> takePhoto()
                "Galeriden Seç" -> selectPhotoFromGallery()
                "İptal" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private fun selectPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageSourceDialog()
            } else {
                Toast.makeText(this, "Depolama izni reddedildi.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val image = data?.extras?.get("data") as Bitmap
                    selectedImage = resizeImage(image)
                    kayitUresim.setImageBitmap(selectedImage)
                }
                GALLERY_REQUEST_CODE -> {
                    val selectedImageUri = data?.data
                    val imageStream = contentResolver.openInputStream(selectedImageUri!!)
                    val image = BitmapFactory.decodeStream(imageStream)
                    selectedImage = resizeImage(image)
                    kayitUresim.setImageBitmap(selectedImage)
                }
            }
        }
    }

    fun showDatePickerDialog(view: View) {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            kayitUskt.setText(formattedDate)
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

    fun resmiKaldir(view: View) {
        kayitUresim.setImageResource(R.drawable.logo)
    }
}
