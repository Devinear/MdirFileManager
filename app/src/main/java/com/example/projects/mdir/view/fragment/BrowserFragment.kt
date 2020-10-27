package com.example.projects.mdir.view.fragment

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData

class BrowserFragment : Fragment() {

    // UI
    val livePath = MutableLiveData<String>()
    val liveDirs = MutableLiveData<Int>()
    val liveFiles = MutableLiveData<Int>()
    val liveImages = MutableLiveData<Int>()
    val liveShow = MutableLiveData<String>()

    fun onClickHome() = Unit
}