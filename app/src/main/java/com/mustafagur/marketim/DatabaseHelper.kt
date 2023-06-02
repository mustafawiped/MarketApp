import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "database.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "veriler"
        private const val COLUMN_ID = "id"
        private const val COLUMN_PRODUCT = "urunadi"
        private const val COLUMN_PRICE = "urunfiyati"
        private const val COLUMN_AMOUNT = "urunadedi"
        private const val COLUMN_IMAGE = "urunfotografi"
        private const val COLUMN_EXD = "urunskt"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery =
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_PRODUCT TEXT, $COLUMN_PRICE TEXT , $COLUMN_AMOUNT BYTE , $COLUMN_EXD TEXT , $COLUMN_IMAGE BLOB)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun searchData(keyword: String): Cursor? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_PRODUCT LIKE '%$keyword%'"
        return db.rawQuery(query, null)
    }

    fun insertData(product: String, price: String, amount: Byte, image: ByteArray, exd: String, note: String): Long {
        val values = ContentValues()
        values.put(COLUMN_PRODUCT, product)
        values.put(COLUMN_PRICE, price)
        values.put(COLUMN_AMOUNT, amount)
        values.put(COLUMN_IMAGE, image)
        values.put(COLUMN_EXD, exd)
        val db = this.writableDatabase
        return db.insert(TABLE_NAME, null, values)
    }

    fun updateData(id: Long, product: String, price: String, amount: Byte, image: ByteArray, exd: String): Int {
        val values = ContentValues()
        values.put(COLUMN_PRODUCT, product)
        values.put(COLUMN_PRICE, price)
        values.put(COLUMN_AMOUNT, amount)
        values.put(COLUMN_IMAGE, image)
        values.put(COLUMN_EXD, exd)
        val db = this.writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(id.toString())
        return db.update(TABLE_NAME, values, whereClause, whereArgs)
    }

    fun deleteData(id: Long): Int {
        val db = this.writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(id.toString())
        return db.delete(TABLE_NAME, whereClause, whereArgs)
    }

    fun getAllData(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }
}