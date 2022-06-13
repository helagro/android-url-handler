package com.hlag.customurlhandler

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.util.*

class DbHelper private constructor(context: Context) {
    companion object {
        const val DB_NAME = "urlItems.db"
        const val TABLE_NAME = "urlItems"
        private const val TAG = "DbHelper"
        private var ourInstance: DbHelper? = null
        fun getInstance(context: Context): DbHelper {
            if (ourInstance == null) {
                ourInstance = DbHelper(context)
            }
            return ourInstance!!
        }
    }
    private var db: SQLiteDatabase

    init {
        db = context.openOrCreateDatabase(
            DB_NAME,
            Context.MODE_PRIVATE,
            null)
        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME (id INTEGER primary key, name TEXT, url TEXT, app TEXT, broadcast TEXT)")
    }
    
    fun getUrlItems(): ArrayList<UrlItem> {
        val urlItems = ArrayList<UrlItem>()
        val myCursor = db.rawQuery(
            "select id, name, url, app, broadcast from $TABLE_NAME;",
            null
        )
        while (myCursor.moveToNext()) {
            val urlItem = UrlItem(
                myCursor.getLong(0),
                myCursor.getString(1),
                myCursor.getString(2),
                myCursor.getString(3),
                myCursor.getString(4)
            )
            urlItems.add(urlItem)
        }
        myCursor.close()
        return urlItems
    }

    fun updateItem(urlItem: UrlItem) {
        val cv = ContentValues()
        cv.put("name", urlItem.name)
        cv.put("url", urlItem.url)
        cv.put("app", urlItem.app)
        cv.put("broadcast", urlItem.broadcast)

        if (db.update(TABLE_NAME, cv, "id=" + urlItem.id, null) == 0) {
            urlItem.id = db.insertWithOnConflict(
                TABLE_NAME,
                null,
                cv,
                SQLiteDatabase.CONFLICT_REPLACE
            )
        }
    }

    fun delUrlItem(id: Long) {
        db.delete(TABLE_NAME, "id=$id", null)
    }

    fun close() {
        db.close()
        ourInstance = null
    }
}
