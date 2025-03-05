package com.example.proj_memo_aos.ui.activity

import androidx.fragment.app.Fragment
import com.example.proj_memo_aos.R
import com.example.proj_memo_aos.databinding.ActivityMainBinding
import com.example.proj_memo_aos.ui.base.BaseActivity
import com.example.proj_memo_aos.ui.base.BaseFragment
import com.example.proj_memo_aos.viewmodel.MainActivityViewModel
import com.example.proj_memo_aos.viewmodel.MainFragmentViewModel

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val viewModel: MainActivityViewModel = MainActivityViewModel()

    override fun setInitialize(){
        setVariable()
    }

    private fun setVariable() {
        // Databinding: ViewModel 동기화
        binding.viewModel = viewModel
    }

    // Fragment가 변경됨에 따라 Current Fragment를 변경해 주기 위한 함수
    fun onFragmentChanged(fragment: Fragment) {
        // BaseFragment 타입 확인
        if (fragment is BaseFragment<*>) {
            // Current Fragment를 현재 동작중인 Fragment 이름으로 변경
            viewModel.changeDisplayText(fragment.fragmentName)
        }
    }
}