package com.example.proj_memo_aos.helper

import android.content.res.ColorStateList
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.core.widget.ImageViewCompat
import com.example.proj_memo_aos.data.model.BackgroundColorDataModel

class Utils {
    companion object {
        // 재귀 함수를 통해 하위 자식 뷰들을 모두 가져오는 함수
        fun getAllChildViews(root: View, includeSelf: Boolean = false): List<View> {
            val result = mutableListOf<View>()
            if (includeSelf) {
                result.add(root)
            }

            if (root is ViewGroup) {
                for (i in 0 until root.childCount) {
                    val child = root.getChildAt(i)
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