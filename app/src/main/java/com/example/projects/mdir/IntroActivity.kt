package com.example.projects.mdir

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.projects.R
import com.example.projects.mdir.repository.AbsStorageRepository
import com.example.projects.mdir.repository.InitRepository
import com.example.projects.mdir.repository.LegacyStorageRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class IntroActivity : AppCompatActivity(R.layout.activity_intro) {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        if(checkPermission()) {
            initRepository()
        }
    }

    private fun checkPermission() : Boolean {
        Log.d(TAG, "checkPermission")
        val permissions = Array(1) { "android.permission.WRITE_EXTERNAL_STORAGE" }
        if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
            return false
        }
        return true
    }

    private fun initRepository() {
        Log.d(TAG, "initRepository")
        GlobalScope.launch {
            LegacyStorageRepository.INSTANCE.initRepository(object :InitRepository {
                override fun finish(complete: Boolean) {
                    Log.d(TAG, "finish")
                    startActivity(Intent(this@IntroActivity, FileManagerActivity::class.java))
                    finish()
                }
            })
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.d(TAG, "onRequestPermissionsResult")
        if(requestCode == REQUEST_CODE) {
            grantResults.forEach {
                // 허용 미동의시 알림과 함께 종료
                if(it != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한 설정이 필요합니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }

                initRepository()
            }
        }
    }

    companion object {
        const val TAG = "[DE] Intro"
        const val REQUEST_CODE = 1
    }
}