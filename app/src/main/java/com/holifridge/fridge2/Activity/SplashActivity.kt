package com.holifridge.fridge2.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.airbnb.lottie.LottieAnimationView
import com.holifridge.fridge2.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private var mBinding: ActivitySplashBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var lodingImage = binding.loadingImage as LottieAnimationView

        lodingImage.playAnimation()
        lodingImage.loop(true)

        val handler: Handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        },2000)

    }
}