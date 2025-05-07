package com.example.proj_memo_aos.helper

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.proj_memo_aos.data.model.BackgroundColorDataModel
import com.example.proj_memo_aos.data.sharedpref.SharedPreferencesMemoRepository
import dagger.hilt.android.HiltAndroidApp

//Hilt 라이브러리를 사용하기 위해 BaseApplication 사용
@HiltAndroidApp
class BaseApplication : Application() {
    private lateinit var sharedPrefsRepo: SharedPreferencesMemoRepository
    lateinit var backgroundColor: MutableLiveData<BackgroundColorDataModel>

    override fun onCreate() {
        super.onCreate()

        // SharedPreferencesMemoRepository를 읽어옴
        sharedPrefsRepo = SharedPreferencesMemoRepository(this)

        // SharedPreferencesMemoRepository에서 BackgroundColor를 읽어옴
        val loadedBackgroundColor = sharedPrefsRepo.loadBackgroundColor()

        // 읽어온 BackgroundColor를 backgroundColor에 적용, 만약 BackgroundColor이 없다면 defaultBackgroundColor(YELLOW_CREAM) 으로 적용
        val defaultBackgroundColor = BackgroundColorDataModel.getDefaultColor()
        backgroundColor = MutableLiveData(loadedBackgroundColor ?: defaultBackgroundColor)
    }

    // BackgroundColor 설정값 변경 함수
    fun setBackgroundColor(color: BackgroundColorDataModel) {
        // 각 Activity 및 Fragment에 값을 뿌려주기 위해 postValue로 BackgroundColor값 변경
        backgroundColor.postValue(color)

        // 변경된 BackgroundColor값 SharedPreferencesMemoRepository에 저장
        sharedPrefsRepo.saveBackgroundColor(color)
    }
}