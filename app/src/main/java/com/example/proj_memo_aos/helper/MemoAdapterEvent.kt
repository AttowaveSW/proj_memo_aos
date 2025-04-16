package com.example.proj_memo_aos.helper

import com.example.proj_memo_aos.data.model.MemoDataModel

//Memo item Click 이벤트를 처리하기 위한 enum 클래스
enum class MemoClickType{
    ClickBody,
    LongClick
}

//Memo item Click 이벤트를 위한 interface
interface OnMemoClick {
    fun onMemoClick(memo: MemoDataModel, memoClickType: MemoClickType)
}

//recyclerview의 선택모드 변경 감지를 위한 interface
interface OnSelectionModeChange {
    fun onSelectionModeChange(isSelectionMode: Boolean)
}

//recyclerview의 선택된 item 개수 감지를 위한 interface
interface OnItemSelectedCountChange {
    fun onItemSelectedCountChange(count: Int)
}