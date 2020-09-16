package com.example.projects.mdir.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projects.databinding.ItemFileBinding
import com.example.projects.mdir.common.FileType
import com.example.projects.mdir.common.FileUtil
import com.example.projects.mdir.data.FileItem
import com.example.projects.mdir.listener.OnFileClickListener

class FileLinearAdapter(private val context: Context) : RecyclerView.Adapter<FileLinearAdapter.ViewHolder>() {

    private val items = mutableListOf<FileItem>()
    var isPortrait = true // ORIENTATION_PORTRAIT

    var clickListener : OnFileClickListener? = null

    class ViewHolder(private val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: FileItem, color: Int, isPortrait: Boolean) {
            binding.tvName.text = item.name
            binding.tvTime.text = item.time
            if((item.type == FileType.Dir) or (item.type == FileType.UpDir)) {
                binding.tvType.text = item.type.abbr
                binding.tvSize.text = ""
            }
            else {
                binding.tvType.text = item.ext
                binding.tvSize.text = FileUtil.getFileSize(item.byteSize)
            }
            binding.tvName.setTextColor(color)
            binding.tvType.setTextColor(color)
            binding.tvSize.visibility = if(isPortrait) View.GONE else View.VISIBLE
            binding.tvTime.visibility = if(isPortrait) View.GONE else if(item.type == FileType.UpDir) View.INVISIBLE else View.VISIBLE
        }

        fun onTouch(context: Context, event: MotionEvent, item: FileItem) {
            if(event.action == MotionEvent.ACTION_DOWN) {
                binding.tvName.setTextColor(context.getColor(android.R.color.black))
                binding.tvType.setTextColor(context.getColor(android.R.color.black))
                binding.root.setBackgroundResource(item.type.color)
            }
            else if(event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP) {
                binding.tvName.setTextColor(context.getColor(item.type.color))
                binding.tvType.setTextColor(context.getColor(item.type.color))
                binding.root.setBackgroundResource(android.R.color.black)
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