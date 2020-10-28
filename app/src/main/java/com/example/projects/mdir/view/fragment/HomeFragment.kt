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
import com.example.projects.mdir.view.HomeViewModel

class HomeFragment : Fragment() {

    private lateinit var binding : LayoutHomeBinding
    private lateinit var viewModel : HomeViewModel
    private lateinit var activity : Activity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = HomeViewModel(context = context)
        activity = getActivity() as FileManagerActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_home,
            container,
            false
        )
        binding.vm = viewModel
        return binding.root
    }

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            HomeFragment()
        }
    }
}