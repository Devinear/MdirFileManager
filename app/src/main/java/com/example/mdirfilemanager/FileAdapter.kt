package com.example.mdirfilemanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mdirfilemanager.common.FileType
import java.io.File
import java.io.FileFilter
import java.text.SimpleDateFormat
import java.util.*

class FileAdapter(val context: Context, var path: String, val hidden: Boolean) : RecyclerView.Adapter<FileAdapter.ViewHolder>() {

    private val items = mutableListOf<FileItem>()

    data class FileItem(var name: String, var type: FileType, var size: Long, var time: String)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_name)
        val type: TextView = itemView.findViewById(R.id.tv_type)
        val size: TextView = itemView.findViewById(R.id.tv_size)
        val time: TextView = itemView.findViewById(R.id.tv_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val isDir = (items[position].type == FileType.Dir) or (items[position].type == FileType.UpDir)
            val color = items[position].type.color

            name.text = items[position].name
            type.text = items[position].type.abbr
            size.text = items[position].size.toString()
            time.text = items[position].time

            name.setTextColor(context.getColor(color))
            type.setTextColor(context.getColor(color))
            size.visibility = if(isDir) View.GONE else View.VISIBLE
            time.visibility = if(isDir) View.GONE else View.VISIBLE
        }
    }

    fun refreshDir() {
        val file : File = File(path)
        if(file.exists()) {
            items.clear()
            items.add(FileItem(name = "..", type = FileType.UpDir, size = 0L, time = ""))

            val time = SimpleDateFormat(context.getString(R.string.date_format_pattern), Locale.KOREA)

            file.listFiles()?.forEach {
                if(it.isDirectory)
                    items.add(FileItem(name = it.name, type = FileType.Dir, size = 0L, time = time.format(Date(it.lastModified()))))
                else
                    items.add(FileItem(name = it.name, type = FileType.Default, size = it.length(), time = time.format(Date(it.lastModified()))))
            }
//            file.listFiles(FileFilter {
//                pathname -> pathname.isDirectory
//            }).forEach {
//
//            }
        }

        notifyDataSetChanged()
    }
}