package com.mustafagur.marketim.FragmentAdapters

import DatabaseHelper
import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mustafagur.marketim.NotificationsClass
import com.mustafagur.marketim.R
import com.mustafagur.marketim.SktAdapterClass
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainFragmentAdapter : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var list: ArrayList<DataClass2>
    private lateinit var listView: ListView
    private lateinit var gunlukBilgi: TextView

    @SuppressLint("MissingInflatedId", "Range")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val greeting = when (currentHour) {
            in 6..11 -> "Günaydınlar!"
            in 12..17 -> "Tünaydın!"
            in 18..20 -> "İyi akşamlar!"
            else -> "İyi geceler!"
        }
        val textView = view.findViewById<TextView>(R.id.mesaj)
        textView.text = greeting
        listView = view.findViewById(R.id.sktyaklasanList)
        list = ArrayList<DataClass2>()
        dbHelper = DatabaseHelper(requireContext())
        sktyaklasanlar()
        gunlukBilgi = view.findViewById(R.id.infoLbl)
        GunlukBilgi()
        return view
    }

    @SuppressLint("Range")
    private fun sktyaklasanlar() {
        val mevcutTRH = Calendar.getInstance().time
        val TRHformat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val cursor: Cursor? = dbHelper.getAllData()
        val geciciList = ArrayList<DataClass2>()
        cursor?.let {
            while (cursor.moveToNext()) {
                val sktString = cursor.getString(cursor.getColumnIndex("urunskt"))
                val sonKtarihi = TRHformat.parse(sktString)
                if (sonKtarihi != null && sonKtarihi.time - mevcutTRH.time < 90L * 24L * 60L * 60L * 1000L) {
                    val uadi = cursor.getString(cursor.getColumnIndex("urunadi"))
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    var kalanGun = ((sonKtarihi.time - mevcutTRH.time) / (24L * 60L * 60L * 1000L)).toString()
                    val urunimg = cursor.getBlob(cursor.getColumnIndex("urunfotografi"))
                    val urunskt = cursor.getString(cursor.getColumnIndex("urunskt"))
                    val urunfiyati = cursor.getString(cursor.getColumnIndex("urunfiyati"))
                    val urunaded = cursor.getInt(cursor.getColumnIndex("urunadedi"))
                    val data = DataClass2()
                    data.id = id
                    data.urunAdi = uadi
                    data.urunFiyati = urunfiyati.toDouble()
                    data.urunAdedi = urunaded
                    data.urunKalanGun = kalanGun
                    data.urunSkt = urunskt
                    data.urunFotografi = urunimg
                    geciciList.add(data)
                }
            }
            cursor.close()
            val sortedList = geciciList.sortedBy { it.urunKalanGun.toDouble() }
            val adapterclass = SktAdapterClass(sortedList.toMutableList() as ArrayList<DataClass2>, requireContext())
            adapterclass.notifyDataSetChanged()
            listView.adapter = adapterclass
        }
    }
    fun GunlukBilgi() {
        val bilgiListesi = ArrayList<String>()
        bilgiListesi.add("Dünyanın en yüksek noktası, Everest Dağı'dır.")
        bilgiListesi.add("İnsan vücudunda 206 adet kemik bulunur.")
        bilgiListesi.add("Deniz seviyesi her yıl yaklaşık olarak 2-3 mm yükselmektedir.")
        bilgiListesi.add("Çin Seddi, dünyanın en uzun yapısal savunma hattıdır.")
        bilgiListesi.add("Ay, Dünya'nın tek doğal uydusudur.")
        bilgiListesi.add("Timsahlar, tüm hayvanlar arasında en uzun yaşayanlardandır.")
        bilgiListesi.add("Ahtapotlar, kollarını koparabildikleri için kendilerini kurtarabilirler.")
        bilgiListesi.add("Dünyadaki en büyük çöl, Sahara Çölü'dür.")
        bilgiListesi.add("Japonya'da her yıl yaklaşık 1.500 deprem meydana gelir.")
        bilgiListesi.add("Dünya üzerindeki en büyük ada, Grönland'dır.")
        bilgiListesi.add("Mavi balinalar, yaşayan en büyük canlılardır.")
        bilgiListesi.add("Amazon Ormanları, dünyanın en büyük yağmur ormanıdır.")
        bilgiListesi.add("Kelebeklerin tat alma duyusu, ayaklarındaki tat alma tomurcuklarıyla gerçekleşir.")
        bilgiListesi.add("Kanarya Adaları, dünyanın en popüler turistik destinasyonlarından biridir.")
        bilgiListesi.add("Gökkuşağı, güneş ışınlarının yağmur damlacıklarında kırılması sonucu oluşur.")
        bilgiListesi.add("Kutup ayıları, karada yaşayan en büyük etçil memelilerdir.")
        bilgiListesi.add("Dünyanın en eski tapınak kompleksi, Türkiye'deki Göbekli Tepe'dir.")
        bilgiListesi.add("Uydu görüntüleri, dünyadaki hava durumu tahminlerinde kullanılır.")
        bilgiListesi.add("Yıldızlar, devasa gaz ve plazmadan oluşan uzay cisimleridir.")
        bilgiListesi.add("Fransa, Eiffel Kulesi ile ünlüdür.")
        bilgiListesi.add("Dünya üzerindeki en büyük göl, Hazar Denizi'dir.")
        bilgiListesi.add("Ağlayan Bayan heykeli, dünyanın en ünlü heykellerinden biridir.")
        bilgiListesi.add("Güneş sistemi, Güneş ve ona bağlı olan gezegenlerden oluşur.")
        bilgiListesi.add("Dünya'nın en büyük okyanusu, Pasifik Okyanusu'dur.")
        bilgiListesi.add("Dünya'daki en büyük su kütlesi, okyanuslardaki su miktarıdır.")
        bilgiListesi.add("Yıldırımlar, elektrik yüklerinin hızla boşalması sonucu oluşur.")
        bilgiListesi.add("Eskimo öpücüğü, burunlarıyla yapılan bir öpücük çeşididir.")
        bilgiListesi.add("Kahverengi gözlü insanlar, dünyadaki en yaygın göz rengine sahiptir.")
        bilgiListesi.add("Güneş, güneş sistemimizin merkezinde bulunan yıldızdır.")
        bilgiListesi.add("Bir yıl, Dünya'nın Güneş etrafında bir tur atmasıdır.")
        bilgiListesi.add("Uydu, Dünya yüzeyine yakın yörüngede dönen yapay bir gök cisimidir.")
        bilgiListesi.add("Dünya'nın en büyük gözlüğü, Çin Seddi'nin görülebildiği Uzay'dadır.")
        bilgiListesi.add("Bir çölde yaşayan kaktüsler, su tutma yetenekleri sayesinde hayatta kalabilirler.")
        bilgiListesi.add("Nükleer enerji, atomların bölünmesi veya birleşmesi sonucu elde edilir.")
        bilgiListesi.add("Güneş sistemindeki en büyük gezegen, Jüpiter'dir.")
        bilgiListesi.add("Bir yelkenli, rüzgarın etkisiyle hareket eden bir deniz aracıdır.")
        bilgiListesi.add("Yerçekimi, cisimleri yere çeken kuvvetin adıdır.")
        bilgiListesi.add("Kıyamet, dünya üzerindeki tüm yaşamın sonunu simgeler.")
        bilgiListesi.add("Hava, atmosferde bulunan gaz karışımıdır.")
        bilgiListesi.add("Sıcak hava balonları, sıcak havanın yükselmesiyle hareket eder.")
        bilgiListesi.add("Dünya'nın en derin noktası, Mariana Çukuru'dur.")
        bilgiListesi.add("Kutuplarda yaşayan penguenler, yürürken kaymamak için kanatlarını dengede tutarlar.")
        bilgiListesi.add("Kuzey Kutbu'nda yaşayan kutup ayıları, yüzmek için özel olarak tasarlanmışlardır.")
        bilgiListesi.add("Dünya'da yaklaşık 7.8 milyar insan yaşamaktadır.")
        bilgiListesi.add("Yer kabuğu, Dünya'nın en dış katmanıdır.")
        bilgiListesi.add("Göktaşları, uzaydan dünya atmosferine düşen taşlardır.")
        bilgiListesi.add("Bir su damlası, moleküllerin bir araya gelmesiyle oluşur.")
        bilgiListesi.add("Dünya, Güneş etrafında saatte yaklaşık 107.000 km hızla hareket eder.")
        bilgiListesi.add("Yılanlar, omurgalı hayvanlar arasında en uzun olanlardır.")
        bilgiListesi.add("Güneş, yaşamın ana kaynağıdır.")
        bilgiListesi.add("Aydınlatma, elektrik enerjisinin kullanılmasıyla gerçekleşir.")
        bilgiListesi.add("Güneşin ışığı, Dünya'ya yaklaşık 8 dakikada ulaşır.")
        bilgiListesi.add("Güneş sistemindeki en küçük gezegen, Plüton'dur.")
        bilgiListesi.add("Dünyada yaklaşık olarak 7.500 farklı dil konuşulmaktadır.")
        bilgiListesi.add("Bir kereviz, sudan daha yoğundur ve suya batmaz.")
        bilgiListesi.add("Dünya'da yaklaşık 70.000 farklı türden böcek bulunmaktadır.")
        bilgiListesi.add("Dünyadaki en uzun nehiri Nil Nehri'dir.")
        bilgiListesi.add("Yıldızlar arasında en yakın komşumuz, Alfa Centauri'dir.")
        bilgiListesi.add("Yunan mitolojisine göre, dünyayı taşıyan Atlas'ın omuzları üzerinde durmaktayız.")
        bilgiListesi.add("Bir yıl, 365 gün, 5 saat, 48 dakika ve 45 saniyedir.")
        bilgiListesi.add("Dünya'nın en büyük kara hayvanı, fil'dir.")
        bilgiListesi.add("Bir düşünce, beyindeki sinir hücrelerinin iletişimiyle oluşur.")
        bilgiListesi.add("Dünyadaki en büyük orman, Amazon Ormanları'dır.")
        bilgiListesi.add("Dünya'da yaklaşık olarak 3 trilyon ağaç bulunmaktadır.")
        bilgiListesi.add("Dünya üzerindeki en büyük kıta, Asya'dır.")
        bilgiListesi.add("Yunan mitolojisinde, dünyayı taşıyan Titan Atlas, gökyüzüne bağlıdır.")
        bilgiListesi.add("Güneş sistemindeki en hızlı gezegen, Merkür'dür.")
        bilgiListesi.add("Dünya üzerindeki en büyük ada, Grönland'dır.")
        bilgiListesi.add("Dünyanın en uzun sürünen hayvanı, Denizanasıdır.")
        bilgiListesi.add("Yarasa, dünyanın tek gerçek uçabilen memelisidir.")
        bilgiListesi.add("Dünyanın en derin gölü, Baykal Gölü'dür.")
        bilgiListesi.add("Dünya'nın en büyük gezegeni, Jüpiter'dir.")
        bilgiListesi.add("Yer kabuğu, tektonik hareketlerle sürekli olarak değişmektedir.")
        bilgiListesi.add("Yunan mitolojisinde, Dünya Tanrıçası Gaia olarak bilinir.")
        bilgiListesi.add("Güneş sistemindeki en soğuk gezegen, Neptün'dür.")
        bilgiListesi.add("Dünya'nın en büyük kanyonu, Büyük Kanyon'dur.")
        bilgiListesi.add("Dünya üzerindeki en büyük gökdelen, Burj Khalifa'dır.")
        bilgiListesi.add("Rüzgar enerjisi, rüzgarın hareket enerjisinin elektrik enerjisine dönüştürülmesiyle elde edilir.")
        bilgiListesi.add("Yer kabuğunun en kalın olduğu yerler, dağ sıralarıdır.")
        bilgiListesi.add("Dünya'nın en yüksek şelalesi, Angel Şelalesi'dir.")
        bilgiListesi.add("Dünyanın en büyük buzulu, Antarktika Buzulu'dur.")
        bilgiListesi.add("Dünya üzerindeki en büyük volkan, Mauna Loa'dır.")
        bilgiListesi.add("Kırmızı renkli bitkiler, güneş ışığındaki mavi ve yeşil renkleri emerler.")
        bilgiListesi.add("Dünya üzerindeki en büyük çöl, Sahara Çölü'dür.")
        bilgiListesi.add("Dünya'nın en büyük gölü, Hazar Denizi'dir.")
        bilgiListesi.add("Güneş sistemindeki en büyük uydusu olan Ganymede, Jüpiter'in bir uydusudur.")
        bilgiListesi.add("Yeryüzündeki en yüksek şelale, Angel Şelalesi'dir.")
        bilgiListesi.add("Deniz seviyesi, yüksek gelgit ve düşük gelgit arasında sürekli olarak değişir.")
        bilgiListesi.add("Dünyanın en büyük adası, Grönland'dır.")
        bilgiListesi.add("Dünyanın en büyük kayalık yapılarından biri olan Ayers Kayası, Avustralya'da bulunur.")
        bilgiListesi.add("Güneş, yaklaşık olarak 4.6 milyar yaşındadır.")
        bilgiListesi.add("Dünya'nın en büyük kıtası, Asya'dır.")
        bilgiListesi.add("Dünya'nın en büyük volkanı, Mauna Loa'dır.")
        bilgiListesi.add("Yeryüzündeki en derin nokta, Mariana Çukuru'dur.")
        bilgiListesi.add("Dünyanın en eski tapınak kompleksi, Göbekli Tepe'dir.")
        bilgiListesi.add("Dünyanın en büyük çölü, Sahara Çölü'dür.")
        bilgiListesi.add("Deniz seviyesi her yıl yaklaşık olarak 2-3 mm yükselmektedir.")
        bilgiListesi.add("Kutup ayıları, karada yaşayan en büyük etçil memelilerdir.")
        bilgiListesi.add("Dünyadaki en büyük yağmur ormanı, Amazon Ormanları'dır.")
        bilgiListesi.add("Dünya'nın en yüksek noktası, Everest Dağı'dır.")
        bilgiListesi.add("Yıldızlar, devasa gaz ve plazmadan oluşan uzay cisimleridir.")
        bilgiListesi.add("Dünya üzerindeki en büyük göl, Hazar Denizi'dir.")
        bilgiListesi.add("Güneş sistemindeki en büyük gezegen, Jüpiter'dir.")
        bilgiListesi.add("Ahtapotlar, kollarını koparabildikleri için kendilerini kurtarabilirler.")
        bilgiListesi.add("Dünyanın en uzun sürünen hayvanı, Denizanası'dır.")
        bilgiListesi.add("Fransa, Eiffel Kulesi ile ünlüdür.")
        bilgiListesi.add("Dünya'nın en büyük okyanusu, Pasifik Okyanusu'dur.")
        bilgiListesi.add("Kutuplarda yaşayan penguenler, yürürken kaymamak için kanatlarını dengede tutarlar.")
        bilgiListesi.add("Dünya üzerindeki en büyük ada, Grönland'dır.")
        bilgiListesi.add("Gökkuşağı, güneş ışınlarının yağmur damlacıklarında kırılması sonucu oluşur.")
        bilgiListesi.add("Dünya'nın en eski tapınak kompleksi, Türkiye'deki Göbekli Tepe'dir.")
        bilgiListesi.add("Uydu görüntüleri, dünyadaki hava durumu tahminlerinde kullanılır.")
        bilgiListesi.add("Dünyanın en büyük çölü, Sahara Çölü'dür.")
        bilgiListesi.add("Timsahlar, tüm hayvanlar arasında en uzun yaşayanlardandır.")
        bilgiListesi.add("Kelebeklerin tat alma duyusu, ayaklarındaki tat alma tomurcuklarıyla gerçekleşir.")
        bilgiListesi.add("Dünyadaki en büyük çöl, Sahara Çölü'dür.")
        bilgiListesi.add("Kanarya Adaları, dünyanın en popüler turistik destinasyonlarından biridir.")
        bilgiListesi.add("Gökkuşağı, güneş ışınlarının yağmur damlacıklarında kırılması sonucu oluşur.")
        bilgiListesi.add("Kutup ayıları, karada yaşayan en büyük etçil memelilerdir.")
        bilgiListesi.add("Dünyanın en eski tapınak kompleksi, Türkiye'deki Göbekli Tepe'dir.")
        bilgiListesi.add("Uydu görüntüleri, dünyadaki hava durumu tahminlerinde kullanılır.")
        bilgiListesi.add("Yıldızlar, devasa gaz ve plazmadan oluşan uzay cisimleridir.")
        bilgiListesi.add("Fransa, Eiffel Kulesi ile ünlüdür.")
        bilgiListesi.add("Dünya üzerindeki en büyük göl, Hazar Denizi'dir.")
        bilgiListesi.add("Güneş sistemindeki en büyük gezegen, Jüpiter'dir.")
        bilgiListesi.add("Ahtapotlar, kollarını koparabildikleri için kendilerini kurtarabilirler.")
        bilgiListesi.add("Dünyanın en uzun sürünen hayvanı, Denizanası'dır.")
        bilgiListesi.add("Fransa, Eiffel Kulesi ile ünlüdür.")
        bilgiListesi.add("Dünya'nın en büyük okyanusu, Pasifik Okyanusu'dur.")
        bilgiListesi.add("Kutuplarda yaşayan penguenler, yürürken kaymamak için kanatlarını dengede tutarlar.")
        bilgiListesi.add("Dünya üzerindeki en büyük ada, Grönland'dır.")
        bilgiListesi.add("Gökkuşağı, güneş ışınlarının yağmur damlacıklarında kırılması sonucu oluşur.")
        bilgiListesi.add("Dünya'nın en eski tapınak kompleksi, Türkiye'deki Göbekli Tepe'dir.")
        bilgiListesi.add("Uydu görüntüleri, dünyadaki hava durumu tahminlerinde kullanılır.")
        bilgiListesi.add("Dünyanın en büyük çölü, Sahara Çölü'dür.")
        bilgiListesi.add("Timsahlar, tüm hayvanlar arasında en uzun yaşayanlardandır.")
        bilgiListesi.add("Kelebeklerin tat alma duyusu, ayaklarındaki tat alma tomurcuklarıyla gerçekleşir.")
        bilgiListesi.add("Dünyadaki en büyük buzul, Antarktika Buzulu'dur.")
        bilgiListesi.add("Bir su damlası, moleküllerin bir araya gelmesiyle oluşur.")
        bilgiListesi.add("Dünya'nın en büyük kanyonu, Büyük Kanyon'dur.")
        bilgiListesi.add("Dünya üzerindeki en büyük gökdelen, Burj Khalifa'dır.")
        bilgiListesi.add("Rüzgar enerjisi, rüzgarın hareket enerjisinin elektrik enerjisine dönüştürülmesiyle elde edilir.")
        bilgiListesi.add("Yer kabuğunun en kalın olduğu yerler, dağ sıralarıdır.")
        bilgiListesi.add("Dünya'nın en yüksek şelalesi, Angel Şelalesi'dir.")
        bilgiListesi.add("Dünyanın en büyük buzulu, Antarktika Buzulu'dur.")
        bilgiListesi.add("Dünya üzerindeki en büyük volkan, Mauna Loa'dır.")
        bilgiListesi.add("Kırmızı renkli bitkiler, güneş ışığındaki mavi ve yeşil renkleri emerler.")
        bilgiListesi.add("Dünya üzerindeki en büyük çöl, Sahara Çölü'dür.")
        val random = kotlin.random.Random.Default
        val index = random.nextInt(bilgiListesi.size)
        val secilenBilgi = bilgiListesi[index]
        gunlukBilgi.setText(secilenBilgi)

    }
}



