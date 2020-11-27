package com.example.projects.mdir.view.base

import android.content.Context
import android.view.MotionEvent
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.projects.mdir.data.FileItemEx

abstract class BaseViewHolder(val viewDataBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewDataBinding.root) {
    abstract fun onBind(context: Context, item: FileItemEx, color: Int)
    abstract fun onTouch(context: Context, event: MotionEvent, item: FileItemEx)
}