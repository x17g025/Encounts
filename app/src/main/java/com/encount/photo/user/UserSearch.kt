package com.encount.photo.user

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.encount.photo.LoginDataClassList
import com.encount.photo.R
import com.encount.photo.SQLiteHelper
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_search_home.*
import okhttp3.*
import java.io.IOException

/**
 * やってること
 * メアドとパスワードをサーバに送信し、サーバからログイン成功(true)が帰ってきたらホーム画面に飛ばす
 *
 * 製作者：中村
 */

class UserSearch : Fragment() {

    var _helper : SQLiteHelper? = null
    var name = ""
    var num = ""
    var id : Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_search_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        _helper = SQLiteHelper(context)

        progress.visibility = View.GONE

        btnSearch.setOnClickListener {

            name = etName.text.toString()
            num  = etNumber.text.toString()

            if(name.isNotEmpty() && num.isNotEmpty()){

                progress.visibility = View.VISIBLE
                UserSearch().execute()

            }
            else {

                if(name.isEmpty()){

                    etName.error = "ユーザー名が入力されていません"
                }

                if(num.isEmpty()){

                    etNumber.error = "ユーザーIDが入力されていません"
                }
            }
        }

    }

    private inner class UserSearch() : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {

            val client = OkHttpClient()

            //アクセスするURL
            val url = "https://encount.cf/encount/UserSearch.php"

            //Formを作成
            val formBuilder = FormBody.Builder()

            //formに要素を追加
            formBuilder.add("name",name)
            formBuilder.add("num",num)
            //リクエストの内容にformを追加
            val form = formBuilder.build()

            //リクエストを生成
            val request = Request.Builder().url(url).post(form).build()

            try {
                val response = client.newCall(request).execute()
                return response.body()!!.string()
            }
            catch (e: IOException) {
                e.printStackTrace()
                return "Error"
            }
        }

        override fun onPostExecute(result: String) {

            var search = Gson().fromJson(result, LoginDataClassList::class.java)

            progress.visibility = View.GONE

            if(search.flag) {

                id = search.userId.toInt()

                transAct()
            }
            else{

                SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Error")
                    .setContentText("ユーザーが見つかりませんでした")
                    .setConfirmText("OK")
                    .setConfirmClickListener { sDialog ->
                        sDialog.dismissWithAnimation()

                    }
                    .show()
            }
        }
    }

    override fun onDestroy() {

        _helper!!.close()
        super.onDestroy()
    }

    fun transAct(){

        val intent = Intent(context, UserProfile::class.java)
        intent.putExtra("User_Id", id.toString())
        startActivity(intent)
    }
}
