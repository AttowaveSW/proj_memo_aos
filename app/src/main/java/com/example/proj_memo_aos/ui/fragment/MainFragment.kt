package com.example.proj_memo_aos.ui.fragment

import android.animation.Animator
import android.view.View
import com.example.proj_memo_aos.R
import com.example.proj_memo_aos.databinding.FragmentMainBinding
import com.example.proj_memo_aos.helper.SingleToast
import com.example.proj_memo_aos.ui.base.BaseFragment
import com.example.proj_memo_aos.viewmodel.MainFragmentViewModel

class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main, "Main") {

    private lateinit var toast: SingleToast

    // MainFragment에서 필요한 초기화 작업 진행
    // BaseFragement 클래스를 사용함에 따라 강제로 선언해주어야 하며 해당 함수는 onCreateView 단계에서 시행 됨 (BaseFragement 참고)
    override fun setInitialize(){

        // Databinding: ViewModel 동기화
        binding.viewModel = MainFragmentViewModel()

        toast = SingleToast(this.requireActivity())

        startAnimation()
    }

    // MainFragment에서 사용하는 Listener 정의
    // BaseFragement 클래스를 사용함에 따라 강제로 선언해주어야 하며 해당 함수는 onCreateView 단계에서 시행 됨 (BaseFragement 참고)
    override fun setListeners() {

        // clap의 AnimatorListener
        binding.clap.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                // Animation 시작될 때
                // Animation 재생 된 횟수를 Count 하기 위해 Count 값 증가
                binding.viewModel!!.incrementPlayCnt(1)
            }

            override fun onAnimationEnd(animation: Animator) {
                // Animation 종료될 때
                // 총 Lottie animation 보이지 않도록 하고 총 재생 Count Toast 메세지로 출력
                binding.clap.visibility = View.GONE
                toast.showMessage("Total play count:${binding.viewModel!!.animationCountDataModel.value!!.playCnt}")
            }

            override fun onAnimationCancel(animation: Animator) {
                // Animation 취소될 때
            }

            override fun onAnimationRepeat(animation: Animator) {
                // Animation 시작된 이후 반복될 때
                // Animation 재생 된 횟수를 Count 하기 위해 Count 값 증가
                binding.viewModel!!.incrementPlayCnt(1)
            }
        })
    }

    private fun startAnimation() {
        // 5번 반복하는 Animation 시행 (시작 1번, 반복 5번하여 총 6번)
        binding.clap.repeatCount = 5
        binding.clap.playAnimation()
    }
}