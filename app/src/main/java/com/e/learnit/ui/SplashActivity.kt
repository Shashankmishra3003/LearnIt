package com.e.learnit.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import com.e.learnit.R

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT:Long=2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)

        Handler().postDelayed({
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        },SPLASH_TIME_OUT)
    }
}
