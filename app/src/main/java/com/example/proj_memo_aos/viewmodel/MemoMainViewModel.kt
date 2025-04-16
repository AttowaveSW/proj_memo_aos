package com.example.proj_memo_aos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.proj_memo_aos.data.model.MemoDataModel
import com.example.proj_memo_aos.data.sharedpref.SharedPreferencesMemoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// Hilt를 사용해 ViewModel에 의존성 주입
@HiltViewModel
class MemoMainViewModel @Inject constructor(
    private val repository: SharedPreferencesMemoRepository
): ViewModel() {

    // 메모 전체 목록 (내부용)
    private val memoList = MutableLiveData<List<MemoDataModel>>(emptyList())

    // 실제 화면에 보여줄 메모 목록 (필터링된 결과)
    private val _memoListToDisplay = MutableLiveData<List<MemoDataModel>>(emptyList())
    val memoListToDisplay: LiveData<List<MemoDataModel>> get() = _memoListToDisplay

    // 앱바가 확장되어 있는지 여부
    var appBarIsExpanded: Boolean = true

    // 리사이클러뷰의 열 수 (기본 2열=큰 격자)
    var recyclerViewSpanCount: Int = 2

    // 검색 모드 활성화 여부
    var isSearchMode: Boolean = false

    // 검색어
    val stringToSearch  = MutableLiveData<String>("")

    // 선택 모드 여부 (삭제 등 선택 기능에서 사용)
    var isSelectionMode: Boolean = false

    // 메모 목록이 변경되었을 때 실행되는 옵저버
    private val memoObserver = Observer<List<MemoDataModel>> { newList ->
        // 변경된 메모 목록을 저장소에 저장
        repository.saveMemoList(newList)

        // 화면에 보여줄 목록 업데이트
        updateMemoListToDisplay(stringToSearch.value)
    }

    // 검색어가 변경되었을 때 실행되는 옵저버
    private val searchMemoObserver = Observer<String> { keyword ->
        if (isSearchMode) {
            val matchedMemoList = if (keyword == "") {
                emptyList()
            } else {
                // 제목 또는 내용에 검색어가 포함된 메모 필터링
                memoList.value?.filter { memo ->
                    memo.title.contains(keyword, ignoreCase = true) || memo.content.contains(keyword, ignoreCase = true)
                }
            }

            // 결과를 화면에 반영
            matchedMemoList?.let {
                _memoListToDisplay.postValue(it)
            }
        }
    }

    // 초기화 블록: 저장소에서 메모 및 설정 불러오기 + 옵저버 등록
    init {
        memoList.value = repository.loadMemoList()

        val span = repository.loadRecyclerViewSpanCount()
        recyclerViewSpanCount = if (span != -1) span else 2

        memoList.observeForever(memoObserver)
        stringToSearch.observeForever(searchMemoObserver)
    }

    // 새로운 메모 추가 (내부에서만 사용)
    private fun addNewMemo(newMemo: MemoDataModel) {
        val updateList = memoList.value?.plus(newMemo) ?: listOf(newMemo)
        memoList.value = updateList
    }

    // 메모 업데이트 (없으면 새로 추가)
    fun updateMemo(newMemo: MemoDataModel) {
        val index = memoList.value?.indexOfFirst { it.uid == newMemo.uid }!!

        if(index != -1) {
            val currentList = memoList.value?.toMutableList() ?: mutableListOf()
            if (index in currentList.indices) {
                currentList[index] = newMemo
                memoList.postValue(currentList)
            }
        } else {
            addNewMemo(newMemo)
        }
    }

    // 여러 개의 메모 삭제
    fun removeMemoList(memoList: List<MemoDataModel>) {
        val removedMemoList = this.memoList.value?.filterNot { srcMemo ->
            memoList.any { it.uid == srcMemo.uid }
        } ?: emptyList()
        this.memoList.postValue(removedMemoList)
    }

    // 단일 메모 삭제
    fun removeMemo(memo: MemoDataModel) {
        val memoList = listOf(memo)
        removeMemoList(memoList)
    }

    // 전체 메모 삭제
    fun removeAllMemo() {
        memoList.postValue(emptyList())
    }

    // 리사이클러뷰 열 수 변경 + 저장소 반영
    fun changeRecyclerViewSpanCount(count: Int) {
        recyclerViewSpanCount = count
        repository.saveRecyclerViewSpanCount(recyclerViewSpanCount)
    }

    // 검색 모드 또는 일반 모드에 맞게 보여줄 메모 목록 갱신
    private fun updateMemoListToDisplay(keyword: String?) {
        if (this.isSearchMode) {
            keyword?.let {
                stringToSearch.postValue(it)
            }
        } else {
            memoList.value?.let {
                _memoListToDisplay.postValue(it)
            }
        }
    }

    // 검색 모드 활성화
    fun enableSearchMode(initKeyword: String?) {
        this.isSearchMode = true
        updateMemoListToDisplay(initKeyword)
    }

    // 검색 모드 비활성화
    fun disableSearchMode() {
        this.isSearchMode = false
        updateMemoListToDisplay(null)
    }

    // ViewModel 종료 시 옵저버 해제
    override fun onCleared() {
        memoList.removeObserver(memoObserver)
        stringToSearch.removeObserver(searchMemoObserver)
        super.onCleared()
    }
}