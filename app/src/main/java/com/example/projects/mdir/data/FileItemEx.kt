package com.example.projects.mdir.data

import android.graphics.drawable.BitmapDrawable
import com.example.projects.mdir.common.ExtType
import com.example.projects.mdir.common.FileType
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FileItemEx(path: String, val isUpDir: Boolean = false) : File(path) {

    private var _exType : FileType = FileType.None
    val exType : FileType; get() = _exType
    private var _exTime : String = ""
    val exTime : String; get() = _exTime
    private var _isSystem : Boolean = false
    val isSystem : Boolean; get() = _isSystem

    private var _simpleName : String = ""
    val simpleName : String; get() = _simpleName

    var drawable: BitmapDrawable? = null

    init {
        convert()
    }

    private fun convert() {
        this.takeIf { exists() }?: return

        if(isUpDir) {
            _exType = FileType.UpDir
        }
        else {
            _exType = if (isDirectory) FileType.Dir else getFileType(getExtType(extension))
            _exTime = SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREA).format(Date(lastModified()))
            _isSystem = name.indexOf('.') == 0
            _simpleName = if (isDirectory) name else getSimpleName(name)
        }
    }

    private fun getSimpleName(name: String) : String {
        // 확장자가 없는 숨겨진 파일(.로 시작하는)의 경우를 위함
        val lastIndex = name.lastIndexOf('.')
        return if(lastIndex < 1)
            name
        else
            name.substring(0, lastIndex)
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
        when (ext.toLowerCase(Locale.ROOT)) {
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