package com.example.projects.mdir.view.fragment

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.example.projects.mdir.FileManagerActivity

class SettingFragment : Fragment() {

//    private lateinit var binding : LayoutBrowserBinding
    private lateinit var activity : Activity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = getActivity() as FileManagerActivity
    }

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SettingFragment()
        }
    }
}