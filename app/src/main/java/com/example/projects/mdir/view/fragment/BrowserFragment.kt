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

class BrowserFragment : Fragment() {

    private lateinit var binding : LayoutBrowserBinding
    private lateinit var activity : Activity

    val livePath = MutableLiveData<String>()
    val liveDirs = MutableLiveData<Int>()
    val liveFiles = MutableLiveData<Int>()
    val liveImages = MutableLiveData<Int>()
    val liveShow = MutableLiveData<String>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = getActivity() as FileManagerActivity
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

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BrowserFragment()
        }
    }
}