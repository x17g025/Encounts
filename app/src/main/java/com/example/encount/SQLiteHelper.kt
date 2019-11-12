package com.example.encount

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.lang.StringBuilder

class SQLiteHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{

        private const val DATABASE_NAME = "userLogin.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {

        val sb = StringBuilder()
        sb.append("create table userLogin(")
        sb.append("_id integer primary key,")
        sb.append("userId integer,")
        sb.append(");")
        val sql = sb.toString()

        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}