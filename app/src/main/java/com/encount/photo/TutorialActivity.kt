package com.encount.photo

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.stephentuso.welcome.*

class TutorialActivity : WelcomeActivity() {

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }*/
    companion object {
        /**
         * まだ表示していなかったらチュートリアルを表示
         * SharedPreferencesの管理に関しては内部でよしなにやってくれているので普通に呼ぶだけで良い
         */
        fun showIfNeeded(activity: Activity, savedInstanceState: Bundle?) {
            WelcomeHelper(activity, TutorialActivity::class.java).show(savedInstanceState)
        }

        /**
         * 強制的にチュートリアルを表示したい時にはこちらを呼ぶ
         */
        fun showForcibly(activity: Activity) {
            WelcomeHelper(activity, TutorialActivity::class.java).forceShow()
        }
    }

    /**
     * 表示するチュートリアル画面を定義する
     */
    override fun configuration(): WelcomeConfiguration {
        return WelcomeConfiguration.Builder(this)
            .defaultBackgroundColor(BackgroundColor(Color.BLUE))
            .page(TitlePage(R.mipmap.ic_launcher,
                "Encountへようこそ！"))
            .page(BasicPage(
                R.mipmap.ic_launcher,
                "新たな景色にエンカウント！",
                "いつもの道でも観光地でも、新たな風景に出会える！")
                .background(BackgroundColor(Color.BLUE)))
            .page(
                BasicPage(
                android.R.drawable.ic_btn_speak_now,
                "新たな観光地を生み出そう！",
                "良いなと思える景色があったら、写真を投稿して誰かに見てもらおう！")
                .background(BackgroundColor(Color.BLUE))
            )
            .page(
                BasicPage(
                    android.R.drawable.ic_btn_speak_now,
                    "さあ、はじめよう！",
                    "")
                    .background(BackgroundColor(Color.BLUE))
            )
            .swipeToDismiss(true)
            .build()
    }
}
