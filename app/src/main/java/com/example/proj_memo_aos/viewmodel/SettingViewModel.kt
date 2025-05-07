package com.example.proj_memo_aos.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

//Hilt 라이브러리를 사용하여 의존성을 Application 단에서 직접 주입
//Hilt 라이브러리를 사용해 Application 단에서 직접 의존성을 주입 받기 때문에 @ApplicationContext를 사용하여 Application의 Context를 제공 받아야 함
@HiltViewModel
class SettingViewModel @Inject constructor(
    @ApplicationContext private val context: Context
): ViewModel() {

    fun getNotificationPermissionIsAllowed(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}