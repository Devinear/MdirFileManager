package com.example.projects.mdir.view.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.projects.R
import com.example.projects.databinding.LayoutBrowserBinding
import com.example.projects.mdir.FileManagerActivity
import com.example.projects.mdir.common.BrowserType
import com.example.projects.mdir.common.Category
import com.example.projects.mdir.view.BrowserData

class BrowserFragment : Fragment() {

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

    private lateinit var binding : LayoutBrowserBinding
    private lateinit var activity : Activity

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

            laInfo.visibility = if(browserData?.type == BrowserType.Storage) View.VISIBLE else View.GONE
        }
        livePath.value = if (browserData?.type == BrowserType.Storage) "$browserPath" else "> ${browserData?.category?.name}"
        return binding.root
    }

    fun onClickHome() = Unit

}