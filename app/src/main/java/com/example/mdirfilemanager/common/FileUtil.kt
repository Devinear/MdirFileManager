package com.example.mdirfilemanager.common

import android.os.Environment
import java.io.File
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

}