package com.example.projects.mdir.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.projects.R
import com.example.projects.databinding.LayoutSettingBinding
import com.example.projects.mdir.common.FragmentType
import com.example.projects.mdir.common.Setting
import com.example.projects.mdir.view.base.BaseFragment

class SettingFragment : BaseFragment() {

    private lateinit var binding : LayoutSettingBinding

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

    override fun initUi() {
        super.initUi()
        fragmentType = FragmentType.Setting
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