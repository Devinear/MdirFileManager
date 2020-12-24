package com.example.projects.mdir.view

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.ViewDataBinding
import com.example.projects.databinding.ItemGridFileBinding
import com.example.projects.mdir.FileViewModel
import com.example.projects.mdir.data.FileItemEx
import com.example.projects.mdir.view.base.BaseAdapter
import com.example.projects.mdir.view.base.BaseViewHolder

class FileGridAdapter(private val context: Context, val viewModel: FileViewModel) : BaseAdapter(baseContext = context) {

    class ViewHolder(
            private val binding: ItemGridFileBinding,
            viewModel: FileViewModel
            ) : BaseViewHolder(
            viewDataBinding = binding as ViewDataBinding,
            viewModel = viewModel
    ) {

        override fun onBind(context: Context, item: FileItemEx, color: Int) {
            binding.run {
                this.item = item
                tvName.text = item.simpleName
                tvName.setTextColor(color)

                if(item.liveDrawable.value != null) {
                    ivImage.setImageDrawable(item.liveDrawable.value)
                    ivImage.scaleType = ImageView.ScaleType.CENTER_CROP
                }
                else {
                    ivImage.setImageResource(item.exType.drawableRes)
                    ivImage.scaleType = ImageView.ScaleType.CENTER
                }
            }
        }

        override fun onTouch(context: Context, event: MotionEvent, item: FileItemEx) {
            binding.run {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        tvName.setTextColor(context.getColor(android.R.color.black))
                        root.setBackgroundResource(item.exType.color)
                    }
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                        tvName.setTextColor(context.getColor(item.exType.color))
                        root.setBackgroundResource(android.R.color.black)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    = ViewHolder(
            ItemGridFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            , viewModel)
}