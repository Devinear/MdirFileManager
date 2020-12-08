package com.example.projects.mdir.data

import android.graphics.drawable.BitmapDrawable
import com.example.projects.mdir.common.ExtType
import com.example.projects.mdir.common.FileType
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FileItemEx : File {
    constructor(path: String) : super(path)

    private var _exType : FileType = FileType.None
    val exType : FileType; get() = _exType
    private var _exTime : String = ""
    val exTime : String; get() = _exTime
    private var _isSystem : Boolean = false
    val isSystem : Boolean; get() = _isSystem

    var drawable: BitmapDrawable? = null

    init {
        convert()
    }

    private fun convert() {
        this.takeIf { exists() }?: return

        _exType = if(isDirectory) FileType.Dir else getFileType(getExtType(extension))
        _exTime = SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREA).format(Date(lastModified()))
        _isSystem = name.indexOf('.') == 0
    }

    private fun getFileType(ext: ExtType) : FileType =
        when(ext) {
            ExtType.Audio -> FileType.Audio
            ExtType.Image -> FileType.Image
            ExtType.Video -> FileType.Video
            ExtType.Document -> FileType.Document
            ExtType.Zip -> FileType.Zip
            ExtType.Apk -> FileType.APK
            else -> FileType.Default
        }

    private fun getExtType(ext: String) : ExtType =
        when (ext.toLowerCase()) {
            "jpg", "jpeg", "png", "bmp", "gif", "heif" ->
                ExtType.Image
            "3gp", "mp4", "webm", "mkv", "ts", "avi", "wmv" ->
                ExtType.Video
            "m4a", "aac", "flac", "gsm", "mp3", "amr", "ogg" ->
                ExtType.Audio
            "doc", "hwp", "xls", "ppt", "docx", "xlsx", "pptx", "pdf", "txt" ->
                ExtType.Document
            "zip", "7z", "rar" ->
                ExtType.Zip
            "apk" ->
                ExtType.Apk
            else ->
                ExtType.Default
        }
}