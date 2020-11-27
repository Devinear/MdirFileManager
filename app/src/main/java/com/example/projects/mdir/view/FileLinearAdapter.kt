package com.example.projects.mdir.view

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.ViewDataBinding
import com.example.projects.R
import com.example.projects.databinding.ItemLinearFileBinding
import com.example.projects.mdir.common.FileType
import com.example.projects.mdir.common.FileUtil
import com.example.projects.mdir.data.FileItemEx
import com.example.projects.mdir.view.base.BaseAdapter
import com.example.projects.mdir.view.base.BaseViewHolder

class FileLinearAdapter(private val context: Context) : BaseAdapter(baseContext = context) {

    class ViewHolder(private val binding: ItemLinearFileBinding) : BaseViewHolder(viewDataBinding = binding as ViewDataBinding) {

        override fun onBind(context: Context, item: FileItemEx, color: Int) {
            binding.run {
                tvName.text = item.name
                tvTime.text = item.exTime
                when (item.exType) {
                    FileType.UpDir -> {
                        tvType.text = item.exType.abbr
                        tvSize.text = ""
                    }
                    FileType.Dir -> {
                        tvType.text = item.exType.abbr
                        tvSize.apply {
                            val child = item.listFiles()?.size?:0
                            text = if(child > 1) "$child" + context.getString(R.string.sub_items) else "$child" + context.getString(R.string.sub_item)
                        }
                    }
                    else -> {
                        tvType.text = item.extension
                        tvSize.text = FileUtil.getFileSize(item.length())
                    }
                }

                if(item.drawable != null) {
                    ivImage?.setImageDrawable(item.drawable)
                    ivImage?.scaleType = ImageView.ScaleType.CENTER_CROP
                }
                else {
                    ivImage?.setImageResource(item.exType.drawableRes)
                    ivImage?.scaleType = ImageView.ScaleType.CENTER
                }

                tvName.setTextColor(color)
                tvType.setTextColor(color)
                tvTime.visibility = if(item.exType == FileType.UpDir) View.INVISIBLE else View.VISIBLE
            }
        }

        override fun onTouch(context: Context, event: MotionEvent, item: FileItemEx) {
            binding.run {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        tvName.setTextColor(context.getColor(android.R.color.black))
                        tvType.setTextColor(context.getColor(android.R.color.black))
                        root.setBackgroundResource(item.exType.color)
                    }
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                        tvName.setTextColor(context.getColor(item.exType.color))
                        tvType.setTextColor(context.getColor(item.exType.color))
                        root.setBackgroundResource(android.R.color.black)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(ItemLinearFileBinding.inflate(LayoutInflater.from(parent.context), parent, false))
}