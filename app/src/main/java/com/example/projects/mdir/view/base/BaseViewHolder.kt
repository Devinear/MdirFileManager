package com.example.projects.mdir.view.base

import android.content.Context
import android.view.MotionEvent
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.projects.mdir.data.FileItem

abstract class BaseViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
    abstract fun onBind(item: FileItem, color: Int, isPortrait: Boolean)
    abstract fun onTouch(context: Context, event: MotionEvent, item: FileItem)
}