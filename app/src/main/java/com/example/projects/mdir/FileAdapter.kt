package com.example.projects.mdir

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.projects.R
import com.example.projects.databinding.ItemFileBinding
import com.example.projects.mdir.common.ExtType
import com.example.projects.mdir.common.FileType
import com.example.projects.mdir.common.FileUtil
import kotlinx.android.synthetic.main.item_file.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FileAdapter(private val context: Context) : RecyclerView.Adapter<FileAdapter.ViewHolder>() {

    companion object {
        const val TAG = "FileAdapter"
    }

    private var path: String = ""
    private val items = mutableListOf<FileItem>()
    var isPortrait = true // ORIENTATION_PORTRAIT
    var isHideShow = false

    class ViewHolder(private val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: FileItem, color: Int, isPortrait: Boolean) {
            if((item.type == FileType.Dir) or (item.type == FileType.UpDir)) {
                binding.tvName.text = item.name
                binding.tvType.text = item.type.abbr
                binding.tvSize.text = ""
            }
            else {
                binding.tvName.text = item.name
                binding.tvType.text = item.ext
                binding.tvSize.text = FileUtil.getFileSize(item.byteSize)
            }
            binding.tvTime.text = item.time
            binding.tvName.setTextColor(color)
            binding.tvType.setTextColor(color)
            binding.tvSize.visibility = if(isPortrait) View.GONE else View.VISIBLE
            binding.tvTime.visibility = if(isPortrait) View.GONE else if(item.type == FileType.UpDir) View.INVISIBLE else View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder Position:$position Portrait:$isPortrait")
        with(holder) {
            val item : FileItem =  items[position]
            val color = context.getColor(item.type.color)

            onBind(item, color, isPortrait)

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
                        type = when(FileUtil.getFileExtType(item.ext)) {
                            ExtType.Image -> "image/*"
                            ExtType.Video -> "video/*"
                            ExtType.Audio -> "audio/*"
                            else -> "application/*"
                        }
                        putExtra(Intent.EXTRA_STREAM, File("$path/${item.name}").toURI())
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "공유: ${item.name}.${item.ext}"))
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
                if(!isHideShow && it.name[0] == '.') {}
                else {
                    if (it.isDirectory) {
                        items.add(
                            FileItem(
                                name = it.name, type = FileType.Dir,
                                ext = "", byteSize = 0L, time = time.format(Date(it.lastModified()))
                            )
                        )
                    }
                    else {
                        items.add(
                            FileItem(
                                name = FileUtil.getFileName(it.name),
                                type = FileUtil.toFileType(
                                    FileUtil.getFileExtType(
                                        FileUtil.getFileExt(
                                            it.name
                                        )
                                    )
                                ),
                                ext = FileUtil.getFileExt(it.name),
                                byteSize = it.length(),
                                time = time.format(Date(it.lastModified()))
                            )
                        )
                    }
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