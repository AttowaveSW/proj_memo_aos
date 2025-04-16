package com.example.proj_memo_aos.ui.fragment

import android.content.Context
import android.os.Build
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.proj_memo_aos.R
import com.example.proj_memo_aos.data.model.MemoDataModel
import com.example.proj_memo_aos.databinding.FragmentMemoMainBinding
import com.example.proj_memo_aos.helper.MemoAppDialog
import com.example.proj_memo_aos.helper.MemoClickType
import com.example.proj_memo_aos.helper.MemoEditorResult
import com.example.proj_memo_aos.helper.OnItemSelectedCountChange
import com.example.proj_memo_aos.helper.OnMemoClick
import com.example.proj_memo_aos.helper.OnSelectionModeChange
import com.example.proj_memo_aos.ui.adapter.MemoAdapter
import com.example.proj_memo_aos.ui.base.BaseFragment
import com.example.proj_memo_aos.viewmodel.MemoMainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

//Hilt가 의존성을 주입해 줄 수 있도록 @AndroidEntryPoint 사용
@AndroidEntryPoint
class MemoMainFragment: BaseFragment<FragmentMemoMainBinding>(R.layout.fragment_memo_main, "MemoEditor") {

    private val viewModel by viewModels<MemoMainViewModel>()

    private lateinit var adapter: MemoAdapter
    private var ignoreSelectAllChange = false

    override fun setInitialize() {
        setVariable()
        setListeners()
    }

    override fun onResume() {
        super.onResume()
        //다른 Fragment에서 돌아올 때 Scroll, recyclerView 보기방식, 검색모드를 유지하기 위해 onResume동작시 다시 set 해줌
        binding.appBarLayout.setExpanded(viewModel.appBarIsExpanded, false)
        changeMemoSpanCount(viewModel.recyclerViewSpanCount)
        if (viewModel.isSearchMode) {
            enableSearchMode(viewModel.stringToSearch.value)
        }
    }

    override fun onBackPressed() {
        /* 뒤로가기 키 눌렸을 때
           편집모드(selection mode)일 경우 편집모드 종료
           검색모드(search mode)일 경우 검색모드 종료
           둘 다 아닐 경우 기존 Back key 동작 유지
         */
        if (adapter.isSelectionMode) {
            adapter.exitToSelectionMode()
        } else if (viewModel.isSearchMode) {
            disableSearchMode()
        } else {
            super.onBackPressed()
        }
    }

    private fun setVariable(){
        //Databinding: ViewModel 동기화
        binding.viewModel = viewModel

        //MemoEditorFragment에서 보내주는 Data를 Parsing
        parentFragmentManager.setFragmentResultListener("memoEditorResult", viewLifecycleOwner) { _, bundle ->
            val memoEditorResult: MemoEditorResult?
            val memo: MemoDataModel?

            //안드로이드 버전에 맞게 Parcelable 처리
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                memoEditorResult = bundle.getParcelable("editorResult", MemoEditorResult::class.java)
                memo = bundle.getParcelable("memo", MemoDataModel::class.java)
            } else {
                @Suppress("DEPRECATION")
                memoEditorResult = bundle.getParcelable("editorResult") as? MemoEditorResult
                memo = bundle.getParcelable("memo") as? MemoDataModel
            }

            //memoEditorResult에 따라 수정된 메모로 저장, 저장하지 않음, 삭제 동작 수행
            if (memo != null) {
                when (memoEditorResult) {
                    MemoEditorResult.SaveToMemo -> viewModel.updateMemo(memo)
                    MemoEditorResult.Delete -> viewModel.removeMemo(memo)
                    MemoEditorResult.DoNothing -> {}
                    null -> {}
                }
            }
        }

        //MemoList를 표기할 adapter 선언
        adapter = MemoAdapter(binding.recyclerviewMemo,
            arrayListOf(),
            object : OnMemoClick {
                override fun onMemoClick(memo: MemoDataModel, memoClickType: MemoClickType) {
                    /* recyclerView의 item 클릭 이벤트 리스너
                       해당 Key 이벤트는 편집모드(selection mode)일 경우 동작하지 않음
                     */
                    onMemoClickInMainFragment(memo, memoClickType)
                }
            },
            object : OnSelectionModeChange {
                override fun onSelectionModeChange(isSelectionMode: Boolean) {
                    /* recyclerView가 검색모드로 변경되는 이벤트 리스너
                       검색모드로 변경되면 검색모드에 맞는 UI로 설정
                     */
                    setSelectionMode(isSelectionMode)
                }
            },
            object : OnItemSelectedCountChange {
                override fun onItemSelectedCountChange(count: Int) {
                    /* recyclerView가 검색모드일 때 선택된 item의 개수가 변경되는 이벤트 리스너
                       item 개수에 따라 toolbar의 text 변경
                       item이 모두 선택되었다면 전체 선택 checkbox를 checked 상태로 아니면 unchecked 상태로 설정
                     */
                    if(viewModel.isSelectionMode) {
                        if (count > 0) {
                            binding.topTextTitle.text = "${count}개 선택됨"
                            binding.selectionModeToolbarTitle.text = "${count}개 선택됨"
                        } else {
                            binding.topTextTitle.text = "노트선택"
                            binding.selectionModeToolbarTitle.text = "노트선택"
                        }

                        ignoreSelectAllChange = true
                        if(count == adapter.itemCount) {
                            binding.selectAllButton.isChecked = true
                        } else {
                            binding.selectAllButton.isChecked = false
                        }
                        ignoreSelectAllChange = false
                    }
                }
            })
        binding.recyclerviewMemo.adapter = adapter
    }

    private fun setListeners() {
        //add Button Click시 빈 Memo Editor로 이동
        binding.addMemoBtn.setOnClickListener {
            val newMemo = MemoDataModel()
            showMemoEditor(newMemo)
        }

        //del Button Click시 recyclerView에서 선택된 item을 가져와 삭제여부를 묻고 삭제
        binding.delMemoBtn.setOnClickListener {
            adapter.getSelectedMemoList()?.let { showDeleteDialog(it) }
        }

        /* 선택모드일 때 노출되는 toolbar의 전체선택 layout click 이벤트
           해당 layout이 눌렸을 때 selectAllButton의 check 상태를 반전시킴
           selectAllButton 구현시 text를 아래에 배치하고 싶어 layout으로 감싸 textview를 추가함
           아래에 배치된 textview 터치시에도 selectAllButton 이벤트를 실행하고 싶어
           해당 layout 터치시 selectAllButton의 check 상태를 반전 시키도록 구현
        */
        binding.selectAllButtonLayout.setOnClickListener {
            binding.selectAllButton.isChecked = !binding.selectAllButton.isChecked
        }

        /* selectAllButton 의 check 상태가 변경되었을 때 check 상태에 따라
           recyclerView의 모든 item을 check 또는 uncheck 상태가 될 수 있도록 구현
           ignoreSelectAllChange가 true일 때에는 동작하지 않도록 함
           (onItemSelectedCountChange에서 selectAllButton의 check 상태를 변경할 때에는 동작하지 않도록 함)
        */
        binding.selectAllButton.setOnCheckedChangeListener { _, isCheck->
            if (ignoreSelectAllChange) {
                return@setOnCheckedChangeListener
            }

            if (isCheck) {
                adapter.selectAll()
            } else {
                adapter.unSelectAll()
            }
        }

        //memoListToDisplay 변경이 감지되면 memo list adapter 업데이트
        viewModel.memoListToDisplay.observe(this) { memoList ->
            adapter.updateData(memoList)
        }

        binding.mainToolbar.apply {
            //Toolbar NavigationIcon을 메뉴 아이콘으로 변경
            setNavigationIcon(R.drawable.baseline_menu_black_24dp)
            setNavigationOnClickListener {
                //TBD
            }

            //Toolbar 메뉴 List item 대한 ClickListener
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.editMode -> {
                        //선택 클릭시 선택모드로 진입
                        adapter.goToSelectionMode()
                        true
                    }
                    R.id.menoSearch -> {
                        //검색 icon 클릭시 검색모드로 진입
                        enableSearchMode("")
                        true
                    }
                    R.id.changeViewType -> {
                        true
                    }
                    R.id.viewTypeRow1 -> {
                        //보기방식을 목록으로 변경
                        changeMemoSpanCount(1)
                        true
                    }
                    R.id.viewTypeRow2 -> {
                        //보기방식을 큰 격자로 변경
                        changeMemoSpanCount(2)
                        true
                    }
                    R.id.viewTypeRow3 -> {
                        //보기방식을 작은 격자로 변경
                        changeMemoSpanCount(3)
                        true
                    }
                    R.id.allDelete -> {
                        //모두삭제 클릭시 삭제 여부를 물어보며 삭제 진행
                        showDeleteAllDialog()
                        true
                    }
                    else -> false
                }
            }
        }

        binding.searchModeToolbar.apply {
            //Toolbar NavigationIcon을 뒤로가기 아이콘으로 변경
            setNavigationIcon(R.drawable.baseline_arrow_back_ios_new_24)
            setNavigationOnClickListener {
                //뒤로가기 아이콘 클릭시 검색모드 중단
                disableSearchMode()
            }

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.searchReset -> {
                        //X icon 클릭시 검색 keyword 초기화
                        viewModel.stringToSearch.postValue("")
                        true
                    }
                    else -> false
                }
            }
        }

        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            /* 확장된 상태를 viewModel에 저장
               memo editor 등 다른 framgent로 이동 후 돌와왔을 때 이동전의 확장상태를 유지해주기 위함
               onResume에서 fragment 이동전 확장상태로 다시 설정해줌
             */
            // 완전히 확장된 상태
            if (verticalOffset == 0) {
                viewModel.appBarIsExpanded = true
            }
            // 완전히 축소된 상태
            else if (abs(verticalOffset) >= appBarLayout.totalScrollRange) {
                viewModel.appBarIsExpanded = false
            }
            // 중간 상태
            else {
                viewModel.appBarIsExpanded = false
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

            /* selection mode toolbar title 투명도 조절
               선택모드 툴바는 toolbar가 아닌 커스텀한 layout으로 구성되어있음
             */
            binding.selectionModeToolbarTitle.alpha = -scrollRatio
        }
    }

    // 선택모드를 설정해주는 함수
    private fun setSelectionMode(isSelectionMode: Boolean) {
        viewModel.isSelectionMode = isSelectionMode
        if (isSelectionMode) {
            /* 선택모드 enable
               selectionModeToolbar의 visibility를 제어하여 기존 toolbar위에 오버랩핑 됨
               선택모드로 memo 추가 버튼이 사라지며 삭제 버튼이 나타나는 애니메이션 출력
             */
            binding.selectionModeToolbar.visibility = View.VISIBLE
            binding.topTextTitle.text = "노트선택"
            binding.selectionModeToolbarTitle.text = "노트선택"
            setSlideButton(true)
        } else {
            /* 선택모드 disable
               selectionModeToolbar의 visibility를 제어하여 기존 toolbar 보여짐
               선택모드로 memo 삭제 버튼이 사라지며 추가 버튼이 나타나는 애니메이션 출력
             */
            binding.selectionModeToolbar.visibility = View.GONE
            binding.topTextTitle.text = "메모장"
            binding.selectAllButton.isChecked = false
            setSlideButton(false)
        }
    }

    // isDeleteMode에 따라 memo 추가 버튼이, 삭제 버튼이 나타나거나 없어지는 애니메이션 출력 함수
    private fun setSlideButton(isDeleteMode: Boolean) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.mainLayout)

        fun dpToPx(dp: Int): Int {
            return (dp * resources.displayMetrics.density).toInt()
        }

        if (isDeleteMode) {
            constraintSet.clear(R.id.delMemoBtn, ConstraintSet.START)
            constraintSet.clear(R.id.addMemoBtn, ConstraintSet.END)
            constraintSet.connect(
                R.id.delMemoBtn,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )
            constraintSet.connect(
                R.id.addMemoBtn,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )
            constraintSet.setMargin(R.id.delMemoBtn, ConstraintSet.END, dpToPx(15))
            constraintSet.setMargin(R.id.addMemoBtn, ConstraintSet.START, dpToPx(15))
        } else {
            constraintSet.clear(R.id.delMemoBtn, ConstraintSet.END)
            constraintSet.clear(R.id.addMemoBtn, ConstraintSet.START)
            constraintSet.connect(
                R.id.delMemoBtn,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )
            constraintSet.connect(
                R.id.addMemoBtn,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )
            constraintSet.setMargin(R.id.delMemoBtn, ConstraintSet.START, dpToPx(15))
            constraintSet.setMargin(R.id.addMemoBtn, ConstraintSet.END, dpToPx(15))
        }

        TransitionManager.beginDelayedTransition(binding.mainLayout, AutoTransition().apply {
            duration = 300
        })
        constraintSet.applyTo(binding.mainLayout)
    }

    private fun enableSearchMode(initKeyword: String?) {
        /* 검색모드 enable
           appBarLayout을 축소
           searchModeToolbar의 visibility를 제어하여 기존 toolbar위에 오버랩핑 됨
           기본 소프트 키보드를 강제로 띄워줌
           검색 editText에 포커싱울 줌
         */
        binding.appBarLayout.setExpanded(false)
        binding.searchModeToolbar.visibility = View.VISIBLE
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.searchText.requestFocus()
        imm.showSoftInput(binding.searchText, InputMethodManager.SHOW_IMPLICIT)
        viewModel.enableSearchMode(initKeyword)
    }

    private fun disableSearchMode() {
        /* 검색모드 disable
           searchModeToolbar의 visibility를 제어하여 기존 toolbar 보여짐
         */
        binding.searchModeToolbar.visibility = View.GONE
        viewModel.disableSearchMode()
    }

    //각 Memo 객체를 눌렀을 때의 처리
    private fun onMemoClickInMainFragment(memo: MemoDataModel, memoClickType: MemoClickType) {
        when (memoClickType) {
            //짧게 눌렀을 때 Memo Editor로 이동
            MemoClickType.ClickBody -> {
                showMemoEditor(memo)
            }
            //길게 눌렀을 때
            MemoClickType.LongClick -> {

            }
        }
    }

    //Memo Editor로 이동하는 함수
    private fun showMemoEditor(memo: MemoDataModel) {
        val action = MemoMainFragmentDirections.actionMemoMainToMemoEditor(memo)
        findNavController().navigate(action)
    }

    // 보기방식을 변경해주는 함수
    private fun changeMemoSpanCount(count: Int) {
        val layoutManager = binding.recyclerviewMemo.layoutManager
        if (layoutManager is GridLayoutManager) {
            if (layoutManager.spanCount != count) {
                adapter.setViewType(count != 1)
                layoutManager.spanCount = count
                layoutManager.requestLayout()
                viewModel.changeRecyclerViewSpanCount(count)
            }
        }
    }

    // 삭제 여부를 묻는 AlertDialog을 띄워주는 함수
    private fun showDeleteDialog(memoList: List<MemoDataModel>) {
        MemoAppDialog.Builder(requireContext())
            .setTitle("")
            .setMessage("${memoList.count()}개의 메모를 삭제 하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                viewModel.removeMemoList(memoList)
                adapter.exitToSelectionMode()
            }
            .setNegativeButton("취소") { _, _ ->
            }
            .show()
    }

    // 모두 삭제 여부를 묻는 Dialog 띄워주는 함수
    private fun showDeleteAllDialog() {
        MemoAppDialog.Builder(requireContext())
            .setTitle("")
            .setMessage("모든 메모를 삭제 하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
                viewModel.removeAllMemo()
            }
            .setNegativeButton("취소") { _, _ ->
            }
            .show()
    }
}