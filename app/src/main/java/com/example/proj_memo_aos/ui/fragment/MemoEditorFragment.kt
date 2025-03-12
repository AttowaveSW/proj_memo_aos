package com.example.proj_memo_aos.ui.fragment

import android.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.proj_memo_aos.R
import com.example.proj_memo_aos.data.model.MemoDataModel
import com.example.proj_memo_aos.databinding.FragmentMemoEditorBinding
import com.example.proj_memo_aos.ui.base.BaseFragment
import com.example.proj_memo_aos.viewmodel.MemoEditorViewModel

// Memo 수정을 위한 Memo Edit Fragment
class MemoEditorFragment: BaseFragment<FragmentMemoEditorBinding>(R.layout.fragment_memo_editor, "MemoEditor") {

    private val args: MemoEditorFragmentArgs by navArgs()

    private val viewModel by viewModels<MemoEditorViewModel>()

    private lateinit var beforeMemo: MemoDataModel

    override fun setInitialize(){
        setVariable()
        setListeners()
    }

    override fun onBackPressed() {
        showDeleteDialog()
    }

    private fun setVariable() {
        binding.viewModel = viewModel

        viewModel.setMemo(args.memo)
        beforeMemo = args.memo.copy()
    }

    private fun setListeners() {
    }

    private fun exitMemoEditor(isSave: Boolean) {
        if (isSave) {
            viewModel.memo.value?.setEditTimestampToCurrentTime()
        } else {
            viewModel.changeMemo(beforeMemo)
        }

        findNavController().popBackStack()
    }

    // 저장 여부를 묻는 AlertDialog을 띄워주는 함수
    private fun showDeleteDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("")
            .setMessage("\"${viewModel.memo.value?.title}\" 메모를 저장 하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                exitMemoEditor(true)
            }
            .setNegativeButton("아니오") { _, _ ->
                exitMemoEditor(false)
            }
            .setNeutralButton("취소") { _, _ ->

            }
            .show()
    }
}