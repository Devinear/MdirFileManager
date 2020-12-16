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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projects.R
import com.example.projects.databinding.LayoutBrowserBinding
import com.example.projects.mdir.FileManagerActivity
import com.example.projects.mdir.FileViewModel
import com.example.projects.mdir.common.BrowserType
import com.example.projects.mdir.common.Category
import com.example.projects.mdir.common.LayoutType
import com.example.projects.mdir.view.BrowserData
import com.example.projects.mdir.view.FileGridAdapter
import com.example.projects.mdir.view.FileLinearAdapter

class BrowserFragment : Fragment() {

    companion object {
        private const val TAG = "[FR] BROWSER"
        private const val PATH = "path"
        private const val TYPE = "type"
        private const val GRID_ITEM_WIDTH_DP = 120

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

    private val viewModel by lazy {
        ViewModelProvider(this).get(FileViewModel::class.java)
    }

    private lateinit var binding : LayoutBrowserBinding
    private lateinit var activity : Activity

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

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach")
        super.onAttach(context)
        activity = getActivity() as FileManagerActivity
        activity.invalidateOptionsMenu()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

            recycler.adapter = adapterLinear
            recycler.layoutManager = LinearLayoutManager(activity)
            layoutType = LayoutType.Linear

            laInfo.visibility = if(browserData?.type == BrowserType.Storage) View.VISIBLE else View.GONE
        }

        if(browserData?.type == BrowserType.Storage)
            viewModel.loadDirectory()
        else
            viewModel.loadCategory(browserData?.category?:Category.Image)

        (activity as FileManagerActivity).liveShowType.apply {
            removeObservers(viewLifecycleOwner)
            observe(viewLifecycleOwner, Observer { changeViewMode(isListMode = it) })
        }
        return binding.root
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

}