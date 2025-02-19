package com.example.proj_memo_aos.helper
import android.content.Context
import android.widget.Toast

// Toast를 출력하려는 순간 현재 Toast 메세지가 출력되는것과 상관없이 출력하고싶은 새로운 Toast 메세지를 출력하기 위해 하나의 Toast 객체만을 사용 및 관리하는 클래스
class SingleToast {
    private var toast: Toast
    private var context: Context

    constructor(context: Context) {
        this.context = context
        toast = Toast(this.context)
    }

    // Activity에서 재생중인 Toast 메세지가 있다면 해당 Toast 메세지를 Cancel 하고 출력하기 위한 함수
    fun showMessage(text: String) {
        try {
            toast.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        toast = Toast.makeText(context.applicationContext, text, Toast.LENGTH_SHORT)
        toast.show()
    }
}