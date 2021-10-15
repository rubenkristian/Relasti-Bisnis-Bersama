package com.ethelworld.RBBApp.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.provider.ContactsContract.PhoneLookup
import android.util.Log
import androidx.core.database.getStringOrNull
import com.ethelworld.RBBApp.DBInterface.OnContactSelected
import com.ethelworld.RBBApp.DBInterface.OnSelected
import com.ethelworld.RBBApp.Item.Contact
import com.ethelworld.RBBApp.tools.auth.Authentication

@Suppress("UNREACHABLE_CODE")
class EthelDBHelper(private val context: Context?):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_TABLE_CONTACT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACT")
        var upgradeTo = oldVersion + 1
        while(upgradeTo <= newVersion) {
            when(upgradeTo) {
                3 -> db?.execSQL("ALTER TABLE $TABLE_CONTACT ADD COLUMN $COLUMN_IMAGE TEXT NOT NULL DEFAULT ''")
            }
            upgradeTo++
        }
//        if(newVersion > oldVersion) {
//            db?.needUpgrade(newVersion)
//        }
//        onCreate(db)
    }

    fun addContact(
        idUser: Long,
        image: String = "",
        name: String?,
        occupation: String?,
        company: String?,
        province: String?,
        city: String?,
        wa: String?,
        facebook: String = "",
        instagram: String = "",
        youtube: String = "",
        onlineshop: String = "",
        tiktok: String = ""): Contact {
        val values = ContentValues()
        values.put(COLUMN_ID_SERVER, idUser)
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_OCCUPATION, occupation)
        values.put(COLUMN_COMPANY, company)
        values.put(COLUMN_PROVINCE, province)
        values.put(COLUMN_CITY, city)
        values.put(COLUMN_WA, wa)
        values.put(COLUMN_SAVED, false)
        val db = this.writableDatabase
        val lastID = db.insert(TABLE_CONTACT, null, values)
        val contact = Contact(false, lastID, idUser, image, name,
            "$province $city", occupation, company, province, city, wa, facebook, instagram, onlineshop, tiktok, youtube)
        db.close()
        return contact
    }

    fun setContactSaved(id: Long) : Boolean {
        val result: Boolean
        val values = ContentValues()
        values.put(COLUMN_SAVED, 1)
        val db = this.writableDatabase
        result = db.update(TABLE_CONTACT, values, "$COLUMN_ID = $id", null) > 0
        db.close()
        return result
    }

    suspend fun getContactDetail(idDB: Long, listener: OnContactSelected) {
        val contact: Contact
        val db = this.readableDatabase

        val query = "SELECT * FROM $TABLE_CONTACT WHERE $COLUMN_ID = $idDB LIMIT 1"

        val cursor = db.rawQuery(query, null)
        if(cursor != null) {
            if(cursor.count > 0) {
                val saved       = cursor.getColumnIndexOrThrow(COLUMN_SAVED)
                val id          = cursor.getColumnIndexOrThrow(COLUMN_ID)
                val idUser      = cursor.getColumnIndexOrThrow(COLUMN_ID_SERVER)
                val image       = cursor.getColumnIndex(COLUMN_IMAGE)
                val name        = cursor.getColumnIndexOrThrow(COLUMN_NAME)
                val province    = cursor.getColumnIndexOrThrow(COLUMN_PROVINCE)
                val city        = cursor.getColumnIndexOrThrow(COLUMN_CITY)
                val occupation  = cursor.getColumnIndexOrThrow(COLUMN_OCCUPATION)
                val company     = cursor.getColumnIndexOrThrow(COLUMN_COMPANY)
                val wa          = cursor.getColumnIndexOrThrow(COLUMN_WA)
                val facebook    = cursor.getColumnIndexOrThrow(COLUMN_FACEBOOK)
                val instagram   = cursor.getColumnIndexOrThrow(COLUMN_INSTAGRAM)
                val olshop      = cursor.getColumnIndexOrThrow(COLUMN_ONLINESHOP)
                val tiktok      = cursor.getColumnIndexOrThrow(COLUMN_TIKTOK)
                val youtube     = cursor.getColumnIndexOrThrow(COLUMN_YOUTUBE)
                cursor.moveToFirst()
                contact = Contact(
                    cursor.getInt(saved) == 1,
                    cursor.getLong(id),
                    cursor.getLong(idUser),
                    cursor.getStringOrNull(image).toString(),
                    cursor.getString(name),
                    cursor.getString(province) + cursor.getShort(city),
                    cursor.getString(occupation),
                    cursor.getString(company),
                    cursor.getString(province),
                    cursor.getString(city),
                    cursor.getString(wa),
                    if(cursor.isNull(facebook)) "-" else cursor.getString(facebook),
                    if(cursor.isNull(instagram)) "-" else cursor.getString(instagram),
                    if(cursor.isNull(olshop)) "-" else cursor.getString(olshop),
                    if(cursor.isNull(tiktok)) "-" else cursor.getString(tiktok),
                    if(cursor.isNull(youtube)) "-" else cursor.getString(youtube)
                )

                listener.onContactSelected(contact)
            } else {
                listener.onErrorContact("Terjadi kesalaha, kontak tidak ditemukan.")
            }
        }
        cursor.close()
    }

    fun getAllContactSize(): Int {
        var size = 0
        val db = this.readableDatabase
        val query = "SELECT COUNT(1) AS row_size FROM $TABLE_CONTACT"
        val cursor = db.rawQuery(query, null)
        if(cursor != null) {
            if(cursor.count > 0) {
                val row = cursor.getColumnIndex("row_size")
                cursor.moveToFirst()
                size = cursor.getInt(row)
            }
        }
        cursor.close()
        return size
    }

    suspend fun getListContact(search: String?, from: Int, limit: Int, listener: OnSelected) {
        val contacts = ArrayList<Contact?>()
        val db = this.readableDatabase

        if (search?.trim()!!.isNotEmpty()) {
//            Log.i("CONTACT", "DB NOT EMPTY")
            val query =
                "SELECT * FROM $TABLE_CONTACT WHERE $COLUMN_NAME LIKE '%$search%' ORDER BY $COLUMN_ID DESC LIMIT $from, $limit"
            val cursor = db.rawQuery(query, null)
            if (cursor != null) {
                if (cursor.count > 0) {
                    val saved       = cursor.getColumnIndexOrThrow(COLUMN_SAVED)
                    val id          = cursor.getColumnIndexOrThrow(COLUMN_ID)
                    val idUser      = cursor.getColumnIndexOrThrow(COLUMN_ID_SERVER)
                    val image       = cursor.getColumnIndex(COLUMN_IMAGE)
                    val name        = cursor.getColumnIndexOrThrow(COLUMN_NAME)
                    val province    = cursor.getColumnIndexOrThrow(COLUMN_PROVINCE)
                    val city        = cursor.getColumnIndexOrThrow(COLUMN_CITY)
                    val occupation  = cursor.getColumnIndexOrThrow(COLUMN_OCCUPATION)
                    val company     = cursor.getColumnIndexOrThrow(COLUMN_COMPANY)
                    val wa          = cursor.getColumnIndexOrThrow(COLUMN_WA)
                    val facebook    = cursor.getColumnIndexOrThrow(COLUMN_FACEBOOK)
                    val instagram   = cursor.getColumnIndexOrThrow(COLUMN_INSTAGRAM)
                    val olshop      = cursor.getColumnIndexOrThrow(COLUMN_ONLINESHOP)
                    val tiktok      = cursor.getColumnIndexOrThrow(COLUMN_TIKTOK)
                    val youtube     = cursor.getColumnIndexOrThrow(COLUMN_YOUTUBE)
                    cursor.moveToFirst()
                    while (!cursor.isAfterLast) {
                        contacts.add(
                            Contact(
                                cursor.getInt(saved) == 1,
                                cursor.getLong(id),
                                cursor.getLong(idUser),
                                cursor.getStringOrNull(image).toString(),
                                cursor.getString(name),
                                cursor.getString(province) + cursor.getShort(city),
                                cursor.getString(occupation),
                                cursor.getString(company),
                                cursor.getString(province),
                                cursor.getString(city),
                                cursor.getString(wa),
                                if (cursor.isNull(facebook)) "-" else cursor.getString(facebook),
                                if (cursor.isNull(instagram)) "-" else cursor.getString(
                                    instagram
                                ),
                                if (cursor.isNull(olshop)) "-" else cursor.getString(olshop),
                                if (cursor.isNull(tiktok)) "-" else cursor.getString(tiktok),
                                if (cursor.isNull(youtube)) "-" else cursor.getString(youtube)
                            )
                        )
                        cursor.moveToNext()
                    }
                }
            }
            cursor.close()
        } else {
            //            Log.i("CONTACT", "DB EMPTY")
            val query =
                "SELECT * FROM $TABLE_CONTACT ORDER BY $COLUMN_ID DESC LIMIT $from, $limit"
            val cursor = db.rawQuery(query, null)

            if (cursor != null) {
                if (cursor.count > 0) {
                    val saved       = cursor.getColumnIndexOrThrow(COLUMN_SAVED)
                    val id          = cursor.getColumnIndexOrThrow(COLUMN_ID)
                    val idUser      = cursor.getColumnIndexOrThrow(COLUMN_ID_SERVER)
                    val image       = cursor.getColumnIndex(COLUMN_IMAGE)
                    val name        = cursor.getColumnIndexOrThrow(COLUMN_NAME)
                    val province    = cursor.getColumnIndexOrThrow(COLUMN_PROVINCE)
                    val city        = cursor.getColumnIndexOrThrow(COLUMN_CITY)
                    val occupation  = cursor.getColumnIndexOrThrow(COLUMN_OCCUPATION)
                    val company     = cursor.getColumnIndexOrThrow(COLUMN_COMPANY)
                    val wa          = cursor.getColumnIndexOrThrow(COLUMN_WA)
                    val facebook    = cursor.getColumnIndexOrThrow(COLUMN_FACEBOOK)
                    val instagram   = cursor.getColumnIndexOrThrow(COLUMN_INSTAGRAM)
                    val olshop      = cursor.getColumnIndexOrThrow(COLUMN_ONLINESHOP)
                    val tiktok      = cursor.getColumnIndexOrThrow(COLUMN_TIKTOK)
                    val youtube     = cursor.getColumnIndexOrThrow(COLUMN_YOUTUBE)
                    cursor.moveToFirst()
                    while (!cursor.isAfterLast) {
                        contacts.add(
                            Contact(
                                cursor.getInt(saved) == 1,
                                cursor.getLong(id),
                                cursor.getLong(idUser),
                                cursor.getStringOrNull(image).toString(),
                                cursor.getString(name),
                                cursor.getString(province) + cursor.getShort(city),
                                cursor.getString(occupation),
                                cursor.getString(company),
                                cursor.getString(province),
                                cursor.getString(city),
                                cursor.getString(wa),
                                if (cursor.isNull(facebook)) "-" else cursor.getString(facebook),
                                if (cursor.isNull(instagram)) "-" else cursor.getString(
                                    instagram
                                ),
                                if (cursor.isNull(olshop)) "-" else cursor.getString(olshop),
                                if (cursor.isNull(tiktok)) "-" else cursor.getString(tiktok),
                                if (cursor.isNull(youtube)) "-" else cursor.getString(youtube)
                            )
                        )
                        cursor.moveToNext()
                    }
                }
            }
            cursor.close()
        }
        db.close()
        if (contacts.size > 0) {
            listener.onSelectedFinish(contacts)
        } else {
            listener.onEnd()
        }

    }

    private fun updateImage(image: String, id: Long): Boolean {
        val result: Boolean
        val values = ContentValues()
        values.put(COLUMN_IMAGE, image)
        val db = this.writableDatabase
        result = db.update(
            TABLE_CONTACT,
            values,
            "$COLUMN_ID = $id",
            null) > 0
        db.close()
        return  result
    }

    private fun contactExists(number: String): Boolean{
        if(Authentication(context).isContactGranted()) {
            val lookupUri = Uri.withAppendedPath(
                PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number))
            val phoneNumberProjection = arrayOf(
                PhoneLookup._ID,
                PhoneLookup.NUMBER,
                PhoneLookup.DISPLAY_NAME)

            val cursor = context?.contentResolver?.query(
                lookupUri,
                phoneNumberProjection,
                null,
                null,
                null)
            try {
                return cursor?.moveToFirst()!!
            } finally {
                cursor?.close()
                return false
            }
        } else {
            return false
        }
    }

    fun dropTable() {
        val db = this.writableDatabase
        db.delete(TABLE_CONTACT, null, null)
        db.close()
    }

    companion object{
        const val DATABASE_NAME = "ehthel.db"
        const val DATABASE_VERSION = 3
        const val TABLE_CONTACT = "contact"
        const val COLUMN_ID = "_id"
        const val COLUMN_ID_SERVER = "id_user"
        const val COLUMN_NAME = "name"
        const val COLUMN_OCCUPATION = "occupation"
        const val COLUMN_COMPANY = "company"
        const val COLUMN_PROVINCE = "province"
        const val COLUMN_CITY = "city"
        const val COLUMN_WA = "wa"
        const val COLUMN_YOUTUBE = "youtube"
        const val COLUMN_TIKTOK = "tiktok"
        const val COLUMN_ONLINESHOP = "onlineshop"
        const val COLUMN_FACEBOOK = "facebook"
        const val COLUMN_INSTAGRAM = "instagram"
        const val COLUMN_SAVED = "saved"
        const val COLUMN_IMAGE = "image"

        // table query
        const val SQL_CREATE_TABLE_CONTACT = "CREATE TABLE ${TABLE_CONTACT}(" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_ID_SERVER INTEGER NOT NULL, " +
                "$COLUMN_IMAGE TEXT NOT NULL DEFAULT '', " +
                "$COLUMN_NAME TEXT NOT NULL, " +
                "$COLUMN_OCCUPATION TEXT NOT NULL, " +
                "$COLUMN_COMPANY TEXT NOT NULL, "+
                "$COLUMN_PROVINCE TEXT NOT NULL, " +
                "$COLUMN_CITY TEXT NOT NULL, " +
                "$COLUMN_WA TEXT NOT NULL, " +
                "$COLUMN_YOUTUBE TEXT, " +
                "$COLUMN_FACEBOOK TEXT, " +
                "$COLUMN_INSTAGRAM TEXT, " +
                "$COLUMN_ONLINESHOP TEXT, " +
                "$COLUMN_TIKTOK TEXT, " +
                "$COLUMN_SAVED INTEGER)"
    }
}