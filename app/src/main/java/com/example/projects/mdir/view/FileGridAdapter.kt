package com.example.projects.mdir.view

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.ViewDataBinding
import com.example.projects.databinding.ItemGridFileBinding
import com.example.projects.mdir.data.FileItem
import com.example.projects.mdir.view.base.BaseAdapter
import com.example.projects.mdir.view.base.BaseViewHolder

class FileGridAdapter(private val context: Context) : BaseAdapter(baseContext = context) {

    class ViewHolder(private val binding: ItemGridFileBinding) : BaseViewHolder(viewDataBinding = binding as ViewDataBinding) {

        override fun onBind(context: Context, item: FileItem, color: Int) {
            binding.run {
                tvName.text = item.name
                tvName.setTextColor(color)

                if(item.drawable != null) {
                    ivImage.setImageDrawable(item.drawable)
                    ivImage.scaleType = ImageView.ScaleType.CENTER_CROP
                }
                else {
                    ivImage.setImageResource(item.type.drawableRes)
                    ivImage.scaleType = ImageView.ScaleType.CENTER
                }
            }
        }

        override fun onTouch(context: Context, event: MotionEvent, item: FileItem) {
            binding.run {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        tvName.setTextColor(context.getColor(android.R.color.black))
                        root.setBackgroundResource(item.type.color)
                    }
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                        tvName.setTextColor(context.getColor(item.type.color))
                        root.setBackgroundResource(android.R.color.black)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(ItemGridFileBinding.inflate(LayoutInflater.from(parent.context), parent, false))
}