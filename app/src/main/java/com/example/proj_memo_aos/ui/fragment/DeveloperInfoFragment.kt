package com.example.proj_memo_aos.ui.fragment

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import androidx.navigation.fragment.findNavController
import com.example.proj_memo_aos.R
import com.example.proj_memo_aos.databinding.FragmentDeveloperInfoBinding
import com.example.proj_memo_aos.ui.base.BaseFragment

class DeveloperInfoFragment: BaseFragment<FragmentDeveloperInfoBinding>(R.layout.fragment_developer_info, "developerInfo") {

    override fun setInitialize() {
        setVariable()
        setListeners()

        setOnBackPressed { exitFragment() }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setVariable() {
        // webView의 Client 부여
        // webView에 "https://www.google.com" URL load
        binding.webView.webChromeClient = WebChromeClient()
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl("https://www.google.com")
    }

    @SuppressLint("PrivateResource")
    private fun setListeners() {
        binding.toolbar.apply {
            // Toolbar의 NavigationIcon을 abc_ic_ab_back_material(<-) 로 변경
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener {
                // 뒤로가기 클릭시 종료
                exitFragment()
            }
        }
    }

    // 현재 스텍을 제거하며 종료
    private fun exitFragment() {
        findNavController().popBackStack()
    }
}