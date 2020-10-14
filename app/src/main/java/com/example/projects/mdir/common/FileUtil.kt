package com.example.projects.mdir.common

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import com.example.projects.R
import com.example.projects.mdir.data.FileItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object FileUtil {

    var ROOT = Environment.getExternalStorageDirectory().absolutePath

    fun getFileName(name: String) : String {
        // 확장자가 없는 숨겨진 파일(.로 시작하는)의 경우를 위함
        val lastIndex = name.lastIndexOf('.')
        return if(lastIndex < 1)
            name
        else
            name.substring(0, lastIndex)
    }

    fun getFileExt(name: String) : String {
        // 확장자가 없는 숨겨진 파일(.로 시작하는)의 경우를 위함
        val lastIndex = name.lastIndexOf('.')
        return if(lastIndex < 1)
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

    fun getMimeType(ext: String) : String {
        return when(ext.toLowerCase()) {
            "apk"   -> "application/vnd.android.package-archive"
            "ccad"  -> "application/clariscad"
            "dxf"   -> "application/dxf"
            "mdb"   -> "application/msaccess"
            "doc"   -> "application/msword"
            "bin"   -> "application/octet-stream"
            "pdf"   -> "application/pdf"
            "ai", "ps", "eps" -> "application/postscript"
            "rtf"   -> "application/rtf"
            "xls"   -> "application/vnd.ms-excel"
            "ppt"   -> "application/vnd.ms-powerpoint"
            "cdf"   -> "application/x-cdf"
            "csh"   -> "application/x-csh"
            "dvi"   -> "application/x-dvi"
            "js"    -> "application/x-javascript"
            "latex" -> "application/x-latex"
            "mif"   -> "application/x-mif"
            "tcl"   -> "application/x-tcl"
            "tex"   -> "application/x-tex"
            "texinfo", "texi" -> "application/x-texinfo"
            "t", "tr", "roff" -> "application/x-troff"
            "man"   -> "application/x-troff-man"
            "me"    -> "application/x-troff-me"
            "ms"    -> "application/x-troff-ms"
            "src"   -> "application/x-wais-source"
            "zip"   -> "application/zip"
            "au", "snd" -> "audio/basic"
            "aif", "aiff", "aifc" -> "audio/x-aiff"
            "wav"   -> "audio/x-wav"
            "gif"   -> "image/gif"
            "ief"   -> "image/ief"
            "jpeg", "jpg", "jpe" -> "image/jpeg"
            "tiff", "tif" -> "image/tiff"
            "ras"   -> "image/x-cmu-raster"
            "pnm"   -> "image/x-portable-anymap"
            "pbm"   -> "image/x-portable-bitmap"
            "pgm"   -> "image/x-portable-graymap"
            "ppm"   -> "image/x-portable-pixmap"
            "rgb"   -> "image/x-rgb"
            "xbm"   -> "image/x-xbitmap"
            "xpm"   -> "image/x-xpixmap"
            "xwd"   -> "image/x-xwindowdump"
            "gzip"  -> "multipart/x-gzip"
            "css"   -> "text/css"
            "html", "htm" -> "text/html"
            "txt"   -> "text/plain"
            "rtx"   -> "text/richtext"
            "tsv"   -> "text/tab-separated- values"
            "xml"   -> "text/xml"
            "etx"   -> "text/x-setext"
            "xsl"   -> "text/xsl"
            "mpeg", "mpg", "mpe" -> "video/mpeg"
            "qt", "mov" -> "video/quicktime"
            "avi"   -> "video/x-msvideo"
            "movie" -> "video/x-sgi-movie"
            "xap"   -> "application/x-silverlight-app"
            "manifest" -> "application/manifest"
            "application" -> "application/x-ms-application"
            "xbap"  -> "application/x-ms-xbap"
            "deploy" -> "application/octet-stream"
            "xps"   -> "application/vnd.ms-xpsdocument"
            "xaml"  -> "application/xaml+xml"
            "cab"   -> "application/vnd.ms-cab-compressed"
            "docx"  -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "docm"  -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "pptx"  -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            "pptm"  -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            "xlsx"  -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "xlsm"  -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "accdb" -> "application/msaccess"
            "pub"   -> "application/x-mspublisher"
            "svg"   -> "image/svg+xml"
            "xht"   -> "application/xhtml+xml"
            "xhtml" -> "application/xhtml+xml"
            else    -> "application/*"
        }
    }

    fun toFileType(ext: ExtType) : FileType {
        return when(ext) {
            ExtType.Audio -> FileType.Audio
            ExtType.Image -> FileType.Image
            ExtType.Video -> FileType.Video
            ExtType.Document -> FileType.Document
            ExtType.Zip -> FileType.Zip
            ExtType.Apk -> FileType.APK
            else -> FileType.Default
        }
    }

    fun convertFileItem(context: Context, path: String) : FileItem? {
        val file = File(path)
        if(file.exists()) {
            val time = SimpleDateFormat(context.getString(R.string.date_format_pattern), Locale.KOREA)
            var image : BitmapDrawable? = null
            if(file.isDirectory) {
                val subs = file.listFiles { it -> it.isFile && getFileExtType(it.extension) == ExtType.Image }
                if(subs.isNotEmpty())
                    image = BitmapDrawable(context.resources,
                        BitmapFactory.decodeFile(subs[0].absolutePath, BitmapFactory.Options().apply { inSampleSize = 4 } ))

                return FileItem(name = file.name, path = file.absolutePath, type = FileType.Dir,
                    ext = "", byteSize = 0L, time = time.format(Date(file.lastModified())), drawable = image, childCount = file.list()?.size?:0)
            }
            else {
                val type = toFileType(getFileExtType(file.extension)) // getFileExt(it.name)
                if(type == FileType.Image)
                    image = BitmapDrawable(context.resources, BitmapFactory.decodeFile(file.absolutePath, BitmapFactory.Options().apply { inSampleSize = 4 } ))

                return FileItem(name = getFileName(file.name), path = file.absolutePath, type = type,
                    ext = getFileExt(file.name), byteSize = file.length(), time = time.format(Date(file.lastModified())), drawable = image, childCount = file.list()?.size?:0)
            }
        }
        return null
    }

    fun getChildFileItems(context: Context, path: String, isHideShow: Boolean, isShowType: ShowType = ShowType.All) : List<FileItem> {
        val items = mutableListOf<FileItem>()

        val file = File(path)
        if(file.exists()) {
            items.clear()
            items.add(FileItem(name = "..", path = file.absolutePath, type = FileType.UpDir, ext = "", byteSize = 0L, time = "00-00-00 00:00"))
            val time = SimpleDateFormat(context.getString(R.string.date_format_pattern), Locale.KOREA)

            file.listFiles()?.forEach {
                if (isHideShow || it.name[0] != '.') {
                    if (it.isDirectory) {
                        var image : BitmapDrawable? = null
                        val subList = it.listFiles { file -> file.isFile && getFileExtType(file.extension) == ExtType.Image }
                        if(subList.isNotEmpty()) {
                            image = BitmapDrawable(context.resources,
                                BitmapFactory.decodeFile(subList[0].absolutePath, BitmapFactory.Options().apply { inSampleSize = 4 } ))
                        }
                        items.add(
                            FileItem(name = it.name, path = it.absolutePath, type = FileType.Dir,
                                ext = "", byteSize = 0L, time = time.format(Date(it.lastModified())), drawable = image, childCount = it.list()?.size?:0)
                        )
                    } else {
                        val type = toFileType(getFileExtType(it.extension)) // getFileExt(it.name)
                        val isAddItem = (isShowType == ShowType.All) ||
                                when(type) {
                                    FileType.Image    -> isShowType == ShowType.Img
                                    FileType.Document -> isShowType == ShowType.Doc
                                    FileType.Zip      -> isShowType == ShowType.Zip
                                    else              -> false
                                }
                        if(isAddItem) {
                            var image : BitmapDrawable? = null
                            if(type == FileType.Image) {
                                val bitmap = BitmapFactory.decodeFile(it.absolutePath, BitmapFactory.Options().apply { inSampleSize = 4 } )
                                image = BitmapDrawable(context.resources, bitmap)
                            }
                            items.add(FileItem(name = getFileName(it.name), path = it.absolutePath, type = type,
                                    ext = getFileExt(it.name), byteSize = it.length(),
                                    time = time.format(Date(it.lastModified())), drawable = image, childCount = it.list()?.size?:0))
                        }
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