package com.example.encount.post

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.encount.NavigationActivity
import com.example.encount.R
import com.example.encount.SQLiteHelper
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_user_post.*
import okhttp3.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 投稿機能(カメラ、コメント、位置情報)
 * 制作者：大野、社務
 */

class UserPost : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val requestingLocationUpdates = true //フラグ
    private val locationRequest: LocationRequest = LocationRequest.create()
    val _helper = SQLiteHelper(this@UserPost)
    //写真のパスを受け取る変数(将来的には撮影した写真のパス、ファイル名を取得して指定する)
    var uurl = ""
    //送信するコメント内容の受け取り変数
    var cmnt = ""

    //保存された画像のURI
    private var _imageUri: Uri? = null
    //緯度フィールド
    private var lat = ""
    //経度フィールド
    private var lng = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_post)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest.setInterval(10000)   //最遅の更新間隔
        locationRequest.setFastestInterval(5000)   //最速の更新間隔
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)           //バッテリー消費を抑えたい場合、精度は100m程度

        /**
         * 投稿処理
         * 投稿ボタン押すと動作する
         */
        val commentInput = findViewById<EditText>(R.id.commentInput)
        ivCameraBig.visibility = View.GONE
        postClose.setOnClickListener {
            startActivity(Intent(this, NavigationActivity::class.java))
        }

        ivCamera.setOnClickListener {
            ivCamera.visibility = View.GONE
            ivCameraBig.visibility = View.VISIBLE
        }

        ivCameraBig.setOnClickListener {
            ivCamera.visibility = View.VISIBLE
            ivCameraBig.visibility = View.GONE
        }

        // 投稿ボタンが押された時
        postButton.setOnClickListener {

            //ここで、緯度と経度がちゃんと取得できていなければ、投稿できないようにする。
            Log.d("緯度経度テスト",lat + lng)

            if((lat != "")and(lng != "")){

                if(_imageUri != null) {
                    //パスの処理
                    val uuri = getFileSchemeUri(_imageUri as Uri)

                    println(uuri.toString())

                    var pass = uuri.toString().substring(uuri.toString().length - 17)
                    print(pass)
                    uurl = pass

                    //コメントをEditTextから取得
                    cmnt = commentInput.getText().toString()
                    /*
                    //緯度を取得
                    latitude = _latitude.toString()
                    //経度を取得
                    longitude = _longitude.toString()
                    */
                    Log.d("debug","緯度Str")
                    //ここで現在地取得処理(更新)を終了させる
                    print("GPS終了")
                    onPause()

                    //投稿処理開始
                    val postTask = OkHttpPost()
                    postTask.execute(/*uuri.toString()*/)

                    SweetAlertDialog(this@UserPost, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("投稿完了")
                        .setContentText("")
                        .setConfirmText("ホーム画面へ")
                        .setConfirmClickListener {
                                sDialog -> sDialog.dismissWithAnimation()
                            goHome()
                        }
                        .show()
                }else{
                }

            }else{
                SweetAlertDialog(this@UserPost, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("投稿失敗")
                    .setContentText("位置情報が取得できません。設定を見直すか、電波状況が良い場所へ移動してください。")
                    .setConfirmText("戻る")
                    .setConfirmClickListener {
                            sDialog -> sDialog.dismissWithAnimation()
                        //goHome()
                    }
                    .show()
            }



        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //カメラアプリからの戻りでかつ撮影成功の場合
        if(requestCode == 200 && resultCode == RESULT_OK) {
            //撮影された画像のビットマップデータを取得。
            val bitmap = data?.getParcelableExtra<Bitmap>("data")
            //画像を表示するImageViewを取得。
            val ivCamera = findViewById<ImageView>(R.id.ivCamera)
            //撮影された画像をImageViewに設定。
            ivCamera.setImageBitmap(bitmap)
            ivCameraBig.setImageBitmap(bitmap)
            //フィールドの画像URIをImageViewに設定。
            ivCamera.setImageURI(_imageUri)
            ivCameraBig.setImageURI(_imageUri)

            //デバッグ用
            System.out.println("変換前"+_imageUri)

            /**
             * ここで一番下のメソッドを利用して、パスを取得する
             */
            val uuri = getFileSchemeUri(_imageUri as Uri)
            println("変換後："+uuri.toString())

        } else{
            photoButton.visibility  = View.VISIBLE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        //ACCESS_FINE_LOCATIONに対するパーミションダイアログでかつ許可を選択したなら…
        if(requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //再度ACCESS_FINE_LOCATIONの許可が下りていないかどうかのチェックをし、降りていないなら処理を中止。
            if(ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            //位置情報の追跡を開始。
            onResume()
        }
        //ここまで
        //WRITE_EXTERNAL_STORAGEに対するパーミションダイアログでかつ許可を選択したなら…
        if(requestCode == 2000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //もう一度カメラアプリを起動。
            val ivCamera = findViewById<ImageView>(R.id.ivCamera)
            onCameraImageClick(ivCamera)
        }
    }

    /**
     * 画像部分がタップされたときの処理メソッド。
     * カメラのパーミッション設定の確認と同時に、ここで現在地取得のパーミッションも確認して、許可がないなら再度リクエストする処理を追加する
     */
    /*private*/ fun onCameraImageClick(view: View) {
        //↑にprivateをつけるとうまく動作しなくなる
        //WRITE_EXTERNAL_STORAGEの許可が下りていないなら…
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //WRITE_EXTERNAL_STORAGEの許可を求めるダイアログを表示。その際、リクエストコードを2000に設定。
            val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions, 2000)
            return
        }

        //日時データを「yyyyMMddHHmmss」の形式に整形するフォーマッタを生成。
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss")
        //現在の日時を取得。
        val now = Date()
        //取得した日時データを「yyyyMMddHHmmss」形式に整形した文字列を生成。
        val nowStr = dateFormat.format(now)
        //ストレージに格納する画像のファイル名を生成。ファイル名の一意を確保するためにタイムスタンプの値を利用。
        val fileName = "UseCameraActivityPhoto_${nowStr}.jpg"

        //ContentValuesオブジェクトを生成。
        val values = ContentValues()
        //画像ファイル名を設定。
        values.put(MediaStore.Images.Media.TITLE, fileName)
        //画像ファイルの種類を設定。
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        //ContentResolverを使ってURIオブジェクトを生成。
        _imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //Intentオブジェクトを生成。
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //Extra情報として_imageUriを設定。
        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageUri)
        //アクティビティを起動。
        startActivityForResult(intent, 200)

        photoButton.visibility  = View.GONE
    }

    /**
     * URIをFileスキームのURIに変換する.
     * @param uri 変換前のURI  例) content://media/external/images/media/33
     * @return 変換後のURI     例) file:///storage/sdcard/test1.jpg
     */
    private fun getFileSchemeUri(uri: Uri): Uri {
        var fileSchemeUri = uri
        val path = getPath(uri)
        fileSchemeUri = Uri.fromFile(File(path))
        return fileSchemeUri
    }

    /**
     * URIからファイルPATHを取得する.
     * @param uri URI
     * @return ファイルPATH
     */
    private fun getPath(uri: Uri): String {
        var path = uri.toString()
        if (path.matches("^file:.*".toRegex())) {
            return path.replaceFirst("file://".toRegex(), "")
        } else if (!path.matches("^content:.*".toRegex())) {
            return path
        }
        val context = applicationContext
        val contentResolver = context.contentResolver
        val columns = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, columns, null, null, null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                path = cursor.getString(0)
            }
            cursor.close()
        }
        return path
    }

    private inner class OkHttpPost : AsyncTask<String, String, String>() {

        public override
        fun doInBackground(vararg ImagePath: String): String? {

            //user-id(将来的にはAndroid内のSQLiteから取得)
            var id     = ""
            val db     = _helper.writableDatabase
            val sql    = "select * from userInfo"
            val cursor = db.rawQuery(sql, null)

            while(cursor.moveToNext()){

                val idxId = cursor.getColumnIndex("user_id")
                id = cursor.getString(idxId)
            }

            //アクセスするURL
            val url = "https://encount.cf/encount/PostPhoto.php"

            //パスを設定
            //var pass = "/sdcard/Pictures/"
            var pass = "/storage/emulated/0/Pictures/"
            //ファイル名を取得
            pass = pass + uurl
            //写真のパスを取得する
            val file2 = File(pass)
            val str = file2.absolutePath
            println("pass : $str")
            //ファイルの存在確認(uurlの写真が存在するのか)　※デバッグ用
            if (file2.exists()) {
                println("ファイルが存在します。")
            } else {
                println("ファイルが存在しません。")
            }

            Log.d("debug","緯度Str" + lat)
            //ここでPOSTする内容を設定　"image/jpg"の部分は送りたいファイルの形式に合わせて変更する
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userId", id)
                .addFormDataPart("longitude", lng)
                .addFormDataPart("latitude", lat)
                .addFormDataPart("word", cmnt)
                .addFormDataPart(
                    "file",
                    file2.name,
                    RequestBody.create(MediaType.parse("image/jpg"), file2)
                )
                .build()

            val client = OkHttpClient()
                .newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build()

            //リクエストの作成
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
            //ここまで

            try {
                val response = client.newCall(request).execute()
                //System.out.println(response.body().string());
                return response.body()!!.string()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(str: String) {
            //結果をログに出力(レスポンスのbodyタグ内を出力する)
            Log.d("Debug", str)
        }
    }

    override fun onDestroy(){

        //ヘルパーオブジェクトの開放
        _helper.close()
        super.onDestroy()
    }

    fun goHome(){

        startActivity(Intent(this, NavigationActivity::class.java))
        finish()
    }

    /**
     * 位置情報関連の処理
     */
    //Update Result
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                if (location != null) {
                    lat = location.latitude.toString()
                    lng = location.longitude.toString()
                    Log.d("debug", "緯度" + lat)
                    Log.d("debug", "経度" + lng)
                }
            }
        }
    }
    //start locationUpdate
    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }

    private fun startLocationUpdates() {
        Log.d("debug","位置情報開始")
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback, null /* Looper */
        )
    }
    //stop locationUpdate
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}