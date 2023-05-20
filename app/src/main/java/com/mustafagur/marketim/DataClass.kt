package com.mustafagur.marketim

class DataClass {
    var id: Int = 0
        get() = field
        set(value) {
            field = value
        }

    var urunAdi: String = ""
        get() = field
        set(value) {
            field = value
        }

    var urunFiyati: Double = 0.0
        get() = field
        set(value) {
            field = value
        }

    var urunAdedi: Int = 0
        get() = field
        set(value) {
            field = value
        }

    var urunFotografi: ByteArray? = null
        get() = field
        set(value) {
            field = value
        }

    var urunSkt: String = ""
        get() = field
        set(value) {
            field = value
        }
}
