package com.mustafagur.marketim

import DatabaseHelper
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream

class ItemActivity : AppCompatActivity() {
    private lateinit var kayitUadi: EditText
    private lateinit var kayitUfiyat: EditText
    private lateinit var kayitUadet: EditText
    private lateinit var kayitUskt: EditText
    private lateinit var selectedImage: Bitmap

    private val PERMISSION_REQUEST_CODE = 1
    private val CAMERA_REQUEST_CODE = 2
    private val GALLERY_REQUEST_CODE = 3

    private fun initialize() {
        kayitUadi = findViewById(R.id.kayitUadi)
        kayitUfiyat = findViewById(R.id.kayitUfiyat)
        kayitUadet = findViewById(R.id.kayitUadet)
        kayitUskt = findViewById(R.id.kayitUskt)
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

        val db = DatabaseHelper(this)
        val imageByteArray = convertBitmapToByteArray(selectedImage)
        db.insertData(gelenUadi, gelenUfiyat, gelenUadet.toByte(), imageByteArray, gelenUskt, "")
        Toast.makeText(this, "Ürün başarıyla kaydedildi.", Toast.LENGTH_LONG).show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun fotografEkle(view: View) {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
        } else {
            showImageSourceDialog()
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf<CharSequence>("Fotoğraf Çek", "Galeriden Seç", "İptal")
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
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
                Toast.makeText(this, "Kamera izni reddedildi.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val image = data?.extras?.get("data") as Bitmap
                    selectedImage = image
                    Toast.makeText(this, "Fotoğraf başarıyla seçildi.", Toast.LENGTH_LONG).show()
                }
                GALLERY_REQUEST_CODE -> {
                    val selectedImageUri = data?.data
                    val imageStream = contentResolver.openInputStream(selectedImageUri!!)
                    val image = BitmapFactory.decodeStream(imageStream)
                    selectedImage = image
                    Toast.makeText(this, "Fotoğraf başarıyla seçildi.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

