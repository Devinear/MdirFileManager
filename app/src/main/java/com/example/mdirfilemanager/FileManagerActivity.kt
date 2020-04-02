package com.example.mdirfilemanager

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FileManagerActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE = 1
    }

    // TARGET API 29 이상인 경우 사용할 수 없다. 외부 저장소 정책이 애플과 동일해진다.
    private val adapter: FileAdapter = FileAdapter(this, Environment.getExternalStorageDirectory().absolutePath, hidden = false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_file_manager)
        checkPermission()

        findViewById<RecyclerView>(R.id.recycler).also {
            it.layoutManager = LinearLayoutManager(this@FileManagerActivity)
            it.adapter = adapter
        }
        adapter.refreshDir()
    }

    private fun checkPermission() {
//        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
//            return
        val permissions = Array(1) {
//            "android.permission.READ_EXTERNAL_STORAGE"
            "android.permission.WRITE_EXTERNAL_STORAGE"
        }
        if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == REQUEST_CODE) {
            grantResults.forEach {
                // 허용 미동의
                if(it != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한 설정이 필요합니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}