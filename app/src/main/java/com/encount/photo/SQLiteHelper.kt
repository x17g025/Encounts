package com.encount.photo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.lang.StringBuilder

/**
 * やってること
 * 端末のSQLiteを使うためのクラス(ローカルストレージなのでサーバと通信しない)
 *
 * 製作者：中村
 */

class SQLiteHelper(context: Context?): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{

        private const val DATABASE_NAME = "userInfo.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {

        val sb = StringBuilder()
        sb.append("CREATE TABLE userInfo(")
        sb.append("_id INTEGER PRIMARY KEY,")
        sb.append("user_id INTEGER")
        sb.append(");")
        val sql = sb.toString()
        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}