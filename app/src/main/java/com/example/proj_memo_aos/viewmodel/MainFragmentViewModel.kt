package com.example.proj_memo_aos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proj_memo_aos.data.model.AnimationCountDataModel

class MainFragmentViewModel: ViewModel() {

    // MainFragment에 관련된 Model을 LiveData로 선언
    private val _animationCountDataModel = MutableLiveData<AnimationCountDataModel>()
    val animationCountDataModel: LiveData<AnimationCountDataModel> = _animationCountDataModel

    init {
        // AnimationCountDataModel Model 초기화
        _animationCountDataModel.value = AnimationCountDataModel()
    }

    fun incrementPlayCnt(increaseNum: Int) {
        // increaseNum 만큼 PlayCnt 증가 (Databinding과 LiveData를 사용하여 변경 즉시 UI에 적용시켜줌)
        _animationCountDataModel.value = _animationCountDataModel.value?.copy(playCnt = (_animationCountDataModel.value?.playCnt ?: 0) + increaseNum)
    }
}