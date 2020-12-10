package com.example.projects.mdir

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.projects.mdir.common.BrowserType
import com.example.projects.mdir.common.FileUtil
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

//    private val _category = MutableLiveData<Category>()
//    val category: LiveData<Category>
//        get() = _category

    private val repository: AbsStorageRepository by lazy {
        LegacyStorageRepository()
    }

    private var rootUri: Uri = Uri.EMPTY
//    private val rootUri: Uri by lazy { File(FileUtil.ROOT).toUri() }

    init {
        _mode.value = BrowserType.Storage
//        rootUri = FileItemEx(FileUtil.ROOT).toUri()
    }

    fun onClickStorage() {
        repository.loadDirectory(app, FileUtil.LEGACY_ROOT)

        Toast.makeText(app, "STORAGE", Toast.LENGTH_SHORT).show()
    }

    fun loadDirectory(path: String = "") {
        viewModelScope.launch {
            rootUri = if(path == "") { File(FileUtil.LEGACY_ROOT).toUri() } else { File(path).toUri() }

            // Dispatchers.IO ??
            _files.postValue(repository.loadDirectory(app, rootUri.path?:FileUtil.LEGACY_ROOT))
        }
    }

    fun requestClickItem(item: FileItemEx) {

    }

    fun requestLongClickItem(item: FileItemEx) {

    }
}