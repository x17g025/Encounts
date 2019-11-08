package com.example.encount

import android.os.AsyncTask
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DatabaseAccess : AsyncTask<String, String, String>() {

    override fun doInBackground(vararg params: String): String {

        //可変長引数の1個目(インデックス0)を取得。これが都市ID
        val id = params[0]
        val postData = ""

        //都市IDを使って接続URL文字列を作成。
        val urlStr = "kinako.cf/phptest.php"

        //URLオブジェクトを生成。
        val url = URL(urlStr)

        //URLオブジェクトからHttpURLConnectionオブジェクトを取得。
        val con = url.openConnection() as HttpURLConnection

        //http接続メソッドを設定。
        con.requestMethod = "GET"
        con.doOutput = true
        con.setReadTimeout(10000)
        con.setConnectTimeout(20000)
        val outStream = con.outputStream
        outStream.write(postData.toByteArray())
        outStream.flush()
        outStream.close()

        //接続。
        con.connect()

        //HttpURLConnectionオブジェクトからレスポンスデータを取得。天気情報が格納されている。
        //val stream = con.inputStream
        //レスポンスデータであるInputStreamオブジェクトを文字列(JSON文字列)に変換。
        // val result = is2String(stream)

        //HttpURLConnectionオブジェクトを解放。
        con.disconnect()

        //InputStreamオブジェクトを解放。
        //stream.close()

        //JSON文字列を返す。
        var result = "a"
        return result
    }

    override fun onPostExecute(result: String) {

        //JSON文字列からJSONObjectオブジェクトを生成。これをルートJSONオブジェクトとする。
        val rootJSON = JSONObject(result)

        //ルートJSON直下の「description」JSONオブジェクトを取得。
        val descriptionJSON = rootJSON.getJSONObject("description")

        //「description」プロパティ直下の「text」文字列(天気概況文)を取得。
        val desc = descriptionJSON.getString("text")

        //ルートJSON直下の「forecasts」JSON配列を取得。
        val forecasts = rootJSON.getJSONArray("forecasts")

        //「forecasts」JSON配列のひとつ目(インデックス0)のJSONオブジェクトを取得。
        val forecastNow = forecasts.getJSONObject(0)

        //「forecasts」ひとつ目のJSONオブジェクトから「telop」文字列(天気)を取得。
        val telop = forecastNow.getString("telop")
    }
}

private fun is2String(stream: InputStream): String {
    val sb = StringBuilder()
    val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
    var line = reader.readLine()

    while(line != null) {
        sb.append(line)
        line = reader.readLine()
    }

    reader.close()
    return sb.toString()
}
