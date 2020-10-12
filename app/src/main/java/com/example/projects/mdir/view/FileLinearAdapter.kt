package com.example.projects.mdir.view

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.example.projects.R
import com.example.projects.databinding.ItemLinearFileBinding
import com.example.projects.mdir.common.FileType
import com.example.projects.mdir.common.FileUtil
import com.example.projects.mdir.data.FileItem
import com.example.projects.mdir.view.base.BaseAdapter
import com.example.projects.mdir.view.base.BaseViewHolder

class FileLinearAdapter(private val context: Context) : BaseAdapter(baseContext = context) {

    class ViewHolder(private val binding: ItemLinearFileBinding) : BaseViewHolder(viewDataBinding = binding as ViewDataBinding) {

        override fun onBind(context: Context, item: FileItem, color: Int) {
            binding.run {
                tvName.text = item.name
                tvTime.text = item.time
                if((item.type == FileType.Dir) or (item.type == FileType.UpDir)) {
                    tvType.text = item.type.abbr
                    val child = item.childCount
                    tvSize.text = if(child > 1) "$child" + context.getString(R.string.sub_items) else "$child" + context.getString(R.string.sub_item)
                }
                else {
                    tvType.text = item.ext
                    tvSize.text = FileUtil.getFileSize(item.byteSize)
                }
                tvName.setTextColor(color)
                tvType.setTextColor(color)
                tvTime.visibility = if(item.type == FileType.UpDir) View.INVISIBLE else View.VISIBLE
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
            = ViewHolder(ItemLinearFileBinding.inflate(LayoutInflater.from(parent.context), parent, false))
}