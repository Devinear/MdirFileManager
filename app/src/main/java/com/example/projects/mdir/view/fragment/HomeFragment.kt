package com.example.projects.mdir.view.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.projects.R
import com.example.projects.databinding.LayoutHomeBinding
import com.example.projects.mdir.FileManagerActivity

class HomeFragment : Fragment() {

    private lateinit var binding : LayoutHomeBinding
    private lateinit var activity : Activity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = getActivity() as FileManagerActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        binding = DataBindingUtil.setContentView(activity, R.layout.layout_home)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_home,
            container,
            false
        )
        return binding.root
    }

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            HomeFragment()
        }
    }
}