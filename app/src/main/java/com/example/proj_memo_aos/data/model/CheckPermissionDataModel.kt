package com.example.proj_memo_aos.data.model

// 권한 설정 관련 Model
data class CheckPermissionDataModel(
    var checkPermissionsResult: CheckPermissionResult = CheckPermissionResult.None,
    var missingPermissions: List<String> = emptyList(),
    var missingRequiredPermissions: List<String> = emptyList()
)

// 권한 확인에 대한 결과
enum class CheckPermissionResult {
    None,
    AllPermissionsAllowed,
    RequiredPermissionsAllowed,
    RequiredPermissionsDenied
}

// 권한 설정 Step
enum class RequestPermissionsStep {
    StepRequestToPopUp,
    StepRequestToAlertDialog,
    StepCheckAndFinish
}