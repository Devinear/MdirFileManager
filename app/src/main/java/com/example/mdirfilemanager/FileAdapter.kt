package com.example.mdirfilemanager

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mdirfilemanager.common.FileType
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FileAdapter(private val context: Context, var path: String, val hidden: Boolean) : RecyclerView.Adapter<FileAdapter.ViewHolder>() {

    companion object {
        const val TAG = "FileAdapter"
    }

    private val items = mutableListOf<FileItem>()
    var isPortrait = true // ORIENTATION_PORTRAIT

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
        Log.d(TAG, "onBindViewHolder Position:$position Portrait:$isPortrait")
        with(holder) {
            val item : FileItem =  items[position]
            val isDir = (item.type == FileType.Dir) or (item.type == FileType.UpDir)
            val color = item.type.color

            if(isDir) {
                name.text = item.name
                type.text = item.type.abbr
                size.text = ""
            }
            else {
                name.text = getFileName(item.name)
                type.text = getFileExt(item.name)
                size.text = item.size.toString()
            }
            time.text = item.time
            name.setTextColor(context.getColor(color))
            type.setTextColor(context.getColor(color))
            size.visibility = if(isPortrait) View.GONE else View.VISIBLE
            time.visibility = if(isPortrait) View.GONE else View.VISIBLE
        }
    }

    fun refreshDir() {
        Log.d(TAG, "refreshDir")
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

    private fun getFileName(name: String) : String {
        // 확장자가 없는 숨겨진 파일(.로 시작하는)의 경우를 위함
        val lastIndex = name.lastIndexOf('.')
        return if(lastIndex < 2)
            name
        else
            name.substring(0, lastIndex)
    }

    private fun getFileExt(name: String) : String {
        // 확장자가 없는 숨겨진 파일(.로 시작하는)의 경우를 위함
        val lastIndex = name.lastIndexOf('.')
        return if(lastIndex < 2)
            ""
        else
            name.substring(lastIndex+1)
    }
}