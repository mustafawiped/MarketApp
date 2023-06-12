package com.mustafagur.marketim

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.mustafagur.marketim.FragmentAdapters.MainFragmentAdapter
import java.text.SimpleDateFormat
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var OtomatikSilmeSw: Switch
    private lateinit var TumUrunBildirimSw: Switch
    private lateinit var YiliGecmisVeriSw: Switch
    private lateinit var GunlukBildirimSaatSw: Switch
    private lateinit var GunlukBilgiMesajSw: Switch

    private lateinit var OtomatikSilmeTv1: TextView
    private lateinit var TumUrunBildirimTv1: TextView
    private lateinit var YiliGecmisVeriTv1: TextView
    private lateinit var GunlukBildirimSaatTv1: TextView
    private lateinit var GunlukBilgiMesajTv1: TextView

    private fun init() {
        OtomatikSilmeSw = findViewById(R.id.otomatikSilmeSw)
        TumUrunBildirimSw = findViewById(R.id.tumurunBildirimSw)
        YiliGecmisVeriSw = findViewById(R.id.YiliGecmisVeriSilSw)
        GunlukBildirimSaatSw = findViewById(R.id.GunlukBildirimSaatSw)
        GunlukBilgiMesajSw = findViewById(R.id.BilgiMesajiSw)
        OtomatikSilmeTv1 = findViewById(R.id.otomatikSilmeTv1)
        TumUrunBildirimTv1 = findViewById(R.id.tumurunBildirimTv1)
        YiliGecmisVeriTv1 = findViewById(R.id.YiliGecmisVeriSilTv1)
        GunlukBildirimSaatTv1 = findViewById(R.id.GunlukBildirimSaatTv1)
        GunlukBilgiMesajTv1 = findViewById(R.id.BilgiMesajiTv1)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        init()

        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val control1 = sharedPreferences.getBoolean("otomatiksilme", false)
        val control2 = sharedPreferences.getBoolean("tumurunlerbildirim", false)
        val control3 = sharedPreferences.getBoolean("oncekiyilotomatiksil", false)
        val control4 = sharedPreferences.getBoolean("gunlukbildirimdurum", false)
        val control5 = sharedPreferences.getBoolean("gunlukbilgi", true)
        if(control1)
            OtomatikSilmeSw.isChecked = true
        if (control2)
            TumUrunBildirimSw.isChecked = true
        if (control3)
            YiliGecmisVeriSw.isChecked = true
        if (control4) {
            val alarm = sharedPreferences.getString("gunlukbildirimsaat","")
            val textv = findViewById<TextView>(R.id.GunlukBildirimSaatTv2)
            textv.text = "Bildirimler her gün saat $alarm 'da atılıyor. Eğer kapatırsan 21:30 'da atılacak."
            GunlukBildirimSaatSw.isChecked = true
        }
        if (control5)
            GunlukBilgiMesajSw.isChecked = true

        OtomatikSilmeSw.setOnClickListener { view ->
            if (OtomatikSilmeSw.isChecked) {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.sure_dialog, null)
                val builder = AlertDialog.Builder(this).setView(dialogView)
                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.setCancelable(false)
                val baslik: TextView = dialogView.findViewById(R.id.text_title)
                val icerik: TextView = dialogView.findViewById(R.id.text_content)
                baslik.setText("Marketim | Uyarı ")
                icerik.setText("Bu ayarı açarsanız, son kullanma tarihi geçen ürünler sorulmadan otomatik silinir.")
                val buttonOk: Button = dialogView.findViewById(R.id.button_sil)
                val buttonNo: Button = dialogView.findViewById(R.id.button_silme)
                buttonOk.setText("Ayarı Aç")
                buttonNo.setText("Vazgeç")
                buttonOk.setOnClickListener {
                    val sharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("otomatiksilme", true)
                    editor.apply()
                    OtomatikSilmeSw.isChecked = true
                    Toast.makeText(this, "Ayar başarıyla açıldı!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                buttonNo.setOnClickListener {
                    OtomatikSilmeSw.isChecked = false
                    dialog.dismiss()
                }
                dialog.show()
            } else {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.sure_dialog, null)
                val builder = AlertDialog.Builder(this).setView(dialogView)
                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.setCancelable(false)
                val baslik: TextView = dialogView.findViewById(R.id.text_title)
                val icerik: TextView = dialogView.findViewById(R.id.text_content)
                baslik.setText("Marketim | Uyarı ")
                icerik.setText("Bu ayarı kapatırsanız, son kullanma tarihi geçen ürünler otomatik silinmez.")
                val buttonOk: Button = dialogView.findViewById(R.id.button_sil)
                val buttonNo: Button = dialogView.findViewById(R.id.button_silme)
                buttonOk.setText("Ayarı Kapat")
                buttonNo.setText("Vazgeç")
                buttonOk.setOnClickListener {
                    val sharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("otomatiksilme", false)
                    editor.apply()
                    OtomatikSilmeSw.isChecked = false
                    Toast.makeText(this, "Ayar başarıyla kapatıldı!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                buttonNo.setOnClickListener {
                    dialog.dismiss()
                    OtomatikSilmeSw.isChecked = true
                }
                dialog.show()
            }
        }
        TumUrunBildirimSw.setOnClickListener { view ->
            if (TumUrunBildirimSw.isChecked) {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.sure_dialog, null)
                val builder = AlertDialog.Builder(this).setView(dialogView)
                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.setCancelable(false)
                val baslik: TextView = dialogView.findViewById(R.id.text_title)
                val icerik: TextView = dialogView.findViewById(R.id.text_content)
                baslik.setText("Marketim | Uyarı ")
                icerik.setText("Bu ayarı açarsanız, 5 gün veya daha az süresi kalan tüm ürünlerin bildirimi tek tek gönderilir.")
                val buttonOk: Button = dialogView.findViewById(R.id.button_sil)
                val buttonNo: Button = dialogView.findViewById(R.id.button_silme)
                buttonOk.setText("Ayarı Aç")
                buttonNo.setText("Vazgeç")
                buttonOk.setOnClickListener {
                    val sharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("tumurunlerbildirim", true)
                    editor.apply()
                    TumUrunBildirimSw.isChecked = true
                    Toast.makeText(this, "Ayar başarıyla açıldı!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                buttonNo.setOnClickListener {
                    TumUrunBildirimSw.isChecked = false
                    dialog.dismiss()
                }
                dialog.show()
            } else {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.sure_dialog, null)
                val builder = AlertDialog.Builder(this).setView(dialogView)
                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.setCancelable(false)
                val baslik: TextView = dialogView.findViewById(R.id.text_title)
                val icerik: TextView = dialogView.findViewById(R.id.text_content)
                baslik.setText("Marketim | Uyarı ")
                icerik.setText("Bu ayarı kapatırsanız, sadece hatırlatma amacıyla 1 tane bildirim gönderilir.")
                val buttonOk: Button = dialogView.findViewById(R.id.button_sil)
                val buttonNo: Button = dialogView.findViewById(R.id.button_silme)
                buttonOk.setText("Ayarı Kapat")
                buttonNo.setText("Vazgeç")
                buttonOk.setOnClickListener {
                    val sharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("tumurunlerbildirim", false)
                    editor.apply()
                    TumUrunBildirimSw.isChecked = false
                    Toast.makeText(this, "Ayar başarıyla kapatıldı!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                buttonNo.setOnClickListener {
                    TumUrunBildirimSw.isChecked = true
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
        YiliGecmisVeriSw.setOnClickListener { view ->
            if (YiliGecmisVeriSw.isChecked) {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.sure_dialog, null)
                val builder = AlertDialog.Builder(this).setView(dialogView)
                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.setCancelable(false)
                val baslik: TextView = dialogView.findViewById(R.id.text_title)
                val icerik: TextView = dialogView.findViewById(R.id.text_content)
                baslik.setText("Marketim | Uyarı ")
                icerik.setText("Bu ayarı açarsanız, Bir önceki yıl kaydedilen ve skt 'si geçmiş tüm ürünler otomatik silinir.")
                val buttonOk: Button = dialogView.findViewById(R.id.button_sil)
                val buttonNo: Button = dialogView.findViewById(R.id.button_silme)
                buttonOk.setText("Ayarı Aç")
                buttonNo.setText("Vazgeç")
                buttonOk.setOnClickListener {
                    val sharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("oncekiyilotomatiksil", true)
                    editor.apply()
                    YiliGecmisVeriSw.isChecked = true
                    Toast.makeText(this, "Ayar başarıyla açıldı!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                buttonNo.setOnClickListener {
                    YiliGecmisVeriSw.isChecked = false
                    dialog.dismiss()
                }
                dialog.show()
            } else {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.sure_dialog, null)
                val builder = AlertDialog.Builder(this).setView(dialogView)
                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.setCancelable(false)
                val baslik: TextView = dialogView.findViewById(R.id.text_title)
                val icerik: TextView = dialogView.findViewById(R.id.text_content)
                baslik.setText("Marketim | Uyarı ")
                icerik.setText("Bu ayarı kapatırsanız, önceki yıl kaydettiğiniz skt 'si geçmiş ürünler otomatik silinmez.")
                val buttonOk: Button = dialogView.findViewById(R.id.button_sil)
                val buttonNo: Button = dialogView.findViewById(R.id.button_silme)
                buttonOk.setText("Ayarı Kapat")
                buttonNo.setText("Vazgeç")
                buttonOk.setOnClickListener {
                    val sharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("oncekiyilotomatiksil", false)
                    editor.apply()
                    YiliGecmisVeriSw.isChecked = false
                    Toast.makeText(this, "Ayar başarıyla kapatıldı!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                buttonNo.setOnClickListener {
                    YiliGecmisVeriSw.isChecked = true
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
        GunlukBildirimSaatSw.setOnClickListener { view ->
            if (GunlukBildirimSaatSw.isChecked) {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.settings_dialog, null)
                val builder = AlertDialog.Builder(this).setView(dialogView)
                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.setCancelable(false)
                val edittext: EditText = dialogView.findViewById(R.id.edittext_saat)
                val buttonOk: Button = dialogView.findViewById(R.id.button_kaydet)
                val buttonNo: Button = dialogView.findViewById(R.id.button_iptal)
                edittext.setOnClickListener {
                    saatsec(edittext)
                }
                buttonOk.setOnClickListener {
                    if (edittext.text.isEmpty()) {
                        Toast.makeText(this, "Lütfen saati giriniz.", Toast.LENGTH_SHORT).show()
                        GunlukBildirimSaatSw.isChecked = false
                    } else {
                        val sharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("gunlukbildirimdurum", true)
                        editor.putString("gunlukbildirimsaat",edittext.text.toString())
                        editor.apply()
                        GunlukBildirimSaatSw.isChecked = true
                        val textv = findViewById<TextView>(R.id.GunlukBildirimSaatTv2)
                        textv.text = "Bildirimler her gün saat ${edittext.text} 'da atılıyor. Eğer kapatırsan 21:30 'da atılacak."
                        Toast.makeText(this, "Ayar başarıyla açıldı!", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                buttonNo.setOnClickListener {
                    GunlukBildirimSaatSw.isChecked = false
                    dialog.dismiss()
                }
                dialog.show()
            } else {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.sure_dialog, null)
                val builder = AlertDialog.Builder(this).setView(dialogView)
                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.setCancelable(false)
                val baslik: TextView = dialogView.findViewById(R.id.text_title)
                val icerik: TextView = dialogView.findViewById(R.id.text_content)
                baslik.setText("Marketim | Uyarı ")
                icerik.setText("Bu ayarı kapatırsanız, önceki yıl kaydettiğiniz ürünler otomatik silinmez.")
                val buttonOk: Button = dialogView.findViewById(R.id.button_sil)
                val buttonNo: Button = dialogView.findViewById(R.id.button_silme)
                buttonOk.setText("Ayarı Kapat")
                buttonNo.setText("Vazgeç")
                buttonOk.setOnClickListener {
                    val sharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("gunlukbildirimdurum", false)
                    editor.apply()
                    GunlukBildirimSaatSw.isChecked = false
                    val textv = findViewById<TextView>(R.id.GunlukBildirimSaatTv2)
                    textv.text = "Ayar açıksa, belirlediğiniz saatte her gün bildirim gönderir. Ayar kapalıysa her gün 21:30 'da gönderir."
                    Toast.makeText(this, "Ayar başarıyla kapatıldı!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                buttonNo.setOnClickListener {
                    GunlukBildirimSaatSw.isChecked = true
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
        GunlukBilgiMesajSw.setOnClickListener { view ->
            if (GunlukBilgiMesajSw.isChecked) {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.sure_dialog, null)
                val builder = AlertDialog.Builder(this).setView(dialogView)
                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.setCancelable(false)
                val baslik: TextView = dialogView.findViewById(R.id.text_title)
                val icerik: TextView = dialogView.findViewById(R.id.text_content)
                baslik.setText("Marketim | Uyarı ")
                icerik.setText("Bu ayarı açarsanız, Anasayfa her yenilendiğinde yeni bir bilgi ile karşılaşacaksınız.")
                val buttonOk: Button = dialogView.findViewById(R.id.button_sil)
                val buttonNo: Button = dialogView.findViewById(R.id.button_silme)
                buttonOk.setText("Ayarı Aç")
                buttonNo.setText("Vazgeç")
                buttonOk.setOnClickListener {
                    val sharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("gunlukbilgi", true)
                    editor.apply()
                    GunlukBilgiMesajSw.isChecked = true
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Ayar başarıyla açıldı!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    finish()
                }
                buttonNo.setOnClickListener {
                    GunlukBilgiMesajSw.isChecked = false
                    dialog.dismiss()
                }
                dialog.show()
            } else {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.sure_dialog, null)
                val builder = AlertDialog.Builder(this).setView(dialogView)
                val dialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.setCancelable(false)
                val baslik: TextView = dialogView.findViewById(R.id.text_title)
                val icerik: TextView = dialogView.findViewById(R.id.text_content)
                baslik.setText("Marketim | Uyarı ")
                icerik.setText("Bu ayarı kapatırsanız, anasayfada günlük bilgilendirmeler göremeyeceksiniz.")
                val buttonOk: Button = dialogView.findViewById(R.id.button_sil)
                val buttonNo: Button = dialogView.findViewById(R.id.button_silme)
                buttonOk.setText("Ayarı Kapat")
                buttonNo.setText("Vazgeç")
                buttonOk.setOnClickListener {
                    val sharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("gunlukbilgi", false)
                    editor.apply()
                    GunlukBilgiMesajSw.isChecked = false
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Ayar başarıyla kapatıldı!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    finish()
                }
                buttonNo.setOnClickListener {
                    GunlukBilgiMesajSw.isChecked = true
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
    }

    fun backToMain(view: View) {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun saatsec(edittexts: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            this,
            R.style.CustomTimePickerDialog, // Özel stil kaynağı
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                val selectedTime = formatTime(selectedHour, selectedMinute)
                edittexts.setText(selectedTime)
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }


    private fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun sifirla(view: View) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.sure_dialog, null)
        val builder = AlertDialog.Builder(this).setView(dialogView)
        val dialog = builder.create()
        val baslik: TextView = dialogView.findViewById(R.id.text_title)
        val icerik: TextView = dialogView.findViewById(R.id.text_content)
        baslik.setText("Marketim | Ayar Sıfırlama ")
        icerik.setText("Ayarları varsayılana sıfırlamak istediğine emin misin?")
        val buttonOk: Button = dialogView.findViewById(R.id.button_sil)
        val buttonNo: Button = dialogView.findViewById(R.id.button_silme)
        buttonOk.setText("Sıfırla")
        buttonNo.setText("Vazgeç")
        buttonOk.setOnClickListener {
            val sharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("otomatiksilme",false)
            editor.putBoolean("tumurunlerbildirim",false)
            editor.putBoolean("oncekiyilotomatiksil",false)
            editor.putBoolean("gunlukbildirimdurum",false)
            editor.putBoolean("gunlukbilgi", true)
            editor.apply()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Ayarlar başarıyla sıfırlandı!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            finish()

        }
        buttonNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}