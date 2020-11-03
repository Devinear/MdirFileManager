package com.example.projects.mdir

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.example.projects.R
import com.example.projects.mdir.common.*
import com.example.projects.mdir.data.FileItem
import com.example.projects.mdir.listener.OnFileClickListener
import com.example.projects.mdir.view.FileGridAdapter
import com.example.projects.mdir.view.FileLinearAdapter
import com.example.projects.mdir.view.fragment.BrowserFragment
import com.example.projects.mdir.view.fragment.FindFragment
import com.example.projects.mdir.view.fragment.HomeFragment
import com.example.projects.mdir.view.fragment.SettingFragment
import com.google.android.material.appbar.AppBarLayout
import java.io.File

class FileManagerActivity : AppCompatActivity(R.layout.activity_file_manager), OnFileClickListener, AppBarLayout.OnOffsetChangedListener, ViewModelStoreOwner {

//    private lateinit var binding : LayoutFileManagerBinding
//    private lateinit var binding : ActivityFileManagerBinding

    private val viewModelStore = ViewModelStore()
    private lateinit var viewModel: FileViewModel
//    private val viewModel : FileViewModel by lazy {
//        ViewModelProviders.of(this).get(FileViewModel::class.java)
//    }

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

    private var showFragment = FragmentType.Home

//    private val snackBar : Snackbar by lazy { Snackbar.make(binding.root, "SNACK BAR", Snackbar.LENGTH_LONG) }

    private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val appbar by lazy { findViewById<AppBarLayout>(R.id.appbar) }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        checkPermission()

//        binding = DataBindingUtil.setContentView(this, R.layout.activity_file_manager)


//        binding = DataBindingUtil.setContentView(this, R.layout.layout_file_manager)
//        binding.apply {
//            recycler.adapter = adapterLinear
//            recycler.layoutManager = LinearLayoutManager(this@FileManagerActivity)
//            layoutType = LayoutType.Linear
//
//            // Binding에 LifeCycleOwner을 지정해줘야 LiveData가 실시간으로 변경된다.
//            lifecycleOwner = this@FileManagerActivity
//            activity = this@FileManagerActivity
//
//            // Bind Item
//            items = listFileItem
//        }
//
//        adapterLinear.apply {
//            clickListener = this@FileManagerActivity
//            isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
//            updateFileList()
//        }

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(FileViewModel::class.java)

        initUi()
    }

    private fun initUi() {
        toolbar.apply {
//            setBackgroundColor(ContextCompat.getColor(context, R.color.colorHomeLayout))
            setTitleTextColor(ContextCompat.getColor(context, R.color.colorHomeText))
        }.also {
            setSupportActionBar(it.apply {
                title = "Retro File"
            })
        }

        appbar.setExpanded(false)
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
    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        TODO("Not yet implemented")
    }

    private lateinit var menu: Menu
    var isShowList = true

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(menu == null) return false
        this.menu = menu
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        // Browser Fragment 보기 모드 변경 메뉴
        if(showFragment == FragmentType.Home || showFragment == FragmentType.Find) {
            menu?.findItem(R.id.action_list)?.isVisible = false
            menu?.findItem(R.id.action_grid)?.isVisible = false
        }
        else {
            menu?.findItem(R.id.action_list)?.isVisible = !isShowList
            menu?.findItem(R.id.action_grid)?.isVisible = isShowList
        }
        menu?.findItem(R.id.action_find)?.isVisible =
            (showFragment != FragmentType.Find) && (showFragment != FragmentType.Setting)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_list -> {
                isShowList = true
                menu.findItem(R.id.action_list)?.isVisible = false
                menu.findItem(R.id.action_grid)?.isVisible = true
                true
            }
            R.id.action_grid -> {
                isShowList = false
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

    private fun changeFragment(type: FragmentType = FragmentType.Home) {
        showFragment = type
        supportFragmentManager.beginTransaction().apply {
            when(showFragment) {
                FragmentType.Home -> {
                    replace(R.id.fragment_container, HomeFragment.INSTANCE)
                    addToBackStack(null)
                    supportActionBar?.setDisplayHomeAsUpEnabled(false) // BackKey 비활성화
                }
                FragmentType.Browser -> {
                    replace(R.id.fragment_container, BrowserFragment.newInstance(BrowserType.Category))
                    addToBackStack(HomeFragment.toString())
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
                FragmentType.Find -> {
                    replace(R.id.fragment_container, FindFragment.INSTANCE)
                    addToBackStack(HomeFragment.toString())
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
                FragmentType.Setting -> {
                    replace(R.id.fragment_container, SettingFragment.INSTANCE)
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
//        Log.d(TAG, "onConfigurationChanged ORIENTATION:${newConfig.orientation}")
//        when (layoutType) {
//            LayoutType.Linear -> {
//                adapterLinear.isPortrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT
//                adapterLinear.notifyDataSetChanged()
//            }
//            else -> {
//                adapterGrid.isPortrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT
//                adapterGrid.notifyDataSetChanged()
//                if(binding.recycler.layoutManager is GridLayoutManager) {
//                    val spanCount : Int = newConfig.screenWidthDp / GRID_ITEM_WIDTH_DP +1
//                    (binding.recycler.layoutManager as GridLayoutManager).spanCount = spanCount
//                }
//            }
//        }
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
                updateFileList()
            }
            FileType.Dir -> {
//                currentPath = "$currentPath/${item.name}"
                currentPath = item.path
                livePath.value = "..${currentPath.removePrefix(FileUtil.ROOT)}"
                updateFileList()
            }
            else -> {
                // 일반 파일
                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK // setFlags
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    setDataAndType(
                        FileProvider.getUriForFile(this@FileManagerActivity,
                            "${this@FileManagerActivity.packageName}.provider", File("$currentPath/${item.name}.${item.ext}")),
                        FileUtil.getMimeType(item.ext))
                }

                // 해당 Intent를 처리할 수 있는 앱이 있는지 확인
                val resolveInfo : List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)
                val isIntentSafe = resolveInfo.isNotEmpty()
                if(isIntentSafe)
                    startActivity(intent)
                else
                    Toast.makeText(this, item.name, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onLongClickFile(item: FileItem) {
        // 이동, 복사, 상세정보, 공유, 삭제

        // 즐겨찾기 설정
        // 이름변경
//        val layout : Snackbar.SnackbarLayout = snackBar.view as Snackbar.SnackbarLayout
////        layout.findViewById<TextView>(R.id.snackbar_text).visibility = View.INVISIBLE
//        layout.removeAllViews()
//
//        // 기존의 SnackBar가 아닌 CustomView를 연결
//        val customView = FileSnackBar(context = this, item = item, path = currentPath).view
//        layout.setPadding(0, 0, 0, 0)
//        layout.addView(customView, 0)
//
//        snackBar.show()
    }

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
//            val items = mutableListOf<FileItem>()
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

    fun requestStorage() {
        changeFragment(FragmentType.Browser)
    }

    fun onClickAll() = updateFileList(isShowType = ShowType.All)

    fun onClickImage() = updateFileList(isShowType = ShowType.Img)

    fun onClickZip() = updateFileList(isShowType = ShowType.Zip)

    fun onClickDoc() = updateFileList(isShowType = ShowType.Doc)

    companion object {
        const val GRID_ITEM_WIDTH_DP = 120
        const val TAG = "FileManagerActivity"
        const val REQUEST_CODE = 1
    }
}