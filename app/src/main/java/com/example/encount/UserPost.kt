package com.example.encount

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.text.SimpleDateFormat
import java.util.Date
import android.os.AsyncTask
import android.util.Log
import android.widget.*
import okhttp3.*
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * 投稿機能(カメラ、コメント、位置情報)
 * 制作者：大野
 */

class UserPost : AppCompatActivity() {

    val _helper = SQLiteHelper(this@UserPost)

    //写真のパスを受け取る変数(将来的には撮影した写真のパス、ファイル名を取得して指定する)
    var uurl = ""
    //緯度
    var latitude = ""
    //経度
    var longitude = ""
    //送信するコメント内容の受け取り変数
    var cmnt = ""

    /**
     * 保存された画像のURI
     */
    private var _imageUri: Uri? = null

    /**
     * 緯度フィールド
     */

    private var _latitude = 0.0
    /**
     * 経度フィールド
     */
    private var _longitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_post)

        //画面遷移用
        val menuHomeBtn     = findViewById<LinearLayout>(R.id.MenuHome)
        val menuUserBtn = findViewById<LinearLayout>(R.id.MenuUser)

        /**
         * 位置情報取得
         */
        //LocationManagerオブジェクトを取得。
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //位置情報が更新された際のリスナオブジェクトを生成。
        val locationListener = GPSLocationListener()
        //ACCESS_FINE_LOCATIONの許可が下りていないなら…
        if(ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //ACCESS_FINE_LOCATIONの許可を求めるダイアログを表示。その際、リクエストコードを1000に設定。
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this@UserPost, permissions, 1000)
            //onCreate()メソッドを終了。
            return
        }
        //位置情報の追跡を開始。
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)

        /**
         * 投稿処理
         * 投稿ボタン押すと動作する
         */
        // 投稿ボタン、コメント取得
        val postButton = findViewById<Button>(R.id.postButton)
        val commentInput = findViewById<EditText>(R.id.commentInput)


        // 投稿ボタンが押された時
        postButton.setOnClickListener {

            //パスの処理
            val uuri = getFileSchemeUri(_imageUri as Uri)
            println(uuri.toString())
            //OkHttpPost.uurl = uuri.toString()
            var pass = uuri.toString().substring(uuri.toString().length - 17)
            print(pass)
            uurl = pass

            //コメントをEditTextから取得
            cmnt = commentInput.getText().toString()
            //緯度を取得
            latitude = _latitude.toString()
            //経度を取得
            longitude = _longitude.toString()

            //ここで現在地取得処理(更新)を終了させる
            print("GPS終了")
            locationManager.removeUpdates(locationListener)

            //投稿処理開始
            val postTask = OkHttpPost()
            postTask.execute(/*uuri.toString()*/)

            startActivity(Intent(this, UserHome::class.java))
        }

        //メニューバーを押した場合の処理
        menuUserBtn.setOnClickListener {

            startActivity(Intent(this, UserProfile::class.java))
            overridePendingTransition(0, 0)
        }

        menuHomeBtn.setOnClickListener {

            startActivity(Intent(this, UserHome::class.java))
            overridePendingTransition(0, 0)
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
            //フィールドの画像URIをImageViewに設定。
            ivCamera.setImageURI(_imageUri)

            //デバッグ用
            System.out.println("変換前"+_imageUri)

            /**
             * ここで一番下のメソッドを利用して、パスを取得する
             */
            val uuri = getFileSchemeUri(_imageUri as Uri)
            println("変換後："+uuri.toString())

            //位置情報の更新作業をここで終了させる
            //locationManager.removeUpdates(this)

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        //追加
        //ACCESS_FINE_LOCATIONに対するパーミションダイアログでかつ許可を選択したなら…
        if(requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //LocationManagerオブジェクトを取得。
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            //位置情報が更新された際のリスナオブジェクトを生成。
            val locationListener = GPSLocationListener()
            //再度ACCESS_FINE_LOCATIONの許可が下りていないかどうかのチェックをし、降りていないなら処理を中止。
            if(ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            //位置情報の追跡を開始。
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
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
    }

    /**
     * ロケーションリスナクラス。
     */
    private inner class GPSLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            //引数のLocationオブジェクトから緯度を取得。
            _latitude = location.latitude
            //引数のLocationオブジェクトから経度を取得。
            _longitude = location.longitude
            //取得した緯度をTextViewに表示。
            val tvLatitude = findViewById<TextView>(R.id.tvLatitude)
            tvLatitude.text = _latitude.toString()
            //取得した経度をTextViewに表示。
            val tvLongitude = findViewById<TextView>(R.id.tvLongitude)
            tvLongitude.text = _longitude.toString()
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}
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
            val url = "https://kinako.cf/encount/PostPhoto.php"

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

            //ここでPOSTする内容を設定　"image/jpg"の部分は送りたいファイルの形式に合わせて変更する
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userId", id)
                .addFormDataPart("longitude", longitude)
                .addFormDataPart("latitude", latitude)
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
}
