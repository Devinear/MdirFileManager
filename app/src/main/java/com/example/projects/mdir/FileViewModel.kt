package com.example.projects.mdir

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projects.mdir.common.BrowserType
import com.example.projects.mdir.common.FileUtil
import com.example.projects.mdir.data.FileItem
import com.example.projects.mdir.repository.AbsStorageRepository
import com.example.projects.mdir.repository.LegacyStorageRepository

class FileViewModel(val app: Application) : AndroidViewModel(app) {

    private val _files = MutableLiveData<List<FileItem>>()
    val files: LiveData<List<FileItem>>
        get() = _files

    private val _openDir = MutableLiveData<FileItem>()
    val openDir: LiveData<FileItem>
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

    init {
        _mode.value = BrowserType.Storage
    }

    fun onClickStorage() {
        repository.loadDirectory(app, FileUtil.ROOT)

        Toast.makeText(app, "STORAGE", Toast.LENGTH_SHORT).show()
    }

}