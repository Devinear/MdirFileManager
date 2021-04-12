package com.example.projects.mdir.view.fragment

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projects.R
import com.example.projects.databinding.LayoutBrowserBinding
import com.example.projects.mdir.FileManagerActivity
import com.example.projects.mdir.FileViewModel
import com.example.projects.mdir.common.BrowserType
import com.example.projects.mdir.common.Category
import com.example.projects.mdir.common.Image
import com.example.projects.mdir.common.LayoutType
import com.example.projects.mdir.data.FileItemEx
import com.example.projects.mdir.listener.RequestListener
import com.example.projects.mdir.view.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_file_manager.*
import kotlinx.android.synthetic.main.layout_browser.*

class BrowserFragment : Fragment() {

    companion object {
        private const val TAG = "[DE][FR] BROWSER"
        private const val PATH = "path"
        private const val TYPE = "type"
        private val GRID_ITEM_WIDTH_DP = Image.widthGridItemDp

//        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
//            BrowserFragment()
//        }

        fun newInstance(type: BrowserType, category: Category? = null, path: String = "") = BrowserFragment().apply {
            arguments = Bundle().apply {
                putParcelable(TYPE, BrowserData(type, category))
                putString(PATH, path)
            }
        }
    }


    private lateinit var binding : LayoutBrowserBinding
    private lateinit var activity : Activity
    private lateinit var viewModel : FileViewModel
    var requestListener : RequestListener? = null

    // TARGET API 29 이상인 경우 사용할 수 없다. 외부 저장소 정책이 애플과 동일해진다.
    private val adapterLinear by lazy { FileLinearAdapter(activity, viewModel) }
    private val adapterGrid by lazy { FileGridAdapter(activity, viewModel) }
    private var layoutType = LayoutType.Linear

    val livePath = MutableLiveData<String>()
    val liveDirs = MutableLiveData<Int>()
    val liveFiles = MutableLiveData<Int>()
    val liveImages = MutableLiveData<Int>()
    val liveShow = MutableLiveData<String>()

    private val browserData by lazy { requireArguments().getParcelable<BrowserData>(TYPE) }
    private val browserPath by lazy { requireArguments().getString(PATH)?:"" }

    private var _browserType = BrowserType.Storage
    val browserType : BrowserType
        get() = _browserType


    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach")
        super.onAttach(context)
        activity = getActivity() as FileManagerActivity
        activity.invalidateOptionsMenu()

        // FileManagerActivity 에서 생성한 ViewModel과 LifeCycle 을 공유하는 동일한 ViewModel
        // ViewModel은 내부적으로 싱글턴이다.
        viewModel = ViewModelProvider(activity as ViewModelStoreOwner).get(FileViewModel::class.java)
    }

    override fun onDetach() {
        Log.d(TAG, "onDetach")
        super.onDetach()
        viewModel.clearFileItem()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_browser,
            container,
            false
        )
        binding.apply {
            // Binding에 LifeCycleOwner을 지정해줘야 LiveData가 실시간으로 변경된다.
            lifecycleOwner = this@BrowserFragment
            fragment = this@BrowserFragment
            viewModel = this@BrowserFragment.viewModel

            owner = lifecycleOwner

            recycler.adapter = adapterLinear
            recycler.layoutManager = LinearLayoutManager(activity)
            layoutType = LayoutType.Linear

            laInfo.visibility = if(browserData?.type == BrowserType.Storage) View.VISIBLE else View.GONE
        }

        _browserType = browserData?.type?:BrowserType.Storage
        when(_browserType) {
            BrowserType.Category -> {
                viewModel.loadCategory(browserData?.category?:Category.Image)
            }
            BrowserType.Favorite -> {
                viewModel.loadFavorite()
            }
            else/*BrowserType.Storage*/ -> {
                viewModel.loadDirectory(browserPath)
            }
        }

        (activity as FileManagerActivity).apply {
            appbar.setExpanded(false, true)

            liveShowType.removeObservers(viewLifecycleOwner)
            liveShowType.observe(viewLifecycleOwner, Observer { changeViewMode(isListMode = it) })
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
//        with(binding.progress) {
//            show()
//            isActivated = true
//            visibility = View.VISIBLE
//        }

        showProgress()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    private val snackBar : Snackbar by lazy { Snackbar.make(binding.root, "SNACK BAR", Snackbar.LENGTH_LONG) }
    private var optionDialog : FileOptionDialog? = null

    private fun observeViewModel() {
        Log.d(TAG, "observeViewModel")
        with(viewModel) {
            showOption.observe(viewLifecycleOwner, Observer {
//                if(snackBar.isShown) {
//                    snackBar.dismiss()
//                }
//
//                // 이동, 복사, 상세정보, 공유, 삭제
//
//                // 즐겨찾기 설정
//                // 이름변경
//                val layout : Snackbar.SnackbarLayout = snackBar.view as Snackbar.SnackbarLayout
//                layout.removeAllViews()
//
//                // 기존의 SnackBar가 아닌 CustomView를 연결
//                val customView = FileSnackBar(context = app, item = files.value!![0], path = FileUtil.LEGACY_ROOT).view
//                layout.setPadding(0, 0, 0, 0)
//                layout.addView(customView, 0)
//
//                snackBar.show()

//                optionDialog?.dismiss()
//                optionDialog?: run { FileOptionDialog(context = requireContext(), viewModel = viewModel, item = it).apply {
//                    optionDialog = this
//                } }.run {
//                    show()
//                }

                it ?: return@Observer
                FileOptionDialog(context = requireContext(), viewModel = viewModel, file = it).run { show() }

            })
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        when (layoutType) {
            LayoutType.Linear -> {
                // Adapter를 새로 설정하면 레이아웃을 새로 구성한다.
                // Linear의 경우 port/land가 다른 레이아웃이다.
                binding.recycler.adapter = adapterLinear
            }
            else -> {
                if(binding.recycler.layoutManager is GridLayoutManager) {
                    val spanCount : Int = newConfig.screenWidthDp / GRID_ITEM_WIDTH_DP +1
                    (binding.recycler.layoutManager as GridLayoutManager).spanCount = spanCount
                }
            }
        }
    }

    private fun changeViewMode(isListMode: Boolean) {
        Log.d(TAG, "changeViewMode ListMode:$isListMode")
        layoutType = if( isListMode ) LayoutType.Linear else LayoutType.Grid
        with(binding.recycler) {
            when(layoutType) {
                LayoutType.Linear -> {
                    layoutManager = LinearLayoutManager(activity)
                    adapter = adapterLinear.apply {
                        setFileItems(adapterGrid.items)
//                        isPortrait = adapterGrid.isPortrait
                    }
                }
                LayoutType.Grid -> {
                    layoutManager = GridLayoutManager(activity,
                        resources.configuration.screenWidthDp / GRID_ITEM_WIDTH_DP +1)
                    adapter = adapterGrid.apply {
                        setFileItems(adapterLinear.items)
//                        isPortrait = adapterLinear.isPortrait
                    }
                }
            }
        }
    }

    private fun showOption(item: FileItemEx) {
//                if(snackBar.isShown) {
//                    snackBar.dismiss()
//                }
//
//                // 이동, 복사, 상세정보, 공유, 삭제
//
//                // 즐겨찾기 설정
//                // 이름변경
//                val layout : Snackbar.SnackbarLayout = snackBar.view as Snackbar.SnackbarLayout
//                layout.removeAllViews()
//
//                // 기존의 SnackBar가 아닌 CustomView를 연결
//                val customView = FileSnackBar(context = app, item = files.value!![0], path = FileUtil.LEGACY_ROOT).view
//                layout.setPadding(0, 0, 0, 0)
//                layout.addView(customView, 0)
//
//                snackBar.show()
    }

    fun setItemsFinished() {
        showProgress(show = false)
        tv_empty.visibility = if(recycler.adapter?.itemCount?:0 == 0) View.VISIBLE else View.GONE
        tv_empty.bringToFront()
    }

    fun showProgress(show : Boolean = true) {
        Log.d(TAG, "showProgress Show:$show")
        requestListener?.onRequestProgressBar(show = show)
    }
    fun hideProgress() {
        Log.d(TAG, "hideProgress")
        requestListener?.onRequestProgressBar(show = false)
//        with(binding.progress) {
//            isActivated = false
//            hide()
//        }
    }

}