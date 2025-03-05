package com.example.proj_memo_aos.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.proj_memo_aos.ui.activity.MainActivity

// BaseFragment추상 클래스를 사용하여 모든 Fragment에서 공통으로 사용하는 변수 및 함수 작성
// MainActivity에 현재 Fragment 이름 표시를 위해 fragmentName 추가
abstract class BaseFragment<VB : ViewDataBinding>(private val layoutResId: Int, var fragmentName: String) : Fragment() {

    protected lateinit var binding: VB

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

        // BaseFragment를 상속받아 사용하는 Activity에 정의된 setInitialize, setListeners 함수 실행
        setInitialize()
    }

    override fun onResume() {
        super.onResume()

        // 동작 시 현재 동작하는 Fragment를 부모 Activity에게 전달
        // 부모 Activity에 Display중인 화면을 표기하기 위함
        (activity as? MainActivity)?.onFragmentChanged(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    // setInitialize 함수를 강제하고 OnCreate 단계에서 실행시켜줌
    abstract fun setInitialize()
}