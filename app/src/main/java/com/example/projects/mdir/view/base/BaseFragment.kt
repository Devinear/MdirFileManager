package com.example.projects.mdir.view.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.projects.mdir.FileManagerActivity
import com.example.projects.mdir.common.FragmentType

open class BaseFragment : Fragment() {

    private lateinit var activity : FileManagerActivity

    var fragmentType = FragmentType.None

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach")
        super.onAttach(context)
        activity = getActivity() as FileManagerActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
        activity.showFragment = fragmentType
    }

    open fun initUi() {
        // 메뉴 초기화 작업
        activity.invalidateOptionsMenu()
    }

    companion object {
        private const val TAG = "[DE][FR] BASE"
    }
}