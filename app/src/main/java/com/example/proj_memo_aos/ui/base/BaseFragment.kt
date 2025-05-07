package com.example.proj_memo_aos.ui.base

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.proj_memo_aos.data.model.BackgroundColorDataModel
import com.example.proj_memo_aos.helper.BaseApplication
import com.example.proj_memo_aos.helper.SingleToast
import com.example.proj_memo_aos.helper.Utils

// BaseFragment추상 클래스를 사용하여 모든 Fragment에서 공통으로 사용하는 변수 및 함수 작성
// MainActivity에 현재 Fragment 이름 표시를 위해 fragmentName 추가
abstract class BaseFragment<VB : ViewDataBinding>(private val layoutResId: Int, var fragmentName: String) : Fragment() {

    protected lateinit var binding: VB
    lateinit var singleToast: SingleToast

    private var backPressedTime: Long = 0

    private val BACK_PRESSED_DURATION = 2_000L

    private var _rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container:ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (_rootView == null) {
            // Databinding 초기화 작업 진행
            binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
            binding.lifecycleOwner = this
            _rootView = binding.root
        }
        return _rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        singleToast = (requireActivity() as BaseActivity<*>).singleToast
        setOnBackPressed { defaultBackPressed() }

        // 현재 application의 backgorundColor를 받아옴
        val app = requireActivity().application as BaseApplication
        val loadBackgroundColor = app.backgroundColor.value!!

        // BaseFragment를 상속받아 사용하는 Activity에 정의된 setInitialize, setListeners 함수 실행
        setInitialize()

        // 앱에 설정된 background color로 배경색 설정
        setBackgroundColors(loadBackgroundColor)
        changedBackgroundColor(loadBackgroundColor)

        app.backgroundColor.observe(viewLifecycleOwner) { backgroundColor ->
            // 설정된 background color가 변경되면 activity의 모든 View의 background color를 변경
            setBackgroundColors(backgroundColor)
            changedBackgroundColor(backgroundColor)
        }
    }

    // background color 설정값을 변경해주는 함수
    fun changeBackgroundColor(backgroundColor: BackgroundColorDataModel) {
        val app = requireActivity().application as BaseApplication
        app.setBackgroundColor(backgroundColor)
    }

    fun defaultBackPressed() {
        if(System.currentTimeMillis() - backPressedTime >= BACK_PRESSED_DURATION) {
            backPressedTime = System.currentTimeMillis()
            singleToast.showMessage("한 번 더 누르면 종료됩니다.")
        } else {
            requireActivity().finish()
        }
    }

    fun setOnBackPressed(action: () -> Unit) {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            // activity에서 Drawer가 열려있다면 activity의 closeDrawer를 닫아준다.
            val activity = requireActivity()
            if (activity is BaseActivity<*> && activity.isDrawerOpen()) {
                activity.closeDrawer()
            } else {
                action()
            }
        }
    }

    fun openDrawer() {
        val activity = requireActivity()
        if (activity is BaseActivity<*>) {
            activity.openDrawer()
        }
    }

    // 모든 View의 background color를 설정하는 함수
    private fun setBackgroundColors(backgroundColor: BackgroundColorDataModel) {
        // tag가 background 및 contents로 선언되어있는 View들의 backgournd 색을 설정된 background color에 맞게 변경
        val allViews = Utils.getAllChildViews(binding.root, true)
        allViews.forEach { view ->
            when (view.tag) {
                "background" -> { view.background.setTint(backgroundColor.backColor) }
                "contents" -> { view.background.setTint(backgroundColor.contentsColor) }
                "button" -> {
                    if(view is ImageButton) {
                        ImageViewCompat.setImageTintList(view, ColorStateList.valueOf(backgroundColor.buttonColor))
                    }
                }
            }
        }
    }

    // Background 변경 시 동작하는 함수
    open fun changedBackgroundColor(backgroundColor: BackgroundColorDataModel) {

    }

    // setInitialize 함수를 강제하고 OnCreate 단계에서 실행시켜줌
    abstract fun setInitialize()
}