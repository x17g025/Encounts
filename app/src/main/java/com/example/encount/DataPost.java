package com.example.encount;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * POST通信(投稿)する時に利用するクラス
 * OkHttp3を使用
 */

public class DataPost extends AsyncTask<String,String,String> {


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    //jsonのサンプルデータ
    //String json = "{\"name\":\"名前\", \"taxis\":\"分類\"}";

    //送信するコメント内容の受け取り変数
    public static String mailadress = "";
    public static String password = "";
    String loginFlag = "";

    @Override
    protected String doInBackground(String... strings) {

        OkHttpClient client = new OkHttpClient();

        //アクセスするURL
        String url = "https://kinako.cf/api/login_check.php";

        //Map<String, String> formParamMap = new HashMap<>();
        //formParamMap.put("word", "abc");

        //Formを作成
        final FormBody.Builder formBuilder = new FormBody.Builder();

        //formParamMap.forEach(formBuilder::add);

        //formに要素を追加
        formBuilder.add("mail", mailadress);
        //リクエストの内容にformを追加
        RequestBody body = formBuilder.build();

        //RequestBody body = RequestBody.create(JSON, json);

        //リクエストを生成
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loginFlag;
    }

    @Override
    protected void onPostExecute(String loginFlag) {

    }
}
