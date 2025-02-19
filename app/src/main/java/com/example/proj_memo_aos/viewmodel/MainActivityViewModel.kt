package com.example.proj_memo_aos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.proj_memo_aos.data.model.MainScreenDataModel

class MainActivityViewModel{

    // MainActivity에 관련된 Model을 LiveData로 선언
    private val _mainScreenDataModel = MutableLiveData<MainScreenDataModel>()
    val mainScreenDataModel: LiveData<MainScreenDataModel> = _mainScreenDataModel

    init {
        // MainScreenData Model 초기화
        _mainScreenDataModel.value = MainScreenDataModel()
    }

    fun changeDisplayText(mainScreen: String) {
        // currentFragment 변경 (Databinding과 LiveData를 사용하여 변경 즉시 UI에 적용시켜줌)
        _mainScreenDataModel.value = _mainScreenDataModel.value?.copy(currentFragment = mainScreen)
    }
}