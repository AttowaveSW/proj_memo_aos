package com.example.proj_memo_aos.helper

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

//Hilt 라이브러리를 사용하기 위해 BaseApplication 사용
@HiltAndroidApp
class BaseApplication : Application() {

}