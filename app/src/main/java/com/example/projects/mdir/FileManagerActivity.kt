package com.example.projects.mdir

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.Surface
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projects.R
import com.example.projects.databinding.LayoutFileManagerBinding
import com.example.projects.mdir.common.FileUtil
import com.example.projects.mdir.listener.StateChangeListener

class FileManagerActivity : AppCompatActivity(), StateChangeListener {

    private lateinit var binding : LayoutFileManagerBinding

    // TARGET API 29 이상인 경우 사용할 수 없다. 외부 저장소 정책이 애플과 동일해진다.
    private val adapter: FileAdapter = FileAdapter(this)

    val livePath = MutableLiveData<String>()
    val liveDirs = MutableLiveData<Int>()
    val liveFiles = MutableLiveData<Int>()
    val liveImages = MutableLiveData<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.layout_file_manager)
        checkPermission()

        binding = DataBindingUtil.setContentView(this, R.layout.layout_file_manager)
        binding.apply {
            recycler.layoutManager = LinearLayoutManager(this@FileManagerActivity)
            recycler.adapter = adapter

            // Binding에 LifeCycleOwner을 지정해줘야 LiveData가 실시간으로 변경된다.
            lifecycleOwner = this@FileManagerActivity
            activity = this@FileManagerActivity
        }

        adapter.apply {
            stateListener = this@FileManagerActivity
            isPortrait = (windowManager.defaultDisplay.rotation == Surface.ROTATION_0) or (windowManager.defaultDisplay.rotation == Surface.ROTATION_180)
            refreshDir()
        }

//        livePath.value = ".."
//        liveDirs.value = 0
//        liveFiles.value = 0
//        liveImages.value = 0
    }

    private fun checkPermission() {
        val permissions = Array(1) { "android.permission.WRITE_EXTERNAL_STORAGE" }
        if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions,
                REQUEST_CODE
            )
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
            adapter.refreshDir()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged ORIENTATION:${newConfig.orientation}")
        adapter.isPortrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.hide_show -> {
                adapter.isHideShow = !adapter.isHideShow
                item.isChecked = adapter.isHideShow
                adapter.refreshDir()
                true
            }
            else -> { false }
        }
    }

    override fun notifyPath(path: String) {
//        binding.tvPath.text = "..${path.removePrefix(FileUtil.ROOT)}"
        livePath.value = "..${path.removePrefix(FileUtil.ROOT)}"
    }

    override fun notifyDirCount(count: Int) {
//        binding.tvDirs.text = "$count"
        liveDirs.value = count
    }

    override fun notifyFileCount(count: Int) {
//        binding.tvFiles.text = "$count"
        liveFiles.value = count
    }

    override fun notifyImageCount(count: Int) {
//        binding.tvImgs.text = "$count"
        liveImages.value = count
    }

    companion object {
        const val TAG = "FileManagerActivity"
        const val REQUEST_CODE = 1
    }
}