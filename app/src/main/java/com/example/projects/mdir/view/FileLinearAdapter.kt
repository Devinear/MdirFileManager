package com.example.projects.mdir.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.projects.databinding.ItemFileBinding
import com.example.projects.mdir.common.FileType
import com.example.projects.mdir.common.FileUtil
import com.example.projects.mdir.data.FileItem
import com.example.projects.mdir.listener.OnFileClickListener
import com.example.projects.mdir.view.base.BaseViewHolder

class FileLinearAdapter(private val context: Context) : RecyclerView.Adapter<FileLinearAdapter.ViewHolder>() {

    private val items = mutableListOf<FileItem>()
    var isPortrait = true // ORIENTATION_PORTRAIT

    var clickListener : OnFileClickListener? = null

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

    override fun getItemCount(): Int = items.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder Position:$position Portrait:$isPortrait")
        with(holder) {
            val item : FileItem =  items[position]
            val color = context.getColor(item.type.color)

            onBind(item, color, isPortrait)

            itemView.setOnClickListener { clickListener?.onClickFileItem(item) }
            itemView.setOnTouchListener { _, event ->
                onTouch(context, event, item)
                return@setOnTouchListener false
            }
        }
    }

    fun setFileItems(list: List<FileItem>) {
        Log.d(TAG, "setFileItems")
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    companion object {
        const val TAG = "FileLinearAdapter"
    }

}