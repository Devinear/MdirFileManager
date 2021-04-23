package com.example.projects.mdir.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.example.projects.R
import com.example.projects.R.drawable.outline_arrow_downward_white_24
import com.example.projects.R.drawable.outline_arrow_upward_white_24
import com.example.projects.databinding.LayoutFindBinding
import com.example.projects.mdir.FileManagerActivity
import com.example.projects.mdir.common.FragmentType
import com.example.projects.mdir.view.base.BaseFragment
import kotlinx.android.synthetic.main.activity_file_manager.*
import kotlinx.android.synthetic.main.activity_file_manager.view.*

class FindFragment : BaseFragment() {

    private lateinit var binding : LayoutFindBinding
    private lateinit var searchView : androidx.appcompat.widget.SearchView

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
        binding.fragment = this
        return binding.root
    }

    override fun initUi() {
        Log.d(TAG, "initUi")
        fragmentType = FragmentType.Find
        super.initUi()

        searchView = (activity as FileManagerActivity).toolbar.search
        searchView.visibility = View.VISIBLE
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun changeFilterView() {
        Log.d(TAG, "changeFilterView")
        val drawable = if(binding.laFilter.layout.visibility == View.VISIBLE) {
            binding.laFilter.layout.visibility = View.GONE
            requireContext().getDrawable(outline_arrow_upward_white_24)
        }
        else {
            binding.laFilter.layout.visibility = View.VISIBLE
            requireContext().getDrawable(outline_arrow_downward_white_24)
        }
        binding.btFilter.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        searchView.visibility = View.GONE
    }

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            FindFragment()
        }
        private const val TAG = "[DE][FR] Find"
    }
}