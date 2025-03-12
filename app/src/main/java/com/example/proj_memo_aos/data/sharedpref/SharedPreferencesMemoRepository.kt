package com.example.proj_memo_aos.data.sharedpref

import android.content.Context
import android.content.SharedPreferences
import com.example.proj_memo_aos.data.model.MemoDataModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

//SharedPreferences를 사용해 Memo List를 JSON형식으로 저장 및 불러오는 클래스
class SharedPreferencesMemoRepository @Inject constructor(
    context: Context
) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("memo_prefs", Context.MODE_PRIVATE)
    //Gson = JSON 형식 데이터 관련 라이브러리
    private val gson = Gson()

    companion object {
        private const val PREF_MEMO = "memo_list"
    }

    fun loadMemoList(): List<MemoDataModel> {
        // Gson에서 Memo List 읽어오기
        val jsonString = sharedPreferences.getString(PREF_MEMO, null)

        return if (jsonString != null) {
            // 읽어온 Memo List가 있으면 List<MemoDataModel> 타입으로 읽어와 반환
            val type = object : TypeToken<List<MemoDataModel>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            // 읽어온 Memo List가 없으면 빈 List 반환
            emptyList()
        }
    }

    fun saveMemoList(memoList: List<MemoDataModel>) {
        // memoList를 JSON 형식으로 저장
        val jsonString = gson.toJson(memoList)
        sharedPreferences.edit().putString(PREF_MEMO, jsonString).apply()
    }
}