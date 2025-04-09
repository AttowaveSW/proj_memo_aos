package com.example.proj_memo_aos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proj_memo_aos.data.model.MemoDataModel
import com.example.proj_memo_aos.data.sharedpref.SharedPreferencesMemoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MemoMainViewModel @Inject constructor(
    private val repository: SharedPreferencesMemoRepository
): ViewModel() {

    private val _memoList = MutableLiveData<List<MemoDataModel>>(emptyList())
    val memoList: LiveData<List<MemoDataModel>> get() = _memoList


    init {
        _memoList.value = repository.loadMemoList()

        _memoList.observeForever {  newList ->
            repository.saveMemoList(newList)
        }
    }

    fun addNewMemo() {
        val newMemo = MemoDataModel()
        val updateList = _memoList.value?.plus(newMemo) ?: listOf(newMemo)
        _memoList.value = updateList
    }

    fun updateMemo(newMemo: MemoDataModel) {
        val index = _memoList.value?.indexOfFirst { it.uid == newMemo.uid }!!

        if(index != -1) {
            val currentList = _memoList.value?.toMutableList() ?: mutableListOf()
            if (index in currentList.indices) {
                currentList[index] = newMemo
                _memoList.postValue(currentList)
            }
        }
    }

    fun removeMemo(memo: MemoDataModel) {
        val index = _memoList.value?.indexOfFirst { it.uid == memo.uid }!!

        if(index != -1) {
            val currentList = _memoList.value?.toMutableList() ?: mutableListOf()
            if (index in currentList.indices) {
                currentList.removeAt(index)
                _memoList.postValue(currentList)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _memoList.removeObserver { }
    }
}