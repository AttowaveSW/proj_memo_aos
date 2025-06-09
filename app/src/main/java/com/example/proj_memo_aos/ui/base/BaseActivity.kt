package com.example.proj_memo_aos.ui.base

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.proj_memo_aos.data.model.BackgroundColorDataModel
import com.example.proj_memo_aos.helper.BaseApplication
import com.example.proj_memo_aos.helper.SingleToast
import com.example.proj_memo_aos.helper.Utils

// BaseActivity추상 클래스를 사용하여 모든 Activity에서 공통으로 사용하는 변수 및 함수 작성
abstract class BaseActivity<VB : ViewDataBinding>(private val layoutResId: Int) : AppCompatActivity() {

    lateinit var binding: VB

    lateinit var singleToast: SingleToast

    private lateinit var backgroundColor: BackgroundColorDataModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = true
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // Databinding 초기화 작업 진행
        binding = DataBindingUtil.setContentView(this, layoutResId)
        binding.lifecycleOwner = this

        singleToast = SingleToast(this)

        // 현재 application의 backgorundColor를 받아옴
        val app = application as BaseApplication
        backgroundColor = app.backgroundColor.value!!

        // BaseActivity를 상속받아 사용하는 Activity에 정의된 setInitialize, setListeners 함수 실행
        setInitialize()

        // 앱에 설정된 background color로 배경색 설정
        setBackgroundColors(backgroundColor)
        changedBackgroundColor(backgroundColor)

        app.backgroundColor.observe(this) { backgroundColor ->
            // 설정된 background color가 변경되면 activity의 모든 View의 background color를 변경
            this.backgroundColor = backgroundColor
            setBackgroundColors(backgroundColor)
            changedBackgroundColor(backgroundColor)
        }
    }

    // background color 설정값을 변경해주는 함수
    fun changeBackgroundColor(backgroundColor: BackgroundColorDataModel) {
        val app = application as BaseApplication
        app.setBackgroundColor(backgroundColor)
    }

    // 모든 View의 background color를 설정하는 함수
    private fun setBackgroundColors(backgroundColor: BackgroundColorDataModel) {
        backgroundColor.setTagColor(binding.root)
    }

    // Background 변경 시 동작하는 함수
    open fun changedBackgroundColor(backgroundColor: BackgroundColorDataModel) {

    }

    // setInitialize 함수를 강제하고 OnCreate 단계에서 실행시켜줌
    abstract fun setInitialize()

    // overrideDrawer 제어 관련 함수
    open fun isDrawerOpen(): Boolean = false
    open fun closeDrawer() {}
    open fun openDrawer() {}
}