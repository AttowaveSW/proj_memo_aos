package com.example.proj_memo_aos.ui.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.proj_memo_aos.R
import com.example.proj_memo_aos.data.model.CheckPermissionResult
import com.example.proj_memo_aos.data.model.RequestPermissionsStep
import com.example.proj_memo_aos.databinding.ActivitySplashBinding
import com.example.proj_memo_aos.ui.base.BaseActivity
import com.example.proj_memo_aos.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {

    private val viewModel by viewModels<SplashViewModel>()

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var settingsLauncher: ActivityResultLauncher<Intent>

    override fun setInitialize(){
        setVariable()
        setListeners()

        Handler(Looper.getMainLooper()).postDelayed({
            // Splash 2초 뒤 권한 요청
            requestPermissionProcedure(RequestPermissionsStep.StepRequestToPopUp)
        }, 2000)
    }

    private fun setVariable() {
        // Databinding: ViewModel 동기화
        binding.viewModel = viewModel
    }

    private fun setListeners() {
        // 권한 설정 팝업 Launcher
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            // 사용자 설정 권한 요청 Step으로 이동
            requestPermissionProcedure(RequestPermissionsStep.StepRequestToAlertDialog)
        }

        // 설정창 Launcher
        settingsLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            // 최종 거부 Step으로 이동
            requestPermissionProcedure(RequestPermissionsStep.StepCheckAndFinish)
        }
    }

    private fun requestPermissionProcedure(requestPermissionsStatusMachine: RequestPermissionsStep) {
        val checkPermissionsResult = viewModel.checkPermissions()

        when (checkPermissionsResult.checkPermissionsResult) {
            // 모든 권한이 허용 되었거나 필수 권한이 허용 되었을 경우
            CheckPermissionResult.AllPermissionsAllowed, CheckPermissionResult.RequiredPermissionsAllowed -> {
                // MainFragment로 이동
                goToMain()
            }

            // 필수 권한이 허용되지 않았을 경우
            CheckPermissionResult.RequiredPermissionsDenied -> {
                when (requestPermissionsStatusMachine) {
                    // Popup 권한 요청 Step
                    RequestPermissionsStep.StepRequestToPopUp -> {
                        // 권한 요청 팝업 실행
                        permissionLauncher.launch(checkPermissionsResult.missingPermissions.toTypedArray())
                    }

                    // 사용자 설정 권한 요청 Step
                    RequestPermissionsStep.StepRequestToAlertDialog -> {
                        // 필수 권한이 허용되지 않았을 경우 권한 요청 팝업 실행
                        showSettingsDialog(settingsLauncher, checkPermissionsResult.missingRequiredPermissions)
                    }

                    // 최종 거부 Step
                    RequestPermissionsStep.StepCheckAndFinish -> {
                        finishRequiredPermissionsDenied(checkPermissionsResult.missingRequiredPermissions)
                    }
                }
            }

            CheckPermissionResult.None -> TODO()
        }
    }

    private fun goToMain() {
        Handler(Looper.getMainLooper()).postDelayed({
            // 1초 동안 Splash 화면 노출 후 Main으로 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }, 1000)
    }

    // 사용자 설정 화면을 유도하는 AlertDialog을 띄워주는 함수
    private fun showSettingsDialog(settingsLauncher: ActivityResultLauncher<Intent>, deniedRequiresPermissions: List<String>) {
        val deniedRequiresPermissionsString = getPermissionString(deniedRequiresPermissions)

        AlertDialog.Builder(this)
            .setTitle("권한 설정 필요")
            .setMessage("이 앱을 사용하려면 ${deniedRequiresPermissionsString.joinToString(", ")} 권한을 허용해야 합니다.")
            .setPositiveButton("설정으로 이동") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")

                settingsLauncher.launch(intent)
            }
            .setNegativeButton("취소") { _, _ ->
                // 취소키 누를 경우
                finishRequiredPermissionsDenied(deniedRequiresPermissions)
            }
            .setOnCancelListener {
                // 뒤로가기 키로 Cancel 할 경우
                finishRequiredPermissionsDenied(deniedRequiresPermissions)
            }
            .show()
    }

    // SplashFragmentViewModel에 맵핑 된 권한의 이름을 가져오는 함수
    private fun getPermissionString(permissions: List<String>): List<String> {
        val permissionsString: MutableList<String> = mutableListOf()

        permissions.forEach{ permission ->
            val permissionString = viewModel.getPermissionName(permission)
            permissionsString.takeIf { !it.contains(permissionString) }?.add(permissionString)
        }

        return permissionsString
    }

    // 거부된 권한을 허용 해야 한다는 Toast 메세지 출력하며 APP 종료하는 함수
    private fun finishRequiredPermissionsDenied(deniedRequiresPermissions: List<String>) {
        val deniedRequiresPermissionsLabel = getPermissionString(deniedRequiresPermissions)

        singleToast.showMessage("이 앱을 사용하려면 ${deniedRequiresPermissionsLabel.joinToString(", ")} 권한을 허용해야 합니다.")
        this.finish()
    }
}