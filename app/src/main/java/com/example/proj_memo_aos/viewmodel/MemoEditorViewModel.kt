package com.example.proj_memo_aos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proj_memo_aos.data.model.MemoDataModel

//Memo Editor에서 사용되는 Memo 객체
class MemoEditorViewModel: ViewModel() {

    private val _memo = MutableLiveData<MemoDataModel>()
    val memo: LiveData<MemoDataModel> get() = _memo

    fun setMemo(memo: MemoDataModel) {
        _memo.value = memo
    }

    fun changeMemo(memo: MemoDataModel) {
        _memo.value?.apply {
            title = memo.title
            content = memo.content
            editTimestamp = memo.editTimestamp
        }
    }
}