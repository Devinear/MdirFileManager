package com.example.projects.mdir.view.base

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.projects.mdir.data.FileItemEx
import com.example.projects.mdir.listener.OnFileClickListener

abstract class BaseAdapter(val baseContext: Context) : RecyclerView.Adapter<BaseViewHolder>() {

//    var isPortrait = true // ORIENTATION_PORTRAIT
    val items = mutableListOf<FileItemEx>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        with(holder) {
            val item : FileItemEx =  items[position]
            val color = baseContext.getColor(item.exType.color)

            onBind(baseContext, item, color)
        }
    }

    fun setFileItems(list: List<FileItemEx>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.count()
}