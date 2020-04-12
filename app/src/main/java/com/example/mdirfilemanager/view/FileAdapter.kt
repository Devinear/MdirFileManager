package com.example.mdirfilemanager.view

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.mdirfilemanager.R
import com.example.mdirfilemanager.common.FileType
import com.example.mdirfilemanager.common.FileUtil
import kotlinx.android.synthetic.main.item_file.view.*
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

    data class FileItem(var name: String, var type: FileType, var ext: String, var byteSize: Long, var time: String)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_name)
        val type: TextView = itemView.findViewById(R.id.tv_type)
        val size: TextView = itemView.findViewById(R.id.tv_size)
        val time: TextView = itemView.findViewById(R.id.tv_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_file,
            parent,
            false
        )
    )

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
                name.text = item.name
                type.text = item.ext
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

                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "application/*"
                        val file = File("$path/${item.name}")
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                        putExtra(Intent.EXTRA_STREAM, uri)
                    }
                    context.startActivity(sendIntent)
                }
            }
            itemView.setOnTouchListener { v, event ->
                Log.d(TAG, "setOnTouchListener ACTION:${event.action}")
                if(event.action == MotionEvent.ACTION_DOWN) {
                    v.tv_name.setTextColor(context.getColor(android.R.color.black))
                    v.tv_type.setTextColor(context.getColor(android.R.color.black))
                    v.setBackgroundResource(item.type.color)
                }
                else if(event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP) {
                    v.tv_name.setTextColor(context.getColor(item.type.color))
                    v.tv_type.setTextColor(context.getColor(item.type.color))
                    v.setBackgroundResource(android.R.color.black)
                }
                return@setOnTouchListener false
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
            items.add(
                FileItem(
                    name = "..",
                    type = FileType.UpDir,
                    ext = "",
                    byteSize = 0L,
                    time = "00-00-00 00:00"
                )
            )
            val time = SimpleDateFormat(context.getString(R.string.date_format_pattern), Locale.KOREA)

            file.listFiles()?.forEach {
                if(hidden && it.name[0] == '.') {}
                else {
                    if (it.isDirectory)
                        items.add(
                            FileItem(
                                name = it.name,
                                type = FileType.Dir,
                                ext = "",
                                byteSize = 0L,
                                time = time.format(Date(it.lastModified()))
                            )
                        )
                    else
                        items.add(
                            FileItem(
                                name = FileUtil.getFileName(it.name),
                                type = FileType.Default,
                                ext = FileUtil.getFileExt(it.name),
                                byteSize = it.length(),
                                time = time.format(Date(it.lastModified()))
                            )
                        )
                }
            }

            if(items.isNotEmpty()) {
                items.sortWith(kotlin.Comparator { o1, o2 ->
                    when {
                        o1.type.sort != o2.type.sort  -> { o1.type.sort - o2.type.sort }
                        o1.ext.compareTo(o2.ext) != 0 -> { o1.ext.compareTo(o2.ext) }
                        else -> { o1.name.compareTo(o2.name) }
                    }
                })
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