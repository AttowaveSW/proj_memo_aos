package com.example.proj_memo_aos.helper

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup

class Utils {
    companion object {
        // 재귀 함수를 통해 하위 자식 뷰들을 모두 가져오는 함수
        fun getAllChildViews(view: View, includeSelf: Boolean = false): List<View> {
            val result = mutableListOf<View>()
            if (includeSelf) {
                result.add(view)
            }

            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    val child = view.getChildAt(i)
                    result.addAll(getAllChildViews(child, includeSelf = true))
                }
            }
            return result
        }

        fun dpToPx(resources: Resources,dp: Int): Int {
            return (dp * resources.displayMetrics.density).toInt()
        }
    }
}