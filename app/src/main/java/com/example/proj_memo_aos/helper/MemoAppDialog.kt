package com.example.proj_memo_aos.helper

import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import com.example.proj_memo_aos.R

// custom dialog를 사용하기 위한 class
class MemoAppDialog(context: Context): AlertDialog(context) {

    class Builder(context: Context): AlertDialog.Builder(context) {

        /**
         * show() 함수 override하여 customizing
         * memo_bg 양식을 사용하며 alert dialog를 아래에 위치하도록 변경
         */
        override fun show(): AlertDialog {
            val dialog = this.create()
            dialog.window?.setGravity(Gravity.BOTTOM)
            dialog.window?.setBackgroundDrawableResource(R.drawable.memo_bg)
            dialog.window?.setDimAmount(0.2f)
            dialog.window?.decorView?.elevation = 100f
            dialog.show()
            return dialog
        }
    }
}