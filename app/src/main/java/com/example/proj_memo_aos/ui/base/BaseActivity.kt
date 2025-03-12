package com.example.proj_memo_aos.ui.base

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.proj_memo_aos.helper.SingleToast

// BaseActivity추상 클래스를 사용하여 모든 Activity에서 공통으로 사용하는 변수 및 함수 작성
abstract class BaseActivity<VB : ViewDataBinding>(private val layoutResId: Int) : AppCompatActivity() {

    protected lateinit var binding: VB

    lateinit var singleToast: SingleToast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Databinding 초기화 작업 진행
        binding = DataBindingUtil.setContentView(this, layoutResId)
        binding.lifecycleOwner = this

        singleToast = SingleToast(this)

        // BaseActivity를 상속받아 사용하는 Activity에 정의된 setInitialize, setListeners 함수 실행
        setInitialize()
    }

    // setInitialize 함수를 강제하고 OnCreate 단계에서 실행시켜줌
    abstract fun setInitialize()
}