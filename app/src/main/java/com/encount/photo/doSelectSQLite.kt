package com.encount.photo

import android.content.Context

fun doSelectSQLite(context: Context?): String{

    val _helper = SQLiteHelper(context)

    var id = ""
    val db = _helper.writableDatabase
    val sql = "select * from userInfo"
    val cursor = db.rawQuery(sql, null)

    while (cursor.moveToNext()) {

        val idxId = cursor.getColumnIndex("user_id")
        id = cursor.getString(idxId)
    }

    _helper.close()

    return id
}