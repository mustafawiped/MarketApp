package com.mustafagur.marketim

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

class FeedbackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun feedbackSend(view: View) {
        val baslik: EditText = findViewById(R.id.fbBaslik)
        val icerik: EditText = findViewById(R.id.fbAciklama)
        val today = LocalDate.now()
        val dayOfWeek = today.dayOfWeek.toString()
        val sharedPreferences = getSharedPreferences("feedbacks", Context.MODE_PRIVATE)
        val control = sharedPreferences.getString("feedback", "")
        if (baslik.text.toString().isEmpty() || icerik.text.toString().isEmpty()) Toast.makeText(this,"Lütfen başlığı veya içerik kısmını boş bırakmayın.",Toast.LENGTH_SHORT).show()
        else if (baslik.text.toString().length < 5 || baslik.text.toString().length > 25) Toast.makeText(this,"Başlık minimum 5 karakter maksimum 15 karakter olabilir", Toast.LENGTH_SHORT).show()
        else if (icerik.text.toString().length < 10 || icerik.text.toString().length > 250) Toast.makeText(this,"Açıklama minimum 10 karakter maksimum 250 karakter olabilir", Toast.LENGTH_SHORT).show()
        else if (control != null) {
            if(control.isEmpty() || !control.toString().equals(dayOfWeek)) {
                if (internetVarmi(this)) {
                    val dialogView = LayoutInflater.from(this).inflate(R.layout.sure_dialog, null)
                    val builder = AlertDialog.Builder(this).setView(dialogView)
                    val dialog = builder.create()
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.setCancelable(false)
                    val baslik2: TextView = dialogView.findViewById(R.id.text_title)
                    val icerik2: TextView = dialogView.findViewById(R.id.text_content)
                    baslik2.setText("Marketim | Geri Bildirim Gönder ")
                    icerik2.setText("Geri Bildirim göndermek istediğine emin misin? Eğer herhangi bir kural ihlali yaptıysan bir daha gönderemeyebilirsin.")
                    val buttonOk: Button = dialogView.findViewById(R.id.button_sil)
                    val buttonNo: Button = dialogView.findViewById(R.id.button_silme)
                    buttonOk.setText("Gönder")
                    buttonNo.setText("Vazgeç")
                    buttonOk.setOnClickListener {
                        val database = FirebaseDatabase.getInstance().reference
                        val usersRef = database.child("users")
                        val newChildRef = usersRef.push()
                        val dataMap = hashMapOf<String, Any>("Başlık" to baslik.text.toString(),"İçerik" to icerik.text.toString())
                        newChildRef.setValue(dataMap)
                        val editor = sharedPreferences.edit()
                        editor.putString("feedback", dayOfWeek)
                        editor.apply()
                        baslik.setText("")
                        icerik.setText("")
                        Toast.makeText(this,"Geri Bildirim başarıyla gönderildi.",Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }

                    buttonNo.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                } else {
                    Toast.makeText(this,"Lütfen internet bağlantınızı kontrol edin.",Toast.LENGTH_SHORT).show()
                }
            } else Toast.makeText(this,"Bugün zaten geri bildirim gönderdiniz. Yarın tekrar deneyin.",Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this,"Bir hata oluştu lütfen tekrar deneyin.",Toast.LENGTH_SHORT).show()
        }
    }

    fun internetVarmi(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun backToMain(view: View) {
        finish()
    }

    fun mailGonder(view: View) {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:contact@marketim.com")
        startActivity(emailIntent)
    }
}