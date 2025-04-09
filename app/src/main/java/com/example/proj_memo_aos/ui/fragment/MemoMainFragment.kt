package com.example.proj_memo_aos.ui.fragment

import android.app.AlertDialog
import android.os.Build
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.proj_memo_aos.R
import com.example.proj_memo_aos.data.model.MemoDataModel
import com.example.proj_memo_aos.databinding.FragmentMemoMainBinding
import com.example.proj_memo_aos.helper.MemoClickType
import com.example.proj_memo_aos.helper.MemoEditorResult
import com.example.proj_memo_aos.helper.OnMemoClick
import com.example.proj_memo_aos.ui.adapter.MemoAdapter
import com.example.proj_memo_aos.ui.base.BaseFragment
import com.example.proj_memo_aos.viewmodel.MemoMainViewModel
import dagger.hilt.android.AndroidEntryPoint

//Hilt가 의존성을 주입해 줄 수 있도록 @AndroidEntryPoint 사용
@AndroidEntryPoint
class MemoMainFragment: BaseFragment<FragmentMemoMainBinding>(R.layout.fragment_memo_main, "MemoEditor") {

    private val viewModel by viewModels<MemoMainViewModel>()

    private lateinit var adapter: MemoAdapter

    override fun setInitialize() {
        setVariable()
        setListeners()
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
            when(memoEditorResult) {
                MemoEditorResult.SaveToNewMemo -> if (memo != null) viewModel.updateMemo(memo)
                MemoEditorResult.DoNotSave -> { }
                MemoEditorResult.Delete -> if (memo != null) viewModel.removeMemo(memo)
                null -> { }
            }
        }

        //MemoList를 표기할 adapter 선언
        adapter = MemoAdapter(arrayListOf(), object : OnMemoClick {
            override fun onMemoClick(memo: MemoDataModel, memoClickType: MemoClickType) {
                onMemoClickInMainFragment(memo, memoClickType)
            }
        })
        binding.recyclerviewMemo.adapter = adapter
    }

    private fun setListeners() {
        //add Button Clich시 빈 메모 추가
        binding.addMemoBtn.setOnClickListener {
            viewModel.addNewMemo()
        }

        //memoList 변경이 감지되면 memo list adapter 업데이트
        viewModel.memoList.observe(this) { memoList ->
            adapter.updateData(memoList)
        }

        binding.toolbar.apply {
            //Toolbar NavigationIcon을 메뉴 아이콘으로 변경
            setNavigationIcon(R.drawable.baseline_menu_black_24dp)
            setNavigationOnClickListener {
            }

            //Toolbar 메뉴 List item 대한 ClickListener
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menuSearch -> {
                        //메모 검색을 위한 Dialog를 띄워줌
                        showSearchDialog()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    //각 Memo 객체를 눌렀을 때의 처리
    private fun onMemoClickInMainFragment(memo: MemoDataModel, memoClickType: MemoClickType) {
        when (memoClickType) {
            //짧게 눌렀을 때 Memo Editor로 이동
            MemoClickType.ClickBody -> {
                showMemoEditor(memo)
            }
            //길게 눌렀을 때 삭제여부를 묻는 Dialog를 띄워줌
            MemoClickType.LongClick -> {
                showDeleteDialog(memo)
            }
        }
    }

    //Memo Editor로 이동하는 함수
    private fun showMemoEditor(memo: MemoDataModel) {
        val action = MemoMainFragmentDirections.actionMemoMainToMemoEditor(memo)
        findNavController().navigate(action)
    }

    //메모 검색을 위한 Dialog (TBD)
    private fun showSearchDialog() {
        val editText = EditText(requireContext())

        AlertDialog.Builder(requireContext())
            .setTitle("검색")
            .setView(editText)
            .setPositiveButton("예") { _, _ ->
                adapter.toggleMemoListHighlight(editText.text.toString())
            }
            .setNeutralButton("취소") { _, _ ->

            }
            .show()
    }

    // 삭제 여부를 묻는 AlertDialog을 띄워주는 함수
    private fun showDeleteDialog(memo: MemoDataModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("")
            .setMessage("\"${memo.title}\" 메모를 삭제 하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                viewModel.removeMemo(memo)
            }
            .setNegativeButton("취소") { _, _ ->
            }
            .show()
    }
}