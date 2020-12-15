package com.example.projects.mdir.view.base

import android.content.Context
import android.view.MotionEvent
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.projects.databinding.ItemGridFileBinding
import com.example.projects.databinding.ItemLinearFileBinding
import com.example.projects.mdir.FileViewModel
import com.example.projects.mdir.data.FileItemEx
import kotlinx.android.synthetic.main.item_linear_file.view.*

abstract class BaseViewHolder(
        val viewDataBinding: ViewDataBinding,
        val viewModel: FileViewModel
) : RecyclerView.ViewHolder(viewDataBinding.root) {

    init {
        itemView.setOnClickListener {
            val item = (viewDataBinding as? ItemLinearFileBinding)?.item ?: (viewDataBinding as? ItemGridFileBinding)?.item ?: return@setOnClickListener
            viewModel.requestClickItem(item)
        }
        itemView.setOnLongClickListener {
            val item = (viewDataBinding as? ItemLinearFileBinding)?.item ?: (viewDataBinding as? ItemGridFileBinding)?.item ?: return@setOnLongClickListener false
            viewModel.requestLongClickItem(item)
            return@setOnLongClickListener true
        }
        itemView.setOnTouchListener { v, event ->
            val item = (viewDataBinding as? ItemLinearFileBinding)?.item ?: (viewDataBinding as? ItemGridFileBinding)?.item ?: return@setOnTouchListener false
            onTouch(v.context, event, item)
            return@setOnTouchListener false
        }
    }
  
    abstract fun onBind(context: Context, item: FileItemEx, color: Int)
    abstract fun onTouch(context: Context, event: MotionEvent, item: FileItemEx)
}