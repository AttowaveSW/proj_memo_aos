package com.example.proj_memo_aos.ui.fragment

import androidx.navigation.fragment.findNavController
import com.example.proj_memo_aos.R
import com.example.proj_memo_aos.databinding.FragmentSplashBinding
import com.example.proj_memo_aos.ui.base.BaseFragment

class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash, "Splash") {

    // SplashFragment에서 필요한 초기화 작업 진행
    // BaseFragement 클래스를 사용함에 따라 강제로 선언해주어야 하며 해당 함수는 onCreateView 단계에서 시행 됨 (BaseFragement 참고)
    override fun setInitialize(){

    }

    // SplashFragment에서 사용하는 Listener 정의
    // BaseFragement 클래스를 사용함에 따라 강제로 선언해주어야 하며 해당 함수는 onCreateView 단계에서 시행 됨 (BaseFragement 참고)
    override fun setListeners() {

        // move 버튼 클릭시 main fragment로 이동
        binding.moveMainButton.setOnClickListener {
            findNavController().navigate(R.id.actionSplashToMain, )
        }
    }
}