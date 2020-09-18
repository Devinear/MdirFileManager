package com.example.projects.mdir.view

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.example.projects.databinding.ItemGridFileBinding
import com.example.projects.mdir.data.FileItem
import com.example.projects.mdir.view.base.BaseAdapter
import com.example.projects.mdir.view.base.BaseViewHolder

class FileGridAdapter(private val context: Context) : BaseAdapter(baseContext = context) {

    class ViewHolder(private val binding: ItemGridFileBinding) : BaseViewHolder(viewDataBinding = binding as ViewDataBinding) {

        override fun onBind(item: FileItem, color: Int, isPortrait: Boolean) {
            binding.run {
                tvName.text = item.name
                if(item.drawable != null) {
                    ivImage.setImageDrawable(item.drawable)
                }
                tvName.setTextColor(color)
            }
        }

        override fun onTouch(context: Context, event: MotionEvent, item: FileItem) {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(ItemGridFileBinding.inflate(LayoutInflater.from(parent.context), parent, false))
}