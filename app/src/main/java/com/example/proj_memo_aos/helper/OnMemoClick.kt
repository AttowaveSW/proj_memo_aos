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