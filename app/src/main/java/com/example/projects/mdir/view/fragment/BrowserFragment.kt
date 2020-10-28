package com.example.projects.mdir.view.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
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
import com.example.projects.mdir.view.BrowserData

class BrowserFragment : Fragment() {

    companion object {
        private const val PATH = "path"
        private const val TYPE = "type"

//        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
//            BrowserFragment()
//        }

        fun newInstance(type: BrowserType, path: String = "") = BrowserFragment().apply {
            arguments = Bundle().apply {
                putParcelable(TYPE, BrowserData(type))
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

    val browserType by lazy { requireArguments().getParcelable<BrowserData>(TYPE)?.type?:BrowserType.None }
    val browserPath by lazy { requireArguments().getString(PATH)?:"" }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = getActivity() as FileManagerActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_browser,
            container,
            false
        )
        return binding.root
    }

    fun onClickHome() = Unit


}