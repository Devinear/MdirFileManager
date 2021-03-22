package com.example.projects.mdir

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.ContentLoadingProgressBar
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.example.projects.R
import com.example.projects.mdir.common.*
import com.example.projects.mdir.data.FileItemEx
import com.example.projects.mdir.listener.RequestListener
import com.example.projects.mdir.view.fragment.BrowserFragment
import com.example.projects.mdir.view.fragment.FindFragment
import com.example.projects.mdir.view.fragment.HomeFragment
import com.example.projects.mdir.view.fragment.SettingFragment
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_file_manager.*
import kotlinx.android.synthetic.main.activity_file_manager.view.*
import kotlinx.coroutines.*

class FileManagerActivity : AppCompatActivity(R.layout.activity_file_manager),/* AppBarLayout.OnOffsetChangedListener,*/ ViewModelStoreOwner, RequestListener {

    private val viewModelStore = ViewModelStore()
    private val viewModel by lazy {
        ViewModelProvider(this).get(FileViewModel::class.java)
    }

    // UI
    val livePath = MutableLiveData<String>()
    val liveDirs = MutableLiveData<Int>()
    val liveFiles = MutableLiveData<Int>()
    val liveImages = MutableLiveData<Int>()
    val liveShow = MutableLiveData<String>()

    // Value
    private val listFileItem = ObservableArrayList<FileItemEx>()
    private var currentPath: String = ""
    private var isHideShow : Boolean = false
    private var layoutType = LayoutType.Linear

    private var showFragment = FragmentType.None

//    private val snackBar : Snackbar by lazy { Snackbar.make(binding.root, "SNACK BAR", Snackbar.LENGTH_LONG) }

    private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val appbar by lazy { findViewById<AppBarLayout>(R.id.appbar) }
    private val progress by lazy { findViewById<ContentLoadingProgressBar>(R.id.progress) }
//    private val progress2 by lazy { findViewById<ProgressBar>(R.id.progress2) }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        checkPermission()

        initUi()
    }

    private fun initUi() {
        toolbar.apply {
//            setBackgroundColor(ContextCompat.getColor(context, R.color.colorHomeLayout))
            setTitleTextColor(ContextCompat.getColor(context, R.color.colorHomeText))
        }.also {
            setSupportActionBar(it.apply { title = "Retro File" }   )
        }

        // Favorite, Recent 둘중 하나라도 0인 경우 확장으로 시작하자
//        if(viewModel.favorites.size == 0 || viewModel.recent.size == 0)
//            appbar.setExpanded(true)
//        else
//            appbar.setExpanded(false)
        appbar.setExpanded(true)
        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val range = (-appBarLayout!!.totalScrollRange).toFloat()
            iv_toolbar.imageAlpha = ((255 * (1.0f - verticalOffset.toFloat() / range)).toInt())
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(showFragment == FragmentType.Home) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelStore.clear()
    }

    /* ViewModelStoreOwner */
    override fun getViewModelStore(): ViewModelStore = viewModelStore

    /* AppBarLayout.OnOffsetChangedListener */
//    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
////        TODO("Not yet implemented")
//        val range = (-appBarLayout!!.totalScrollRange).toFloat()
//        iv_toolbar.imageAlpha = ((255 * (1.0f - verticalOffset.toFloat() / range)).toInt())
//    }

    private lateinit var menu: Menu
//    var isShowList = true
    val liveShowType = MutableLiveData<Boolean>()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(menu == null) return false
        this.menu = menu
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        // Browser Fragment 보기 모드 변경 메뉴
        if(showFragment == FragmentType.Browser) {
            menu?.findItem(R.id.action_find)?.isVisible = true
            menu?.findItem(R.id.action_list)?.isVisible = liveShowType.value ?: false
            menu?.findItem(R.id.action_grid)?.isVisible = !(liveShowType.value ?: false)
        }
        else {
            menu?.findItem(R.id.action_find)?.isVisible = showFragment == FragmentType.Home
            menu?.findItem(R.id.action_list)?.isVisible = false
            menu?.findItem(R.id.action_grid)?.isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_list -> {
                liveShowType.postValue(true)
                menu.findItem(R.id.action_list)?.isVisible = false
                menu.findItem(R.id.action_grid)?.isVisible = true
                true
            }
            R.id.action_grid -> {
                liveShowType.postValue(false)
                menu.findItem(R.id.action_list)?.isVisible = true
                menu.findItem(R.id.action_grid)?.isVisible = false
                true
            }
            R.id.action_find -> {
                changeFragment(FragmentType.Find)
                true
            }
            R.id.action_settings -> {
                changeFragment(FragmentType.Browser)
                true
            }
            android.R.id.home -> {
                changeFragment(FragmentType.Home)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkPermission() {
        val permissions = Array(1) { "android.permission.WRITE_EXTERNAL_STORAGE" }
        if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
        }
        else {
            changeFragment()
        }
    }

    private fun changeFragment(type: FragmentType = FragmentType.Home, browserType: BrowserType = BrowserType.Storage, category: Category? = null, path: String = "") {
//        requestProgress(isShow = true)
        showFragment = type

        when {
            showFragment != FragmentType.Home -> {

                // Visible을 바꾸는건 자연스럽지 못하다...
//                appbar.iv_toolbar.visibility = View.INVISIBLE
//                appbar.setExpanded(false, true)

//                GlobalScope.launch {
//                    delay(1000L)
//                    appbar.setExpanded(false, true)
//                    appbar.iv_toolbar.visibility = View.VISIBLE
//                }
            }
        }

        // 모든 Fragment 제거
        supportFragmentManager.fragments.removeAll { true }

        supportFragmentManager.beginTransaction().apply {
            when(showFragment) {
                FragmentType.Home -> {
                    replace(R.id.fragment_container,
                            HomeFragment.INSTANCE.apply {
                                requestListener = this@FileManagerActivity }
                    )
                }
                FragmentType.Browser -> {
                    when(browserType) {
                        BrowserType.Find, BrowserType.Recent, BrowserType.Favorite -> {
                            replace(R.id.fragment_container, BrowserFragment.newInstance(type = browserType))
                        }
                        BrowserType.Category -> {
                            category
                                ?.run { replace(R.id.fragment_container,
                                        BrowserFragment.newInstance(type = BrowserType.Category, category = category)
                                                .apply { requestListener = this@FileManagerActivity })
                                }
                                ?:run { replace(R.id.fragment_container,
                                        BrowserFragment.newInstance(type = BrowserType.Storage)
                                                .apply { requestListener = this@FileManagerActivity })
                                }
                        }
                        else -> {
                            replace(R.id.fragment_container,
                                    BrowserFragment.newInstance(type = BrowserType.Storage, path = path)
                                            .apply { requestListener = this@FileManagerActivity }
                            )
                        }
                    }
                    val showList = browserType != BrowserType.Category || category == Category.Download
                    liveShowType.postValue(showList)
                }
                FragmentType.Find -> {
                    replace(R.id.fragment_container, FindFragment.INSTANCE)
                }
                FragmentType.Setting -> {
                    replace(R.id.fragment_container, SettingFragment.INSTANCE)
                }
            }
            when (showFragment) {
                FragmentType.Home -> {
                    addToBackStack(null)
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
                else -> {
                    addToBackStack(HomeFragment.toString())
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
            }
        }.commit()
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
            changeFragment()
//            updateFileList()
        }
    }

    override fun onRequestProgressBar(show : Boolean) {
        Log.d(TAG, "onRequestProgressBar show:$show")
        progress.visibility = if(show) View.VISIBLE else View.GONE
//        progress2.visibility = View.VISIBLE


//        when(show) {
//            true -> progress.show()
//            false -> progress.hide()
//        }
    }

    override fun onRequestStoragePath(path: String) {
        requestStorage(path = path)
    }

//    override fun onClickFile(item: FileItemEx) {
//        when (item.exType) {
//            FileType.UpDir -> {
//                if(currentPath == FileUtil.LEGACY_ROOT) {
//                    Toast.makeText(this, "최상위 폴더입니다.", Toast.LENGTH_SHORT).show()
//                    return
//                }
//                currentPath = FileUtil.getUpDirPath(currentPath)
//                livePath.value = "..${currentPath.removePrefix(FileUtil.LEGACY_ROOT)}"
//                updateFileList()
//            }
//            FileType.Dir -> {
////                currentPath = "$currentPath/${item.name}"
//                currentPath = item.path
//                livePath.value = "..${currentPath.removePrefix(FileUtil.LEGACY_ROOT)}"
//                updateFileList()
//            }
//            else -> {
//                // 일반 파일
//                val intent = Intent().apply {
//                    action = Intent.ACTION_VIEW
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK // setFlags
//                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    setDataAndType(
//                        FileProvider.getUriForFile(this@FileManagerActivity,
//                            "${this@FileManagerActivity.packageName}.provider", File("$currentPath/${item.name}.${item.extension}")),
//                        FileUtil.getMimeType(item.extension))
//                }
//
//                // 해당 Intent를 처리할 수 있는 앱이 있는지 확인
//                val resolveInfo : List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)
//                val isIntentSafe = resolveInfo.isNotEmpty()
//                if(isIntentSafe)
//                    startActivity(intent)
//                else
//                    Toast.makeText(this, item.name, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    override fun onLongClickFile(item: FileItemEx) {
//        // 이동, 복사, 상세정보, 공유, 삭제
//
//        // 즐겨찾기 설정
//        // 이름변경
////        val layout : Snackbar.SnackbarLayout = snackBar.view as Snackbar.SnackbarLayout
//////        layout.findViewById<TextView>(R.id.snackbar_text).visibility = View.INVISIBLE
////        layout.removeAllViews()
////
////        // 기존의 SnackBar가 아닌 CustomView를 연결
////        val customView = FileSnackBar(context = this, item = item, path = currentPath).view
////        layout.setPadding(0, 0, 0, 0)
////        layout.addView(customView, 0)
////
////        snackBar.show()
//    }

    private fun updateFileList(isHome: Boolean = false, isShowType: ShowType = ShowType.All, isFavorite: Boolean = false) {
        Log.d(TAG, "updateFileList IsHome:$isHome IsShowType:$isShowType")
//        if(currentPath.isEmpty() || isHome) {
//            currentPath = FileUtil.ROOT
//            livePath.value = ".."
//        }
//
//        listFileItem.clear()
//        if(!isFavorite) {
//            listFileItem.addAll(FileUtil.getChildFileItems(this, currentPath, isHideShow, isShowType))
//        }
//        else {
//            val items = mutableListOf<FileItemEx>()
//            FavoriteRepository.INSTANCE.getAll().forEach {
//                val item = FileUtil.convertFileItem(context = this, path = it)
//                if(item != null)
//                    items.add(item)
//            }
//            listFileItem.addAll(items)
//        }
//
//        var dirs = 0
//        var files = 0
//        var images = 0
//        listFileItem.forEach {
//            when(it.type) {
//                FileType.UpDir -> { }
//                FileType.Dir -> { dirs += 1 }
//                FileType.Image -> {
//                    images += 1
//                    files += 1
//                }
//                else -> { files += 1 }
//            }
//        }
//        liveDirs.value = dirs
//        liveFiles.value = files
//        liveImages.value = images
//
//        liveShow.value = isShowType.toString()
//        binding.tvImgs.visibility = if(images > 0 && isShowType == ShowType.All) View.VISIBLE else View.GONE
//        binding.tvImgsName.visibility = if(images > 0 && isShowType == ShowType.All) View.VISIBLE else View.GONE
    }

    fun onClickHome() {
        Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
        updateFileList(isHome = true)
    }

    fun onClickHiddenFolder() {
        isHideShow = !isHideShow
        updateFileList()
    }

    fun onClickFavorite() {
        // 즐겨찾기 폴더
//        Toast.makeText(this, "Favorite", Toast.LENGTH_SHORT).show()
        updateFileList(isFavorite = true)
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
//        when (layoutType) {
//            LayoutType.Linear -> {
//                layoutType = LayoutType.Grid
//                with(binding) {
//                    btGrid.text = "GRID"
//                    recycler.layoutManager = GridLayoutManager( this@FileManagerActivity, resources.configuration.screenWidthDp / GRID_ITEM_WIDTH_DP +1)
//                    recycler.adapter = adapterGrid.apply {
//                        clickListener = this@FileManagerActivity
//                        isPortrait = adapterLinear.isPortrait
//                        updateFileList()
//                    }
//                }
//            }
//            else -> {
//                layoutType = LayoutType.Linear
//                with(binding) {
//                    btGrid.text = "LIST"
//                    recycler.layoutManager = LinearLayoutManager(this@FileManagerActivity)
//                    recycler.adapter = adapterLinear.apply {
//                        clickListener = this@FileManagerActivity
//                        isPortrait = adapterGrid.isPortrait
//                        updateFileList()
//                    }
//                }
//            }
//        }
    }

    fun requestStorage(path: String = "") {
        // 차후에 드라이브를 추가하게되면 타입을 늘리자.
        Log.d(TAG, "requestStorage path:$path")
        changeFragment(type = FragmentType.Browser, browserType = BrowserType.Storage, path = path)
    }

    fun requestCategory(type: Category) {
        Log.d(TAG, "requestCategory type:${type.name}")
        changeFragment(type = FragmentType.Browser, browserType = BrowserType.Category, category = type)
    }

    fun onClickAll() = updateFileList(isShowType = ShowType.All)

    fun onClickImage() = updateFileList(isShowType = ShowType.Img)

    fun onClickZip() = updateFileList(isShowType = ShowType.Zip)

    fun onClickDoc() = updateFileList(isShowType = ShowType.Doc)

    fun requestExtendFavorite() {
        // Favorite Empty 예외
        if(viewModel.favorites.isNotEmpty())
            changeFragment(type = FragmentType.Browser, browserType = BrowserType.Favorite)
    }

    companion object {
        const val GRID_ITEM_WIDTH_DP = 120
        const val TAG = "[DE] Activity"
        const val REQUEST_CODE = 1
    }
}