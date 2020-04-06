package com.example.mdirfilemanager

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mdirfilemanager.common.FileType
import com.example.mdirfilemanager.common.FileUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FileAdapter(private val context: Context, val hidden: Boolean) : RecyclerView.Adapter<FileAdapter.ViewHolder>() {

    companion object {
        const val TAG = "FileAdapter"
    }

    private var path: String = ""
    private val items = mutableListOf<FileItem>()
    var isPortrait = true // ORIENTATION_PORTRAIT

    data class FileItem(var name: String, var type: FileType, var byteSize: Long, var time: String)

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
            val color = item.type.color

            if((item.type == FileType.Dir) or (item.type == FileType.UpDir)) {
                name.text = item.name
                type.text = item.type.abbr
                size.text = ""
            }
            else {
                name.text = FileUtil.getFileName(item.name)
                type.text = FileUtil.getFileExt(item.name)
                size.text = FileUtil.getFileSize(item.byteSize)
            }
            time.text = item.time
            name.setTextColor(context.getColor(color))
            type.setTextColor(context.getColor(color))
            size.visibility = if(isPortrait) View.GONE else View.VISIBLE
            time.visibility = if(isPortrait) View.GONE else if(item.type == FileType.UpDir) View.INVISIBLE else View.VISIBLE

            itemView.setOnClickListener {
                if(item.type == FileType.UpDir) {
                    if(path == FileUtil.ROOT) {
                        Toast.makeText(context, "최상위 폴더입니다.", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        path = FileUtil.getUpDirPath(path)
                        refreshDir()
                    }
                }
                else if(item.type == FileType.Dir){
                    path = "$path/${item.name}"
                    refreshDir()
                }
                else {
                    // 일반 파일
                    Toast.makeText(context, item.name, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun refreshDir() {
        Log.d(TAG, "refreshDir")
        if(path.isEmpty())
            path = FileUtil.ROOT

        val file : File = File(path)
        if(file.exists()) {
            items.clear()
            items.add(FileItem(name = "..", type = FileType.UpDir, byteSize = 0L, time = "00-00-00 00:00"))

            val time = SimpleDateFormat(context.getString(R.string.date_format_pattern), Locale.KOREA)

            file.listFiles()?.forEach {
                if(it.isDirectory)
                    items.add(FileItem(name = it.name, type = FileType.Dir, byteSize = 0L, time = time.format(Date(it.lastModified()))))
                else
                    items.add(FileItem(name = it.name, type = FileType.Default, byteSize = it.length(), time = time.format(Date(it.lastModified()))))
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