package com.example.proj_memo_aos.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.proj_memo_aos.data.model.CheckPermissionDataModel
import com.example.proj_memo_aos.data.model.CheckPermissionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

//Hilt 라이브러리를 사용하여 의존성을 Application 단에서 직접 주입
//Hilt 라이브러리를 사용해 Application 단에서 직접 의존성을 주입 받기 때문에 @ApplicationContext를 사용하여 Application의 Context를 제공 받아야 함
@HiltViewModel
class SplashViewModel @Inject constructor(
    @ApplicationContext private val context: Context
): ViewModel() {
    // Bluetooth 관련 권한
    private val BLUETOOTH_PERMISSIONS: Map<String, String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        mapOf(
            Manifest.permission.BLUETOOTH_SCAN to "근처 기기",
            Manifest.permission.BLUETOOTH_CONNECT to "근처 기기",
            Manifest.permission.ACCESS_FINE_LOCATION to "위치"
        )
    } else {
        mapOf(
            Manifest.permission.BLUETOOTH to "블루투스",
            Manifest.permission.BLUETOOTH_ADMIN to "블루투스",
            Manifest.permission.ACCESS_FINE_LOCATION to "위치"
        )
    }

    // Network 관련 권한
    private val NETWORK_PERMISSIONS: Map<String, String> =
        mapOf(Manifest.permission.INTERNET to "네트워크")

    // Photo 관련 권한
    private val PHOTO_PERMISSIONS: Map<String, String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        mapOf(
            Manifest.permission.READ_MEDIA_IMAGES to "미디어 파일 접근",
            Manifest.permission.READ_MEDIA_VIDEO to "미디어 파일 접근",
            Manifest.permission.READ_MEDIA_AUDIO to "미디어 파일 접근"
        )
    } else {
        mapOf(
            Manifest.permission.READ_EXTERNAL_STORAGE to "파일 접근",
            Manifest.permission.WRITE_EXTERNAL_STORAGE to "파일 접근"
        )
    }

    // 알림 관련 권한
    private val NOTIFICATION_PERMISSIONS: Map<String, String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        mapOf(Manifest.permission.POST_NOTIFICATIONS to "알림")
    } else {
        // Android 13 미만에서는 권한 요청 필요 없음
        mapOf()
    }

    // Bluetooth, Network 권한은 필수 권한으로 설정
    private val REQUIRED_PERMISSIONS: Map<String, String> = BLUETOOTH_PERMISSIONS + NETWORK_PERMISSIONS

    // Photo, notification 권한은 선택적 권한으로 설정
    private val OPTIONAL_PERMISSIONS: Map<String, String> = PHOTO_PERMISSIONS + NOTIFICATION_PERMISSIONS

    private val REQUEST_PERMISSIONS: Map<String, String> = REQUIRED_PERMISSIONS + OPTIONAL_PERMISSIONS

    fun checkPermissions(): CheckPermissionDataModel {
        val checkPermissionDataModel = CheckPermissionDataModel()

        // 허용되지 않은 권한 확인
        checkPermissionDataModel.missingPermissions = REQUEST_PERMISSIONS.keys.toTypedArray().filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        // 허용되지 않은 필수 권한 확인
        checkPermissionDataModel.missingRequiredPermissions = checkPermissionDataModel.missingPermissions.filter {
                permission -> REQUIRED_PERMISSIONS.keys.toTypedArray().contains(permission)
        }

        if (checkPermissionDataModel.missingPermissions.isEmpty()) {
            // 권한이 모두 허용되었을 경우 AllPermissionsAllowed 반환
            checkPermissionDataModel.checkPermissionsResult = CheckPermissionResult.AllPermissionsAllowed
        } else {
            if (checkPermissionDataModel.missingRequiredPermissions.isNotEmpty()) {
                // 필수 권한이 허용되지 않았을 경우 RequiredPermissionsDenied 반환
                checkPermissionDataModel.checkPermissionsResult = CheckPermissionResult.RequiredPermissionsDenied
            } else {
                // 필수 권한은 허용되었을 경우 RequiredPermissionsAllowed 반환
                checkPermissionDataModel.checkPermissionsResult = CheckPermissionResult.RequiredPermissionsAllowed
            }
        }

        return checkPermissionDataModel
    }

    // 맵핑 된 권한의 이름을 반환하는 함수
    fun getPermissionName(permission: String): String {
        return REQUEST_PERMISSIONS[permission] ?: ""
    }
}