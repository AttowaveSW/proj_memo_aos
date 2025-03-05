package com.example.proj_memo_aos.ui.fragment

import android.animation.Animator
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import com.example.proj_memo_aos.R
import com.example.proj_memo_aos.databinding.FragmentMainBinding
import com.example.proj_memo_aos.helper.SingleToast
import com.example.proj_memo_aos.ui.base.BaseFragment
import com.example.proj_memo_aos.viewmodel.MainFragmentViewModel

class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main, "Main") {

    private val viewModel: MainFragmentViewModel = MainFragmentViewModel()

    private lateinit var toast: SingleToast
    private var backPressedTime: Long = 0

    private val BACK_PRESSED_DURATION = 2_000L

    // MainFragment에서 필요한 초기화 작업 진행
    // BaseFragment 클래스를 사용함에 따라 강제로 선언해 주어야 하며 해당 함수는 onCreateView 단계에서 시행 됨 (BaseFragment 참고)
    override fun setInitialize(){

        setVariable()
        setListeners()

        startAnimation()
    }

    private fun setVariable(){

        // Databinding: ViewModel 동기화
        binding.viewModel = viewModel

        toast = SingleToast(this.requireActivity())
    }

    private fun setListeners() {
        // clap의 AnimatorListener
        binding.clap.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                // Animation 시작될 때
                // Animation 재생 된 횟수를 Count 하기 위해 Count 값 증가
                viewModel.incrementPlayCnt(1)
            }

            override fun onAnimationEnd(animation: Animator) {
                // Animation 종료될 때
                // 총 Lottie animation 보이지 않도록 하고 총 재생 Count Toast 메세지로 출력
                binding.clap.visibility = View.GONE
                Log.d("MainFragment", "Total play count:${viewModel.animationCountDataModel.value?.playCnt}")
            }

            override fun onAnimationCancel(animation: Animator) {
                // Animation 취소될 때
            }

            override fun onAnimationRepeat(animation: Animator) {
                // Animation 시작된 이후 반복될 때
                // Animation 재생 된 횟수를 Count 하기 위해 Count 값 증가
                viewModel.incrementPlayCnt(1)
            }
        })

        // permissionState 데이터의 변화를 감지하여 퍼미션 상태 확인
        viewModel.animationCountDataModel.observe(viewLifecycleOwner) { animationCount ->
            Log.d("MainFragment", "changed play count:${animationCount.playCnt}")
        }

        // 뒤로가기 두 번 연속 입력시 종료
        requireActivity().onBackPressedDispatcher.addCallback {
            if(System.currentTimeMillis() - backPressedTime >= BACK_PRESSED_DURATION) {
                backPressedTime = System.currentTimeMillis()
                toast.showMessage("한 번 더 누르면 종료됩니다.")
            } else {
                requireActivity().finish()
            }
        }
    }

    private fun startAnimation() {
        // 0번 반복하는 Animation 시행 (시작 1번, 반복 5번하여 총 6번)
        binding.clap.repeatCount = 0
        binding.clap.playAnimation()
    }
}