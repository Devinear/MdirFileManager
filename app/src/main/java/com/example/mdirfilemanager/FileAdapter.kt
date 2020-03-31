package com.example.mdirfilemanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FileAdapter : RecyclerView.Adapter<FileAdapter.ViewHolder>() {

    private val items = mutableListOf<FileItem>()

    data class FileItem(var name: String, var type: String, var size: String, var time: String)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_name)
        val type: TextView = itemView.findViewById(R.id.tv_type)
        val size: TextView = itemView.findViewById(R.id.tv_size)
        val time: TextView = itemView.findViewById(R.id.tv_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_file, null))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            name.text = items[position].name
            type.text = items[position].type
            size.text = items[position].size
            time.text = items[position].time
        }
    }
}