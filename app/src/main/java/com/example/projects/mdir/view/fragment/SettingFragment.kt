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
import com.example.projects.databinding.LayoutSettingBinding
import com.example.projects.mdir.FileManagerActivity
import com.example.projects.mdir.common.Setting

class SettingFragment : Fragment() {

    private lateinit var binding : LayoutSettingBinding
    private lateinit var activity : Activity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = getActivity() as FileManagerActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_setting,
            container,
            false
        )
        with(binding) {
            fragment = this@SettingFragment
            ckbShowFile.isChecked = !Setting.hideSystem
        }
        return binding.root
    }

    fun onCheckedShowFiles() {
        Setting.hideSystem = !binding.ckbShowFile.isChecked
    }

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SettingFragment()
        }
    }
}