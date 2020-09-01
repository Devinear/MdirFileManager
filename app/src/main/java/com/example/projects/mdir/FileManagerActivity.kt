package com.example.projects.mdir

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.example.projects.R
import com.example.projects.databinding.LayoutFileManagerBinding
import com.example.projects.mdir.common.ExtType
import com.example.projects.mdir.common.FileType
import com.example.projects.mdir.common.FileUtil
import com.example.projects.mdir.common.ShowType
import com.example.projects.mdir.data.FileItem
import com.example.projects.mdir.listener.OnFileClickListener
import com.example.projects.mdir.view.FileAdapter
import java.io.File

class FileManagerActivity : AppCompatActivity(), OnFileClickListener {

    private lateinit var binding : LayoutFileManagerBinding

    // TARGET API 29 이상인 경우 사용할 수 없다. 외부 저장소 정책이 애플과 동일해진다.
    private val adapter: FileAdapter = FileAdapter(this)

    // UI
    val livePath = MutableLiveData<String>()
    val liveDirs = MutableLiveData<Int>()
    val liveFiles = MutableLiveData<Int>()
    val liveImages = MutableLiveData<Int>()
    val liveShow = MutableLiveData<String>()

    // Value
    private val listFileItem = ObservableArrayList<FileItem>()
    private var currentPath: String = ""
    private var isHideShow : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        checkPermission()

        binding = DataBindingUtil.setContentView(this, R.layout.layout_file_manager)
        binding.apply {
            recycler.adapter = adapter

            // Binding에 LifeCycleOwner을 지정해줘야 LiveData가 실시간으로 변경된다.
            lifecycleOwner = this@FileManagerActivity
            activity = this@FileManagerActivity

            // Bind Item
            items = listFileItem
        }

        adapter.apply {
            clickListener = this@FileManagerActivity
            isPortrait = (windowManager.defaultDisplay.rotation == Surface.ROTATION_0) or (windowManager.defaultDisplay.rotation == Surface.ROTATION_180)
            refreshDir()
        }
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
            refreshDir()
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
                isHideShow = !isHideShow
                item.isChecked = isHideShow
                refreshDir()
                true
            }
            else -> { false }
        }
    }

    override fun onClickFileItem(item: FileItem) {
        when (item.type) {
            FileType.UpDir -> {
                if(currentPath == FileUtil.ROOT) {
                    Toast.makeText(this, "최상위 폴더입니다.", Toast.LENGTH_SHORT).show()
                    return
                }
                currentPath = FileUtil.getUpDirPath(currentPath)
                livePath.value = "..${currentPath.removePrefix(FileUtil.ROOT)}"
                refreshDir()
            }
            FileType.Dir -> {
                currentPath = "$currentPath/${item.name}"
                livePath.value = "..${currentPath.removePrefix(FileUtil.ROOT)}"
                refreshDir()
            }
            else -> {
                // 일반 파일
                Toast.makeText(this, item.name, Toast.LENGTH_SHORT).show()

                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = when(FileUtil.getFileExtType(item.ext)) {
                        ExtType.Image -> "image/*"
                        ExtType.Video -> "video/*"
                        ExtType.Audio -> "audio/*"
                        else -> "application/*"
                    }
                    putExtra(Intent.EXTRA_STREAM, File("$currentPath/${item.name}").toURI())
                }
                // java.lang.ClassCastException: java.net.URI cannot be cast to android.os.Parcelable 발생
                startActivity(Intent.createChooser(sendIntent, "공유: ${item.name}.${item.ext}"))
            }
        }
    }

    private fun refreshDir(isHome: Boolean = false, isShowType: ShowType = ShowType.All) {
        Log.d(TAG, "refreshDir IsHome:$isHome IsShowType:$isShowType")
        if(currentPath.isEmpty() || isHome) {
            currentPath = FileUtil.ROOT
            livePath.value = ".."
        }

        listFileItem.clear()
        listFileItem.addAll(FileUtil.getChildFileItems(this, currentPath, isHideShow, isShowType))

        var dirs = 0
        var files = 0
        var images = 0
        listFileItem.forEach {
            when(it.type) {
                FileType.UpDir -> { }
                FileType.Dir -> { dirs += 1 }
                FileType.Image -> {
                    images += 1
                    files += 1
                }
                else -> { files += 1 }
            }
        }
        liveDirs.value = dirs
        liveFiles.value = files
        liveImages.value = images

        liveShow.value = isShowType.toString()
        binding.tvImgs.visibility = if(images > 0 && isShowType == ShowType.All) View.VISIBLE else View.GONE
        binding.tvImgsName.visibility = if(images > 0 && isShowType == ShowType.All) View.VISIBLE else View.GONE
    }

    fun onClickHome() {
        Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
        refreshDir(isHome = true)
    }

    fun onClickGrid() = Unit

    fun onClickAll() = refreshDir(isShowType = ShowType.All)

    fun onClickImage() = refreshDir(isShowType = ShowType.Img)

    fun onClickZip() = refreshDir(isShowType = ShowType.Zip)

    fun onClickDoc() = refreshDir(isShowType = ShowType.Doc)

    companion object {
        const val TAG = "FileManagerActivity"
        const val REQUEST_CODE = 1
    }
}