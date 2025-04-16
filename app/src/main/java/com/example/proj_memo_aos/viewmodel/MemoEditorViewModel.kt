package com.example.proj_memo_aos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proj_memo_aos.data.model.MemoDataModel

//Memo Editor에서 사용되는 Memo 객체
class MemoEditorViewModel: ViewModel() {

    private val _memo = MutableLiveData<MemoDataModel>()
    val memo: LiveData<MemoDataModel> get() = _memo

    private lateinit var beforeMemo: MemoDataModel

    fun setMemo(memo: MemoDataModel) {
        beforeMemo = memo.copy()
        _memo.value = memo
    }

    fun setTitle(title: String) {
        _memo.value?.title = title
        _memo.postValue(_memo.value)
    }

    fun changeMemo(memo: MemoDataModel) {
        _memo.value?.apply {
            title = memo.title
            content = memo.content
            editTimestamp = memo.editTimestamp
        }
    }

    fun checkMemoToBeSaved(): Boolean {
        val isSameMemo = _memo.value?.title == beforeMemo.title && _memo.value?.content == beforeMemo.content
        val isEmptyMemo = _memo.value?.title == "" && _memo.value?.content == ""

        return !isSameMemo && !isEmptyMemo
    }
}