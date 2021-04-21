package com.example.projects.mdir.view.base

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.projects.mdir.FileManagerActivity
import com.example.projects.mdir.common.FragmentType

open class BaseFragment : Fragment() {

    private lateinit var activity : FileManagerActivity

    var fragmentType = FragmentType.None

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach")
        activity = getActivity() as FileManagerActivity
        super.onAttach(context)
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
        activity.showFragment = fragmentType
    }

    open fun initUi() {}

    companion object {
        private const val TAG = "[DE][FR] BASE"
    }
}