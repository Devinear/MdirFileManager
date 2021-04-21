package com.example.projects.mdir.view.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.projects.R
import com.example.projects.databinding.LayoutFindBinding
import com.example.projects.mdir.common.FragmentType
import com.example.projects.mdir.view.base.BaseFragment

class FindFragment : BaseFragment() {
    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            FindFragment()
        }
    }

    private lateinit var binding : LayoutFindBinding
    private lateinit var activity : Activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_find,
            container,
            false
        )
        return binding.root
    }

    override fun initUi() {
        super.initUi()
        fragmentType = FragmentType.Find
    }
}