package com.example.projects.mdir

import android.app.Application
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.example.projects.mdir.common.*
import com.example.projects.mdir.data.FileItemEx
import com.example.projects.mdir.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class FileViewModel(val app: Application) : AndroidViewModel(app) {

    private val _files = MutableLiveData<List<FileItemEx>>()
    val files: LiveData<List<FileItemEx>>
        get() = _files

    private val categoryFiles = mutableListOf<FileItemEx>()

    var needRefresh : Boolean = false

    private val _openDir = MutableLiveData<FileItemEx>()
    val openDir: LiveData<FileItemEx>
        get() = _openDir

    private val _mode = MutableLiveData<BrowserType>()
    val mode: LiveData<BrowserType>
        get() = _mode
    private var _category : Category? = null
    val category: Category?
        get() = _category

    private var _depthDir = MutableLiveData<List<String>>()
    val depthDir: LiveData<List<String>>
        get() = _depthDir

    private var _showOption = MutableLiveData<FileItemEx?>()
    val showOption: LiveData<FileItemEx?>
        get() = _showOption

    private var _showSystem : Boolean = false
    val showSystem: Boolean
        get() = _showSystem

    val favorites = mutableListOf<String>()

    private val repository: AbsStorageRepository by lazy {
        LegacyStorageRepository.INSTANCE
    }

    private var rootUri: Uri = Uri.EMPTY

    init {
        _mode.value = BrowserType.Storage
        initFavorite()

        viewModelScope.launch(Dispatchers.IO) {
            repository.initRepository()
        }
    }

    fun clearFileItem() {
        Log.d(TAG, "clearFileItem")
        _files.value = null
    }

    fun requestRefreshSort() {
        Log.d(TAG, "requestRefreshSort")
        viewModelScope.launch(Dispatchers.Main) {
            val list = _files.value?.toMutableList() ?: return@launch
            sort(list)
            _files.postValue(list)
        }
    }

    private fun sort(list: MutableList<FileItemEx>) {
        val fir = Sort.sortPairFir
        val sec = Sort.sortPairSec

        list.sortWith(kotlin.Comparator { o1, o2 ->
            innerComparator(o1, o2, fir).let {
                // 1차 정렬 결과가 동등하다면 2차 정렬을 실행함.
                if(it != 0)
                    return@Comparator it
                else
                    return@Comparator innerComparator(o1, o2, sec)
            }
        })

    }

    private fun innerComparator(o1: FileItemEx, o2: FileItemEx, sort: Pair<SortBy, SortOrder>) : Int {
        if(o1.parent == o2.absolutePath) return 1
        else if(o1.absolutePath == o2.parent) return -1

        val ascending = if(sort.second == SortOrder.Ascending) 1 else -1
        return when(sort.first) {
            /*is*/ SortBy.Name -> o1.name.toLowerCase().compareTo(o2.name.toLowerCase()) * ascending
            /*is*/ SortBy.Date -> (o1.lastModified() - o2.lastModified()).toInt() * ascending
            /*is*/ SortBy.Size -> (o1.length() - o2.length()).toInt() * ascending
            /*is*/ SortBy.Type -> (o1.exType.sort - o2.exType.sort) * ascending
            else/*is SortBy.Favorite*/ -> {
                val _o1 = o1.favorite.value ?: false
                val _o2 = o2.favorite.value ?: false
                when {
                    _o1 && _o2  -> 0
                    _o1 && !_o2 -> ascending
                    else        -> -ascending
                }
            }

        }
    }

    fun loadCategory(category: Category, isShowSystem: Boolean = _showSystem) {
        Log.d(TAG, "loadCategory")
        viewModelScope.launch(Dispatchers.Main) {
            Log.d(TAG, "loadCategory viewModelScope")

            if(category == Category.Download) {
                loadDirectory(FileUtil.LEGACY_DOWNLOAD)
                return@launch
            }
            _mode.value = BrowserType.Category
            _category = category

//            val listCategory = if(needRefresh || files.value?.isEmpty() != false) {
//                repository.loadDirectory(
//                    context = app, path = FileUtil.LEGACY_ROOT, category = category, isShowSystem = _showSystem)
//            }
//            else {
//                files.value?.toMutableList() ?: mutableListOf()
//            }
//            val listCategory = repository.loadDirectory(
//                    context = app, path = FileUtil.LEGACY_ROOT, category = category, refresh = false
//            )
//            favorites.forEach { favorite ->
//                listCategory.find { it.absolutePath == favorite }?.favorite?.value = true
//            }
            categoryFiles.clear()
            categoryFiles.addAll(repository.request(context = app, category = category))
//            val listCategory = repository.request(context = app, category = category)

            sort(categoryFiles)
            _files.postValue(categoryFiles)

            // DEPTHS
            _depthDir.postValue( MutableList(1) { category.name } )

            requestThumbnail(categoryFiles)
//            withContext(Dispatchers.IO) { requestThumbnail(listCategory) }
//            Thread { requestThumbnail(listCategory) }.start()
        }
    }

    fun loadDirectory(path: String = "", isShowSystem: Boolean = _showSystem) {
        viewModelScope.launch(Dispatchers.Main) {
            _mode.value = BrowserType.Storage
            _category = null

            categoryFiles.clear()

            rootUri = if(path == "") { File(FileUtil.LEGACY_ROOT).toUri() } else { File(path).toUri() }
            val curPath : String = rootUri.path?:FileUtil.LEGACY_ROOT
            val list = repository.loadDirectory(context = app, path = curPath, refresh = false)

            // FAVORITE
            favorites.forEach { favorite ->
                list.find { it.absolutePath == favorite }?.favorite?.value = true
            }
            sort(list)

            // 최상위 폴더가 아닌 경우 UP_DIR TYPE Item 추가
            if(curPath != FileUtil.LEGACY_ROOT) {
                list.add(0, FileItemEx(path = curPath, isUpDir = true))
            }
            _files.postValue(list)

            // DEPTHS
            _depthDir.postValue(changedDepth(depth = curPath))

            requestThumbnail(list)
        }
    }

    private fun clickDirectory(directory: FileItemEx, isUpDir: Boolean = false) {
        viewModelScope.launch(Dispatchers.Main) {

            if(mode.value == BrowserType.Category) {
                // Category는 무조건 깊이가 1이다.
                if(isUpDir) {
                    _files.postValue(categoryFiles)
                    _depthDir.postValue( listOf(_category?.name?:"") )
                }
                else {
                    val list = directory.subFiles.apply {
                        removeAll { item -> return@removeAll item.exType == FileType.UpDir }
                        add(0, FileItemEx(path = directory.absolutePath, isUpDir = true))
                    }
                    sort(list)
                    _files.postValue(list)
                    requestThumbnail(list)

                    _category ?: return@launch
                    _depthDir.postValue( listOf(_category!!.name, directory.simpleName) )
                }
            }
            else {
                // isUpDir 경우 실제로는 directory.up이 자기자신이 된다.
                val target = if (isUpDir) directory.self ?: directory else directory
                val targetUp = target.parentDir
                val list = target.subFiles

                // FAVORITE
                favorites.forEach { favorite ->
                    list.find { it.absolutePath == favorite }?.favorite?.value = true
                }
                _files.postValue(list.apply {
                    removeAll { item -> return@removeAll item.exType == FileType.UpDir }
//                if(isUpDir)
//                    add(0, FileItemEx(path = directory.absolutePath, isUpDir = true).apply { self = directory })
//                else
                    if (targetUp != null) {
                        add(0, FileItemEx(path = targetUp.absolutePath, isUpDir = true).apply { self = targetUp })
                    }
                })
                requestThumbnail(list)

                // DEPTHS
                _depthDir.postValue(changedDepth(depth = target.absolutePath))

            }
        }
    }

    private fun changedDepth(depth: String) = depth.substringAfter(FileUtil.LEGACY_ROOT)
            .split('/')
            .toMutableList()
            .apply { removeIf { it.isEmpty() } }

    fun loadFavorite() {
        viewModelScope.launch(Dispatchers.Main) {
            val list = mutableListOf<FileItemEx>()
            favorites.forEach { list.add(FileItemEx(path = it)) }
            _files.postValue(list)
            _depthDir.postValue(List(1) { "Favorite" })
            requestThumbnail(list)
        }
    }

    private fun requestThumbnail(list: MutableList<FileItemEx>) {
        Log.d(TAG, "requestThumbnail")
        viewModelScope.launch(Dispatchers.IO) {
            list.forEach {
                if(it.exType == FileType.UpDir)
                    return@forEach

                var image: BitmapDrawable? = null
                val type = FileUtil.getFileExtType(it.extension)
                if (it.isDirectory) {
                    val subList = it.listFiles { subFile -> subFile.isFile && FileUtil.getFileExtType(subFile.extension) == ExtType.Image }
                    subList?.takeIf { subList.isNotEmpty() }?.apply {
                        image = ThumbnailRepository.INSTANCE.requestDrawable(app, subList[0].absolutePath)
                    }
                } else if (type == ExtType.Image || type == ExtType.Video || type == ExtType.Audio) {
                    image = ThumbnailRepository.INSTANCE.requestDrawable(app, it.absolutePath, type)
                }
                it.liveDrawable.postValue(image)
            }
        }
    }

   fun requestThumbnailFavorite(list: MutableList<FileItemEx>) {
       requestThumbnail(list)
    }

    fun requestHome(browserType: BrowserType = BrowserType.Storage) {
        if(browserType == BrowserType.Storage)
            loadDirectory(FileUtil.LEGACY_ROOT)
    }

    fun requestDepth(depth: String) {
        val request = "${FileUtil.LEGACY_ROOT}/${depth}".apply {
            replace("//", "/")
        }
        loadDirectory(request)
    }

    fun requestShowSystem(isShowSystem: Boolean) {
        _showSystem = isShowSystem
        loadDirectory(rootUri.path?:FileUtil.LEGACY_ROOT, _showSystem)
    }

    fun requestClickItem(item: FileItemEx) {
        when (item.exType) {
//            // 부모 폴더
//            FileType.UpDir -> {loadDirectory(item.parent)}
//            // 일반 폴더
//            FileType.Dir -> {loadDirectory(item.path, false)}
            // 폴더
            FileType.UpDir, FileType.Dir -> clickDirectory(directory = item, isUpDir = item.exType == FileType.UpDir)
            // 일반 파일
            else -> execute(item)
        }
    }

    fun requestPath(item: FileItemEx) {
        when (item.exType) {
            // 부모 폴더
            FileType.UpDir -> { loadDirectory(item.parent) }
            // 일반 폴더
            FileType.Dir -> { loadDirectory(item.path, false) }
            // 일반 파일
            else -> execute(item)
        }
    }

    fun requestShowOption(item: FileItemEx?) {
        item?.run {
            _showOption.postValue(this)
        } ?: run {
            _showOption.value = null
        }
    }

    private fun execute(item: FileItemEx) {
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

    fun requestCommand(command: Command, item: FileItemEx) {
        when(command) {
            Command.Favorite -> favorite(item)
            Command.Rename -> { }
            Command.Share -> share(item)
            Command.Info -> { }
            Command.Delete -> { }
        }
    }

    private fun favorite(item: FileItemEx) {
        val favorite : Boolean = !(item.favorite.value ?: false)
        item.favorite.postValue(favorite)

        when {
            favorite -> {
                FavoriteRepository.INSTANCE.add(item = item)
                favorites.add(item.absolutePath)
            }
            else -> {
                FavoriteRepository.INSTANCE.remove(path = item.absolutePath)
                favorites.remove(item.absolutePath)
            }
        }
    }

    private fun share(item: FileItemEx) {
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
        }, "Share: ${item.name}").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun initFavorite() {
        FavoriteRepository.INSTANCE.getAll(this)
    }

    fun responseFavoriteList(list: List<String>) {
        favorites.clear()
        favorites.addAll(list)
    }

    companion object {
        private const val TAG = "[DE][VM] ViewModel"
    }
}