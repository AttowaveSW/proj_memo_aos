package com.example.proj_memo_aos.data.model

import android.os.Parcelable
import java.util.Calendar
import java.util.UUID
import kotlinx.parcelize.Parcelize

//Activity, Fragment 간 전달되는 데이터 타입을 안전하게 확장하기 위해 Parcelize 라이브러리 사용 (객체를 전달하기 위해 사용)
@Parcelize
data class MemoDataModel (
    var title: String = "",
    var content: String = "",
    val createTimestamp: Calendar = Calendar.getInstance(),
    var editTimestamp: Calendar = Calendar.getInstance(),
    val uid: String = UUID.randomUUID().toString(),
    var isHighlight: Boolean = false
): Parcelable {
    //Data가 수정될 때 마지막 수정시간을 지정하는 함수
    fun setEditTimestampToCurrentTime() {
        editTimestamp = Calendar.getInstance()
    }
}