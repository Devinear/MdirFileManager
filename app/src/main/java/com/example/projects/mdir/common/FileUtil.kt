package com.example.projects.mdir.common

import android.content.Context
import android.os.Environment
import com.example.projects.R
import com.example.projects.mdir.FileItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object FileUtil {

    var ROOT = Environment.getExternalStorageDirectory().absolutePath

    fun getFileName(name: String) : String {
        // 확장자가 없는 숨겨진 파일(.로 시작하는)의 경우를 위함
        val lastIndex = name.lastIndexOf('.')
        return if(lastIndex < 2)
            name
        else
            name.substring(0, lastIndex)
    }

    fun getFileExt(name: String) : String {
        // 확장자가 없는 숨겨진 파일(.로 시작하는)의 경우를 위함
        val lastIndex = name.lastIndexOf('.')
        return if(lastIndex < 2)
            ""
        else
            name.substring(lastIndex+1).toUpperCase(Locale.ROOT)
    }

    fun getFileSize(byteSize: Long) : String {
        var size = byteSize
        if(size < 1024)
            return "$size B"

        if(size < 1024 * 1024)
            return "${String.format("%.1f", (size/1024F))} KB"

        size /= 1024
        if(size < 1024 * 1024)
            return "${String.format("%.1f", (size/1024F))} MB"

        size /= 1024
        return "${String.format("%.1f", (size/1024F))} GB"
    }

    fun getUpDirPath(path: String) : String {
        val last = path.lastIndexOf('/')
        return if(last < 0)
            ""
        else
            path.substring(0, last)
    }

    fun getFileExtType(ext: String) : ExtType {
        return when (ext.toLowerCase()) {
            "jpg", "jpeg", "png", "bmp" ->
                ExtType.Image
            "3gp", "mp4", "webm", "mkv", "ts" ->
                ExtType.Video
            "m4a", "aac", "flac", "gsm", "mp3" ->
                ExtType.Audio
            "doc", "xls", "ppt", "docx", "xlsx", "pptx", "pdf" ->
                ExtType.Document
            else ->
                ExtType.Default
        }
    }

    fun toFileType(ext: ExtType) : FileType {
        return when(ext) {
            ExtType.Audio -> FileType.Audio
            ExtType.Image -> FileType.Image
            ExtType.Video -> FileType.Video
            ExtType.Document -> FileType.Document
            else -> FileType.Default
        }
    }

    fun getChildFileItems(context: Context, path: String, isHideShow: Boolean) : List<FileItem> {
        val items = mutableListOf<FileItem>()

        val file = File(path)
        if(file.exists()) {
            items.clear()
            items.add(FileItem(name = "..", type = FileType.UpDir, ext = "", byteSize = 0L, time = "00-00-00 00:00"))
            val time = SimpleDateFormat(context.getString(R.string.date_format_pattern), Locale.KOREA)

            file.listFiles()?.forEach {
                if (isHideShow || it.name[0] != '.') {
                    if (it.isDirectory) {
                        items.add(FileItem(name = it.name, type = FileType.Dir,
                            ext = "", byteSize = 0L, time = time.format(Date(it.lastModified()))))
                    } else {
                        items.add(FileItem(name = getFileName(it.name),
                            type = toFileType(getFileExtType(getFileExt(it.name))),
                            ext = getFileExt(it.name), byteSize = it.length(),
                            time = time.format(Date(it.lastModified()))))
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
        }
        return items
    }

}