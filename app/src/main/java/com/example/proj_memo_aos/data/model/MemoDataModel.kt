package com.example.proj_memo_aos.data.model

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import java.util.Calendar
import java.util.UUID
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

//Activity, Fragment 간 전달되는 데이터 타입을 안전하게 확장하기 위해 Parcelize 라이브러리 사용 (객체를 전달하기 위해 사용)
// test 2 hahaha
@Parcelize
data class MemoDataModel (
    var title: String = "",
    var content: String = "",
    val createTimestamp: Calendar = Calendar.getInstance(),
    var editTimestamp: Calendar = Calendar.getInstance(),
    val uid: String = UUID.randomUUID().toString()
): Parcelable {
    // 메모 수정 시, 현재 시간을 수정 시간으로 갱신하는 함수
    fun setEditTimestampToCurrentTime() {
        editTimestamp = Calendar.getInstance()
    }

    // Calendar 객체를 "yyyy년 mm월 dd일" 형태의 문자열로 변환
    private fun getDateStringFromCalendar(calendar: Calendar): String {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return "${year}년 ${month}월 ${day}일"
    }

    // 생성일을 문자열로 반환
    fun getCreateDateString(): String {
        return getDateStringFromCalendar(createTimestamp)
    }

    // 수정일을 문자열로 반환
    fun getEditDateString(): String {
        return getDateStringFromCalendar(editTimestamp)
    }
}