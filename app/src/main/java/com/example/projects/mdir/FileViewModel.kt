package com.example.projects.mdir

import android.app.Application
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.projects.mdir.common.*
import com.example.projects.mdir.data.FileItemEx
import com.example.projects.mdir.repository.AbsStorageRepository
import com.example.projects.mdir.repository.LegacyStorageRepository
import kotlinx.coroutines.launch
import java.io.File

class FileViewModel(val app: Application) : AndroidViewModel(app) {

    private val _files = MutableLiveData<List<FileItemEx>>()
    val files: LiveData<List<FileItemEx>>
        get() = _files

    private val _openDir = MutableLiveData<FileItemEx>()
    val openDir: LiveData<FileItemEx>
        get() = _openDir

    private val _mode = MutableLiveData<BrowserType>()
    val mode: LiveData<BrowserType>
        get() = _mode

    private var _isShowSystem : Boolean = false
    val isShowSystem: Boolean
        get() = _isShowSystem

    private var _depthDir = MutableLiveData<List<String>>()
    val depthDir: LiveData<List<String>>
        get() = _depthDir

    private val _category = MutableLiveData<List<FileItemEx>>()
    val category: LiveData<List<FileItemEx>>
        get() = _category

    private val repository: AbsStorageRepository by lazy {
        LegacyStorageRepository()
    }

    private var rootUri: Uri = Uri.EMPTY

    init {
        _mode.value = BrowserType.Storage
    }

    fun loadCategory(category: Category, isShowSystem: Boolean = _isShowSystem) {
        viewModelScope.launch {
            if(category == Category.Download) {
                loadDirectory(FileUtil.LEGACY_DOWNLOAD)
                return@launch
            }

            val listCategory = repository.loadDirectory(
                    context = app, path = FileUtil.LEGACY_ROOT, category = category, isShowSystem = _isShowSystem
            )
            _files.postValue(listCategory)

            // DEPTHS
            _depthDir.postValue( MutableList(1) { category.name } )
        }
    }

    fun loadDirectory(path: String = "", isShowSystem: Boolean = _isShowSystem) {
        viewModelScope.launch {
            rootUri = if(path == "") { File(FileUtil.LEGACY_ROOT).toUri() } else { File(path).toUri() }
            val curPath : String = rootUri.path?:FileUtil.LEGACY_ROOT

            val list = repository.loadDirectory(app, curPath, _isShowSystem)

            // 최상위 폴더가 아닌 경우 UP_DIR TYPE Item 추가
            if(curPath != FileUtil.LEGACY_ROOT) {
                list.add(0, FileItemEx(path = curPath, isUpDir = true))
            }
            _files.postValue(list)

            // DEPTHS
            val depths = curPath.substringAfter(FileUtil.LEGACY_ROOT)
                    .split('/')
                    .toMutableList()
                    .apply { removeIf { it.isEmpty() } }
            _depthDir.postValue(depths)
        }
    }

    fun requestHome() {
        loadDirectory(FileUtil.LEGACY_ROOT)
    }

    fun requestDepth(depth: String) {
        val request = "${FileUtil.LEGACY_ROOT}/${depth}".apply {
            replace("//", "/")
        }
        loadDirectory(request)
    }

    fun requestShowSystem(isShowSystem: Boolean) {
        _isShowSystem = isShowSystem
        loadDirectory(rootUri.path?:FileUtil.LEGACY_ROOT, _isShowSystem)
    }

    fun requestClickItem(item: FileItemEx) {
        when (item.exType) {
            // 부모 폴더
            FileType.UpDir -> { loadDirectory(item.parent) }
            // 일반 폴더
            FileType.Dir -> { loadDirectory(item.path, false) }
            // 일반 파일
            else -> requestExecute(item)
        }
    }

    fun requestExecute(item: FileItemEx) {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            flags = Intent.FLAG_ACTIVITY_NEW_TASK // setFlags
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            setDataAndType(
                FileProvider.getUriForFile(app, "${app.packageName}.provider", item),
                FileUtil.getMimeType(item.extension)
            )
        }

        // 해당 Intent를 처리할 수 있는 앱이 있는지 확인
        val resolveInfo : List<ResolveInfo> = app.packageManager.queryIntentActivities(intent, 0)
        when(resolveInfo.isNotEmpty()) {
            true -> app.startActivity(intent)
            else -> Toast.makeText(app, item.name, Toast.LENGTH_SHORT).show()
        }
    }

    fun requestShare(item: FileItemEx) {
        // java.lang.ClassCastException: java.net.URI cannot be cast to android.os.Parcelable 발생
        app.startActivity(Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            type = when (FileUtil.getFileExtType(item.extension)) {
                ExtType.Image -> "image/*"
                ExtType.Video -> "video/*"
                ExtType.Audio -> "audio/*"
                else -> "application/*"
            }
            putExtra(Intent.EXTRA_STREAM, File(item.absolutePath).toURI())
        }, "Share: ${item.name}"))
    }

    fun requestLongClickItem(item: FileItemEx) = Unit
}