package com.example.proj_memo_aos.ui.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.proj_memo_aos.R
import com.example.proj_memo_aos.data.model.BackgroundColorDataModel
import com.example.proj_memo_aos.databinding.FragmentSettingBinding
import com.example.proj_memo_aos.helper.BaseApplication
import com.example.proj_memo_aos.helper.Utils
import com.example.proj_memo_aos.ui.base.BaseFragment
import com.example.proj_memo_aos.viewmodel.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

//Hilt가 의존성을 주입해 줄 수 있도록 @AndroidEntryPoint 사용
@AndroidEntryPoint
class SettingFragment: BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting, "setting") {

    private val viewModel by viewModels<SettingViewModel>()

    private lateinit var settingsLauncher: ActivityResultLauncher<Intent>
    private var ignoreCheckedChange = false

    private lateinit var backgroundItemListAdapter: ArrayAdapter<String>
    private lateinit var shapeTop: GradientDrawable
    private lateinit var shapeMiddle: GradientDrawable
    private lateinit var shapeBottom: GradientDrawable

    override fun setInitialize() {
        setVariable()
        setListeners()
    }

    override fun changedBackgroundColor(backgroundColor: BackgroundColorDataModel) {
        // popup menu가 보여질 때 shape들의 색을 현재 설정된 backgroundColor.contentsColor 색으로 변경
        shapeTop.setColor(backgroundColor.contentsColor)
        shapeMiddle.setColor(backgroundColor.contentsColor)
        shapeBottom.setColor(backgroundColor.contentsColor)

        // 변경된 backgroundColor의 displayName을 가져와 colorSpinner에 표시
        val displayName = backgroundColor.displayName
        val index = backgroundItemListAdapter.getPosition(displayName)
        binding.colorSpinner.setSelection(index)
    }

    private fun setVariable() {
        // Databinding: ViewModel 동기화
        binding.viewModel = viewModel

        // BackgroundColorDataModel에 있는 모든 enum data의 displayName(String) 추가
        val items = mutableListOf<String>()
        for(color in BackgroundColorDataModel.entries) {
            items.add(color.displayName)

        }

        // adapter에 들어가는 View들은 Backcolor가 적용되지 않아 Backcolor에 따라 직접 변경해줌
        shapeTop = ContextCompat.getDrawable(requireContext(), R.drawable.setting_popup_top) as GradientDrawable //popup menu에 표시되는 최상위 아이템 모양
        shapeMiddle = ContextCompat.getDrawable(requireContext(), R.drawable.setting_popup_middle) as GradientDrawable //popup menu에 표시되는 중간 아이템 모양
        shapeBottom = ContextCompat.getDrawable(requireContext(), R.drawable.setting_popup_bottom) as GradientDrawable //popup menu에 표시되는 최하위 아이템 모양

        backgroundItemListAdapter = object : ArrayAdapter<String>(requireContext(), R.layout.spinner_dropdown_item, items) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)

                // 선택된 text가 보일 때 왼쪽 정렬 및 padding 0dp 적용
                if (view is TextView) {
                    view.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                    view.setPadding(0, 0, 0, 0)
                }
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)

                // popup menu가 보일 때 text 가운데 정렬 및 padding 12dp 적용
                if (view is TextView) {
                    view.gravity = Gravity.CENTER
                    val dp12 = Utils.dpToPx(resources, 12)
                    view.setPadding(dp12, dp12, dp12, dp12)
                }

                // position에 따라 view에 각 모양 부여
                val background = when (position) {
                    0 -> shapeTop
                    items.lastIndex -> shapeBottom
                    else -> shapeMiddle
                }

                view.background = background

                return view
            }
        }
        binding.colorSpinner.adapter = backgroundItemListAdapter

        // 현재 알림 권한 허용 여부에 따라 notificationSwitch 상태 설정
        binding.notificationSwitch.isChecked = viewModel.getNotificationPermissionIsAllowed()

        // PackageManager 클래스를 사용해 현재 앱의 버전을 Display
        val packageManager: PackageManager = requireActivity().packageManager
        val packageName = requireActivity().packageName // 앱의 패키지 이름
        try {
            // 앱의 PackageInfo를 가져옵니다
            val packageInfo = packageManager.getPackageInfo(packageName, 0)

            // 버전 이름 가져오기
            val versionName = packageInfo.versionName // versionName (String형)

            binding.versionTextView.text = versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        // back key 클릭시 종료
        setOnBackPressed { exitFragment() }
    }

    @SuppressLint("ClickableViewAccessibility", "PrivateResource")
    private fun setListeners() {
        binding.mainToolbar.apply {
            // Toolbar의 NavigationIcon을 abc_ic_ab_back_material(<-) 로 변경
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener {
                // 뒤로가기 클릭시 종료
                exitFragment()
            }
        }

        binding.appBarLayout.setExpanded(false)
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

        // background color popup menu의 아이템 선택시 선택한 색으로 배경색 변경
        binding.colorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position) as String
                changeBackgroundColor(BackgroundColorDataModel.fromDisplayName(selectedItem)!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        // 권한 설정을 위한 settingsLauncher 선언
        settingsLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            // 사용자가 권한설정을 마치고 앱으로 복귀했을 때 현재 앱에 알림 설정 권한 부여 여부를 보고 notificationSwitch를 다시 설정해줌
            // 이 때 ignoreCheckedChange를 두어 setOnCheckedChangeListener 이벤트가 발생하지 않도록 설계
            // 만약 setOnCheckedChangeListener 이벤트가 발생하면 다시 settingsLauncher 실행 됨
            ignoreCheckedChange = true
            binding.notificationSwitch.isChecked = viewModel.getNotificationPermissionIsAllowed()
            ignoreCheckedChange = false
        }

        // 알림 설정 권한 스위치가 변경되면 settingsLauncher를 통해 권한 설정 창을 띄워줌
        binding.notificationSwitch.setOnCheckedChangeListener { _, _ ->
            if (ignoreCheckedChange) return@setOnCheckedChangeListener

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = Intent().apply {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                }
                settingsLauncher.launch(intent)
            }
        }

        // 알림 권한 설정 관련 layout 설정
        setSettingLayoutClickEvent(binding.notificationLayout) {
            // 알림 권한 클릭시 알림 권한 스위치 상태값 반전
            binding.notificationSwitch.isChecked = !binding.notificationSwitch.isChecked
        }

        // 배경색 설정 관련 layout 설정
        setSettingLayoutClickEvent(binding.colorLayout) {
            // 배경색 설정 클릭시 colorSpinner를 누른것으로 처리하여 colorSpinner의 item group popup menu가 나오도록 함
            binding.colorSpinner.performClick()
        }

        // 앱 버전 관련 layout 설정
        setSettingLayoutClickEvent(binding.appVersionLayout) {
            // 앱 버전 클릭시 아무것도 하지 않음
        }

        // 개발자 정보 관련 layout 설정
        setSettingLayoutClickEvent(binding.developerInfoLayout) {
            // 개발자 정보 클릭시 개발자 정보 fragment로 이동
            val action = SettingFragmentDirections.actionSettingToDeveloperInfo()
            findNavController().navigate(action)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setSettingLayoutClickEvent(settingLayoutView: View, clickFunc: ()->Unit) {
        // 각 설정들을 터치 시 검정색으로 오버랩 되는 효과를 주기 위해 각 layout 모양별 GradientDrawable를 선언
        val shape = when(settingLayoutView.tag) {
            "setting_top" -> {
                ContextCompat.getDrawable(requireContext(), R.drawable.setting_top_overlay) as GradientDrawable
            }
            "setting_middle" -> {
                ContextCompat.getDrawable(requireContext(), R.drawable.setting_middle_overlay) as GradientDrawable
            }
            "setting_bottom" -> {
                ContextCompat.getDrawable(requireContext(), R.drawable.setting_bottom_overlay) as GradientDrawable
            }
            else -> {
                return
            }
        }

        // 초기 알파값 0 (선택 안되었을 때)
        shape.alpha = 0

        // outlineLayout layout의 foreground를 shape로 설정
        settingLayoutView.foreground = shape

        val childViewList = Utils.getAllChildViews(settingLayoutView, false)

        val layoutTouchListener = View.OnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // outlineLayout 어두워짐 애니메이션 재생
                    ObjectAnimator.ofInt(shape, "alpha", 0, 80).apply {
                        duration = 100
                        start()
                    }

                    // layout의 하위 view들에 작아지는 애니메이션 재생
                    childViewList.forEach { view ->
                        view.animate().scaleX(0.99f).scaleY(0.99f).setDuration(100).start()
                    }
                }

                MotionEvent.ACTION_UP -> {
                    // click 이벤트 실행
                    clickFunc()

                    // outlineLayout 밝아짐 애니메이션 재생
                    ObjectAnimator.ofInt(shape, "alpha", 80, 0).apply {
                        duration = 100
                        start()
                    }

                    // layout의 하위 view들에 원래대로 돌아오는 애니메이션 재생
                    childViewList.forEach { view ->
                        view.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    }
                }

                MotionEvent.ACTION_CANCEL -> {
                    //outlineLayout 밝아짐 애니메이션 재생
                    ObjectAnimator.ofInt(shape, "alpha", 80, 0).apply {
                        duration = 100
                        start()
                    }

                    // layout의 하위 view들에 원래대로 돌아오는 애니메이션 재생
                    childViewList.forEach { view ->
                        view.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    }
                }
            }
            true
        }

        // layout의 모든 하위 View에 layoutTouchListener 적용
        childViewList.forEach { view ->
            view.setOnTouchListener { v, event ->
                layoutTouchListener.onTouch(v, event)
                true
            }
        }
    }

    // 현재 fragment 종료
    private fun exitFragment() {
        findNavController().popBackStack()
    }
}