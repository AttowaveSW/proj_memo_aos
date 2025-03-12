package com.example.proj_memo_aos.ui.fragment

import android.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.proj_memo_aos.R
import com.example.proj_memo_aos.data.model.MemoDataModel
import com.example.proj_memo_aos.databinding.FragmentMemoMainBinding
import com.example.proj_memo_aos.helper.MemoClickType
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
        // Databinding: ViewModel 동기화
        binding.viewModel = viewModel

        adapter = MemoAdapter(arrayListOf(), object : OnMemoClick {
            override fun onMemoClick(memo: MemoDataModel, memoClickType: MemoClickType) {
                onMemoClickInMainFragment(memo, memoClickType)
            }
        })
        binding.recyclerviewMemo.adapter = adapter
    }

    private fun setListeners() {
        binding.addMemo.setOnClickListener {
            viewModel.addNewMemo()
        }

        viewModel.memoList.observe(this) { memoList ->
            adapter.updateData(memoList)
        }
    }

    private fun onMemoClickInMainFragment(memo: MemoDataModel, memoClickType: MemoClickType) {
        when (memoClickType) {
            MemoClickType.ClickBody -> {
                showMemoEditor(memo)
            }
            MemoClickType.LongClick -> {
                showDeleteDialog(memo)
            }
        }
    }

    private fun showMemoEditor(memo: MemoDataModel) {
        val action = MemoMainFragmentDirections.actionMemoMainToMemoEditor(memo)
        findNavController().navigate(action)
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