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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projects.R
import com.example.projects.databinding.LayoutBrowserBinding
import com.example.projects.mdir.FileManagerActivity
import com.example.projects.mdir.FileViewModel
import com.example.projects.mdir.common.BrowserType
import com.example.projects.mdir.common.Category
import com.example.projects.mdir.common.LayoutType
import com.example.projects.mdir.data.FileItemEx
import com.example.projects.mdir.listener.OnFileClickListener
import com.example.projects.mdir.view.BrowserData
import com.example.projects.mdir.view.FileGridAdapter
import com.example.projects.mdir.view.FileLinearAdapter

class BrowserFragment : Fragment(), OnFileClickListener {

    companion object {
        private const val TAG = "[FR] BROWSER"
        private const val PATH = "path"
        private const val TYPE = "type"

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
        adapterLinear.apply {
            clickListener = this@BrowserFragment
            isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
//            updateFileList()
        }

        livePath.value = if (browserData?.type == BrowserType.Storage) browserPath else "> ${browserData?.category?.name}"
        viewModel.onClickStorage()

        return binding.root
    }

    override fun onClickFile(item: FileItemEx) {
        TODO("Not yet implemented")
    }

    override fun onLongClickFile(item: FileItemEx) {
        TODO("Not yet implemented")
    }

    fun onClickHome() = Unit

}