package com.example.proj_memo_aos.helper

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//Activity, Fragment 간 전달되는 데이터 타입을 안전하게 확장하기 위해 Parcelize 라이브러리 사용 (객체를 전달하기 위해 사용)
@Parcelize
enum class MemoEditorResult : Parcelable {
    SaveToMemo,
    Delete,
    DoNothing
}