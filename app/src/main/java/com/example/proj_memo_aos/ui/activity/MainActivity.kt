package com.example.proj_memo_aos.ui.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.proj_memo_aos.R
import com.example.proj_memo_aos.databinding.ActivityMainBinding
import com.example.proj_memo_aos.ui.base.BaseActivity
import com.example.proj_memo_aos.ui.fragment.MemoMainFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private var isDrawerReallyOpen = false

    override fun setInitialize() {
        setVariable()
        setListener()
    }

    // Drawer 상태를 나타내주는 함수 오버라이드
    override fun isDrawerOpen(): Boolean = isDrawerReallyOpen

    // Drawer를 닫아주는 함수 오버라이드
    override fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    // Drawer를 열어주는 함수 오버라이드
    override fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    private fun setVariable() {
        // Side 메뉴 보여질 때 현재 화면 >> 로 Slide 해주기
        binding.drawerLayout.setScrimColor(Color.parseColor("#33000000"))
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                binding.navHost.translationX = drawerView.width * slideOffset
            }

            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
        })

        // Side 메뉴에 시스템 상태바와 하단 네비게이션바 만큼 Margin 주기
        val statusBarResourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight = if (statusBarResourceId > 0) resources.getDimensionPixelSize(statusBarResourceId) else 0
        val bottomNavigationBarResourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val bottomNavigationBarHeight = if (bottomNavigationBarResourceId > 0) resources.getDimensionPixelSize(bottomNavigationBarResourceId) else 0

        val params = binding.navView.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin += statusBarHeight
        params.bottomMargin += bottomNavigationBarHeight
        binding.navView.layoutParams = params
    }

    private fun setListener() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHostFragment.navController

        // nav fragment의 현재 상태에 따라 drawer를 활성화 또는 비활성화 시킴
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.memoMain, R.id.map -> {
                    // fragment가 memoMain 및 map 일때 Drawer 잠금 해제
                    setDrawerUnlock()
                }
                else -> {
                    // 이외의 경우 Drawer 잠금
                    setDrawerLock()
                }
            }

            // fragment가 변경되면 열려있던 Drawer를 닫아줌
            binding.drawerLayout.closeDrawers()
        }

        // drawerLayout의 상태를 확인하기 위한 Listener 등록
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                // 열리는 중 or 닫히는 중
                isDrawerReallyOpen = slideOffset > 0f
            }

            override fun onDrawerOpened(drawerView: View) {
                isDrawerReallyOpen = true
            }

            override fun onDrawerClosed(drawerView: View) {
                isDrawerReallyOpen = false
            }

            override fun onDrawerStateChanged(newState: Int) {}
        })

        // setting 버튼을 누를 때 setting 화면으로 이동
        binding.settingButton.setOnClickListener {
            val action = MemoMainFragmentDirections.actionGoToSetting()
            navController.navigate(action)
        }

        // 현재 선택된 Tab을 표시하기 위한 shape 선언
        val shapeMemo = ContextCompat.getDrawable(this, R.drawable.menu_radio_button_overlay) as GradientDrawable
        val shapeMap = ContextCompat.getDrawable(this, R.drawable.menu_radio_button_overlay) as GradientDrawable
        val defaultAlpha = 15

        shapeMemo.alpha = defaultAlpha
        shapeMap.alpha = 0

        binding.mainNavigationMemo.foreground = shapeMemo
        binding.mainNavigationMemo.setOnCheckedChangeListener { _, isChecked ->
            // 메모장 라디오 버튼이 checked 이면 main memo fragment로 이동
            if (isChecked) {
                // 메모장 화면은 항상 최하위 스텍에 존재하도록 설계되어 현재 네비게이션 스텍을 없애며 뒤로가면 메모장 화면이 표시됨
                navController.popBackStack()

                // 메모장 버튼에 해당하는 shape를 오버랩 하여 선택된 것처럼 보이도록 함
                // 나머지 버튼은 알파값을 0으로 하여 선택되지 않은것 처럼 보이도록 함
                shapeMemo.alpha = defaultAlpha
                shapeMap.alpha = 0
            }
        }

        binding.mainNavigationMap.foreground = shapeMap
        binding.mainNavigationMap.setOnCheckedChangeListener { _, isChecked ->
            // 메모장 지도 버튼이 checked 이면 map fragment로 이동
            if (isChecked) {
                navController.navigateSingleTop(R.id.map)

                // 지도 버튼에 해당하는 shape를 오버랩 하여 선택된 것처럼 보이도록 함
                // 나머지 버튼은 알파값을 0으로 하여 선택되지 않은것 처럼 보이도록 함
                shapeMemo.alpha = 0
                shapeMap.alpha = defaultAlpha
            }
        }
    }

    private fun NavController.navigateSingleTop(destinationId: Int) {
        // NavController에 추가 함수 선언
        // navigateSingleTop으로 이동 시 R.id.memoMain를 제외한 모든 fragment 스텍들 삭제
        this.navigate(
            destinationId,
            null,
            NavOptions.Builder()
                .setPopUpTo(R.id.memoMain, false) // memoMain 이전 스텍 제거
                .build()
        )
    }

    // Drawer를 비활성화 해주는 함수
    fun setDrawerLock() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    // Drawer를 활성화 해주는 함수
    fun setDrawerUnlock() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }
}