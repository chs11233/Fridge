package com.example.fridge2

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "2f2c2cfe9a270bd33421cd72d001211d")
    }
}