package com.example.proj_memo_aos.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.proj_memo_aos.R
import com.example.proj_memo_aos.databinding.FragmentMemoEditorBinding
import com.example.proj_memo_aos.helper.MemoAppDialog
import com.example.proj_memo_aos.helper.MemoEditorResult
import com.example.proj_memo_aos.ui.base.BaseFragment
import com.example.proj_memo_aos.viewmodel.MemoEditorViewModel

// Memo 수정을 위한 Memo Edit Fragment
class MemoEditorFragment: BaseFragment<FragmentMemoEditorBinding>(R.layout.fragment_memo_editor, "MemoEditor") {

    private val args: MemoEditorFragmentArgs by navArgs()

    private val viewModel by viewModels<MemoEditorViewModel>()

    override fun setInitialize() {
        setVariable()
        setListeners()
    }

    //뒤로가기 키를 override하여 클릭시 저장 여부를 묻는 AlertDialog 띄워줌
    private fun setOnBackPressed() {
        setOnBackPressed { backKeyProcess() }
    }

    private fun setVariable() {
        binding.viewModel = viewModel

        viewModel.setMemo(args.memo.copy())
    }

    private fun setListeners() {
        binding.toolbar.apply {
            //Toolbar의 NavigationIcon을 abc_ic_ab_back_material(<-) 로 변경
            setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
            setNavigationOnClickListener {
                backKeyProcess()
            }

            //Toolbar 메뉴 List item 대한 ClickListener
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.saveMemoBtn -> {
                        //저장하며 Memo Editor 종료
                        exitMemoEditor(MemoEditorResult.SaveToMemo)
                        true
                    }
                    R.id.menuSave -> {
                        //저장하며 Memo Editor 종료
                        exitMemoEditor(MemoEditorResult.SaveToMemo)
                        true
                    }
                    R.id.menuDelete -> {
                        //삭제하며 Memo Editor 종료
                        exitMemoEditor(MemoEditorResult.Delete)
                        true
                    }
                    else -> false
                }
            }
        }

        // 뒤로가기 설정
        setOnBackPressed()
    }

    private fun backKeyProcess() {
        if(viewModel.checkMemoToBeSaved())
            showSaveDialog()
        else
            exitMemoEditor(MemoEditorResult.DoNothing)
    }

    //Memo Editor 종료 함수
    private fun exitMemoEditor(memoEditorResult: MemoEditorResult) {

        //SaveToNewMemo 일 경우 마지막 수정시간 최신화
        when(memoEditorResult) {
            MemoEditorResult.SaveToMemo -> viewModel.memo.value?.setEditTimestampToCurrentTime()
            MemoEditorResult.DoNothing -> { }
            MemoEditorResult.Delete -> { }
        }

        //MemoEditorResult와 수정된 memo를 Bundle로 전달하여 MainMemoFragment가 받아 사용할 수 있도록 함
        val result = Bundle().apply {
            putParcelable("editorResult", memoEditorResult)
            putParcelable("memo", viewModel.memo.value)
        }

        parentFragmentManager.setFragmentResult("memoEditorResult", result)
        findNavController().popBackStack()
    }
    // 저장 여부를 묻는 AlertDialog을 띄워주는 함수
    private fun showSaveDialog() {
        MemoAppDialog.Builder(requireContext())
            .setTitle("")
            .setMessage("\"${viewModel.memo.value?.title}\" 메모를 저장 하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                exitMemoEditor(MemoEditorResult.SaveToMemo)
            }
            .setNegativeButton("아니오") { _, _ ->
                exitMemoEditor(MemoEditorResult.DoNothing)
            }
            .setNeutralButton("취소") { _, _ ->

            }
            .show()
    }
}