package com.example.projects.mdir.view.base

import android.content.Context
import android.view.MotionEvent
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.projects.mdir.data.FileItem

abstract class BaseViewHolder(val viewDataBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewDataBinding.root) {
    abstract fun onBind(context: Context, item: FileItem, color: Int)
    abstract fun onTouch(context: Context, event: MotionEvent, item: FileItem)
}