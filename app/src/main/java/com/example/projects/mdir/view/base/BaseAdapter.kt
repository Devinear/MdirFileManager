package com.example.projects.mdir.view.base

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.projects.mdir.data.FileItem
import com.example.projects.mdir.listener.OnFileClickListener

abstract class BaseAdapter(val baseContext: Context) : RecyclerView.Adapter<BaseViewHolder>() {

    var isPortrait = true // ORIENTATION_PORTRAIT
    var clickListener : OnFileClickListener? = null
    val items = mutableListOf<FileItem>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        with(holder) {
            val item : FileItem =  items[position]
            val color = baseContext.getColor(item.type.color)

            onBind(baseContext, item, color)

            itemView.setOnClickListener {
                clickListener?.onClickFile(item)
            }
            itemView.setOnLongClickListener {
                clickListener?.onLongClickFile(item)
                return@setOnLongClickListener true
            }
            itemView.setOnTouchListener { _, event ->
                onTouch(baseContext, event, item)
                return@setOnTouchListener false
            }
        }
    }

    fun setFileItems(list: List<FileItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.count()
}