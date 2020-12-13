package com.example.projects.mdir.view.base

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.projects.mdir.data.FileItemEx
import com.example.projects.mdir.listener.OnFileClickListener

abstract class BaseAdapter(val baseContext: Context) : RecyclerView.Adapter<BaseViewHolder>() {

    var isPortrait = true // ORIENTATION_PORTRAIT
    var clickListener : OnFileClickListener? = null
    val items = mutableListOf<FileItemEx>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        with(holder) {
            val item : FileItemEx =  items[position]
            val color = baseContext.getColor(item.exType.color)

            onBind(baseContext, item, color)

//            itemView.setOnClickListener {
//                clickListener?.onClickFile(item)
//            }
//            itemView.setOnLongClickListener {
//                clickListener?.onLongClickFile(item)
//                return@setOnLongClickListener true
//            }
            itemView.setOnTouchListener { _, event ->
                onTouch(baseContext, event, item)
                return@setOnTouchListener false
            }
        }
    }

    fun setFileItems(list: List<FileItemEx>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.count()
}