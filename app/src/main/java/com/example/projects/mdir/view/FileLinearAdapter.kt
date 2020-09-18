package com.example.projects.mdir.view

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.example.projects.databinding.ItemFileBinding
import com.example.projects.mdir.common.FileType
import com.example.projects.mdir.common.FileUtil
import com.example.projects.mdir.data.FileItem
import com.example.projects.mdir.view.base.BaseAdapter
import com.example.projects.mdir.view.base.BaseViewHolder

class FileLinearAdapter(private val context: Context) : BaseAdapter(baseContext = context) {

    class ViewHolder(private val binding: ItemFileBinding) : BaseViewHolder(viewDataBinding = binding as ViewDataBinding) {

        override fun onBind(item: FileItem, color: Int, isPortrait: Boolean) {
            binding.run {
                tvName.text = item.name
                tvTime.text = item.time
                if((item.type == FileType.Dir) or (item.type == FileType.UpDir)) {
                    tvType.text = item.type.abbr
                    tvSize.text = ""
                }
                else {
                    tvType.text = item.ext
                    tvSize.text = FileUtil.getFileSize(item.byteSize)
                }
                tvName.setTextColor(color)
                tvType.setTextColor(color)
                tvSize.visibility = if(isPortrait) View.GONE else View.VISIBLE
                tvTime.visibility = if(isPortrait) View.GONE else if(item.type == FileType.UpDir) View.INVISIBLE else View.VISIBLE
            }
        }

        override fun onTouch(context: Context, event: MotionEvent, item: FileItem) {
            binding.run {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        tvName.setTextColor(context.getColor(android.R.color.black))
                        tvType.setTextColor(context.getColor(android.R.color.black))
                        root.setBackgroundResource(item.type.color)
                    }
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                        tvName.setTextColor(context.getColor(item.type.color))
                        tvType.setTextColor(context.getColor(item.type.color))
                        root.setBackgroundResource(android.R.color.black)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false))
}