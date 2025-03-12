package com.example.proj_memo_aos.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.proj_memo_aos.helper.SingleToast

// BaseFragment추상 클래스를 사용하여 모든 Fragment에서 공통으로 사용하는 변수 및 함수 작성
// MainActivity에 현재 Fragment 이름 표시를 위해 fragmentName 추가
abstract class BaseFragment<VB : ViewDataBinding>(private val layoutResId: Int, var fragmentName: String) : Fragment() {

    protected lateinit var binding: VB
    private lateinit var singleToast: SingleToast

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressed()
        }
    }

    private var backPressedTime: Long = 0

    private val BACK_PRESSED_DURATION = 2_000L

    override fun onCreateView(
        inflater: LayoutInflater,
        container:ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Databinding 초기화 작업 진행
        binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        singleToast = (requireActivity() as BaseActivity<*>).singleToast
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)

        // BaseFragment를 상속받아 사용하는 Activity에 정의된 setInitialize, setListeners 함수 실행
        setInitialize()
    }

    override fun onDetach() {
        super.onDetach()
        backPressedCallback.remove()
    }

    open fun onBackPressed() {
        // 뒤로가기 버튼을 눌렀을 때 실행할 코드
        if(System.currentTimeMillis() - backPressedTime >= BACK_PRESSED_DURATION) {
            backPressedTime = System.currentTimeMillis()
            singleToast.showMessage("한 번 더 누르면 종료됩니다.")
        } else {
            requireActivity().finish()
        }
    }

    // setInitialize 함수를 강제하고 OnCreate 단계에서 실행시켜줌
    abstract fun setInitialize()
}