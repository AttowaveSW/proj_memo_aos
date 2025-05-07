package com.example.proj_memo_aos.ui.fragment

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.widget.TextView
import com.example.proj_memo_aos.R
import com.example.proj_memo_aos.databinding.FragmentMapBinding
import com.example.proj_memo_aos.ui.base.BaseFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlin.math.abs

class MapFragment: BaseFragment<FragmentMapBinding>(R.layout.fragment_map, "map"), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    override fun setInitialize() {
        setVariable()
        setListeners()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setVariable() {
        // OnMapReadyCallback을 상속받아 map 관련 이벤트를 처리할 수 있도록 함
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Scorll 이벤트가 map 터치 이벤트와 같이 동작하여 map 터치시 scroll을 막아주도록 설계
        binding.mapTouchLayer.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 맵 터치 시작: Scroll 막기
                    binding.scrollView.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // 터치 끝나면 Scroll 다시 허용
                    binding.scrollView.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }
    }

    private fun setListeners() {
        binding.mainToolbar.apply {
            //Toolbar NavigationIcon을 메뉴 아이콘으로 변경
            setNavigationIcon(R.drawable.baseline_menu_black_24dp)
            setNavigationOnClickListener {
                // 메뉴 아이콘 클릭시 Drawer open
                openDrawer()
            }
        }

        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            /* 확장된 상태를 viewModel에 저장
               memo editor 등 다른 framgent로 이동 후 돌와왔을 때 이동전의 확장상태를 유지해주기 위함
               onResume에서 fragment 이동전 확장상태로 다시 설정해줌
             */
            // 완전히 확장된 상태
            if (verticalOffset == 0) {

            }
            // 완전히 축소된 상태
            else if (abs(verticalOffset) >= appBarLayout.totalScrollRange) {

            }
            // 중간 상태
            else {

            }

            /* 스크롤 높이에 따라 최상단의 Text와 toolbar의 text의 투명도를 조절
               appBarLayout가 확장될 수록 최상단의 Text가 선명해지며 toolbar의 text가 흐려짐
               appBarLayout가 축소될 수록 최상단의 Text가 흐려지며 toolbar의 text가 선명해짐
             */
            val scrollRatio = 1f - (abs(verticalOffset).toFloat() / (appBarLayout.totalScrollRange.toFloat()) * 2)
            binding.topTextTitle.alpha = scrollRatio

            /* 기본 toolbar title 투명도 조절
               기본 toolbar의 title은 직접적인 참조가 불가능해 아래와 같이 mainToolbar의 childView를 검색하여
               title text view를 찾아 투명도를 조절해 줌
             */
            for (i in 0 until binding.mainToolbar.childCount) {
                val child = binding.mainToolbar.getChildAt(i)
                if (child is TextView && child.text == binding.mainToolbar.title) {
                    child.alpha = -scrollRatio
                    break
                }
            }
        }
    }

    // 맵이 정상적으로 로드 되면 불려지는 함수
    override fun onMapReady(googleMap: GoogleMap) {
        val newLatLng = LatLng(37.480351, 126.884130) // 아토웨이브 lat, lng 으로 설정
        val zoomLevel = 15f // zoom level 설정

        map = googleMap

        // 설정된 point로 이동 및 zoom 변경
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, zoomLevel))
    }
}