package com.example.projects.mdir

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projects.R
import com.example.projects.databinding.LayoutFileManagerBinding
import com.example.projects.mdir.common.*
import com.example.projects.mdir.data.FileItem
import com.example.projects.mdir.listener.OnFileClickListener
import com.example.projects.mdir.view.FileGridAdapter
import com.example.projects.mdir.view.FileLinearAdapter
import java.io.File

class FileManagerActivity : AppCompatActivity(), OnFileClickListener {

    private lateinit var binding : LayoutFileManagerBinding

    // TARGET API 29 이상인 경우 사용할 수 없다. 외부 저장소 정책이 애플과 동일해진다.
    private val adapterLinear = FileLinearAdapter(this)
    private val adapterGrid = FileGridAdapter(this)

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
    private var layoutType = LayoutType.Linear

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        checkPermission()

        binding = DataBindingUtil.setContentView(this, R.layout.layout_file_manager)
        binding.apply {
            recycler.adapter = adapterLinear
            recycler.layoutManager = LinearLayoutManager(this@FileManagerActivity)
            layoutType = LayoutType.Linear

            // Binding에 LifeCycleOwner을 지정해줘야 LiveData가 실시간으로 변경된다.
            lifecycleOwner = this@FileManagerActivity
            activity = this@FileManagerActivity

            // Bind Item
            items = listFileItem
        }

        adapterLinear.apply {
            clickListener = this@FileManagerActivity
            isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
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
        when (layoutType) {
            LayoutType.Linear -> {
                adapterLinear.isPortrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT
                adapterLinear.notifyDataSetChanged()
            }
            else -> {
                adapterGrid.isPortrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT
                adapterGrid.notifyDataSetChanged()
                if(binding.recycler.layoutManager is GridLayoutManager) {
                    val spanCount : Int = newConfig.screenWidthDp / GRID_ITEM_WIDTH_DP +1
                    (binding.recycler.layoutManager as GridLayoutManager).spanCount = spanCount
                }
            }
        }
    }

    override fun onClickFile(item: FileItem) {
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

    override fun onLongClickFile(item: FileItem) {
        Toast.makeText(this, "onLongClickFile - ${item.name}", Toast.LENGTH_SHORT).show()
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

    fun onClickHiddenFolder() {
        isHideShow = !isHideShow
        refreshDir()
    }

    fun onClickFavorite() {
        // 즐겨찾기 폴더
        Toast.makeText(this, "Favorite", Toast.LENGTH_SHORT).show()
    }

    fun onClickSetting() {
        // 숨겨진 시스템 파일 표시
        // 시작 폴더 설정
        Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show()
    }

    fun onClickFind() {
        // 검색 기능
        Toast.makeText(this, "Find", Toast.LENGTH_SHORT).show()
    }

    fun onClickGrid() {
        when (layoutType) {
            LayoutType.Linear -> {
                layoutType = LayoutType.Grid
                with(binding) {
                    btGrid.text = "GRID"
                    recycler.layoutManager = GridLayoutManager( this@FileManagerActivity, resources.configuration.screenWidthDp / GRID_ITEM_WIDTH_DP +1)
                    recycler.adapter = adapterGrid.apply {
                        clickListener = this@FileManagerActivity
                        isPortrait = adapterLinear.isPortrait
                        refreshDir()
                    }
                }
            }
            else -> {
                layoutType = LayoutType.Linear
                with(binding) {
                    btGrid.text = "LIST"
                    recycler.layoutManager = LinearLayoutManager(this@FileManagerActivity)
                    recycler.adapter = adapterLinear.apply {
                        clickListener = this@FileManagerActivity
                        isPortrait = adapterGrid.isPortrait
                        refreshDir()
                    }
                }
            }
        }
    }

    fun onClickAll() = refreshDir(isShowType = ShowType.All)

    fun onClickImage() = refreshDir(isShowType = ShowType.Img)

    fun onClickZip() = refreshDir(isShowType = ShowType.Zip)

    fun onClickDoc() = refreshDir(isShowType = ShowType.Doc)

    companion object {
        const val GRID_ITEM_WIDTH_DP = 150
        const val TAG = "FileManagerActivity"
        const val REQUEST_CODE = 1
    }
}