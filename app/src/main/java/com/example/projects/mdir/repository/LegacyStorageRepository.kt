package com.example.projects.mdir.repository

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import com.example.projects.mdir.common.ExtType
import com.example.projects.mdir.common.FileType
import com.example.projects.mdir.common.FileUtil
import com.example.projects.mdir.data.FileItemEx
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LegacyStorageRepository : AbsStorageRepository() {

    override fun loadDirectory(context: Context, path: String): MutableList<FileItemEx> {
        return loadDirectory(context, File(path))
    }

    override fun loadDirectory(context: Context, file: File): MutableList<FileItemEx> {
        if(!file.exists()) return mutableListOf()

        val listLoadDirs = mutableListOf<FileItemEx>()

        file.listFiles()?.forEach {
            var image : BitmapDrawable? = null
            if (it.isDirectory) {
                val subList = it.listFiles { file -> file.isFile && FileUtil.getFileExtType(file.extension) == ExtType.Image }
                subList?.takeIf { subList.isNotEmpty() }?.apply {
                    image = BitmapDrawable(context.resources,
                        BitmapFactory.decodeFile(subList[0].absolutePath, BitmapFactory.Options().apply { inSampleSize = 4 } ))
                }
//                val item = FileItem(name = it.name, path = it.absolutePath, type = FileType.Dir,
//                    ext = "", byteSize = 0L, time = TIME.format(Date(it.lastModified())), drawable = image, childCount = it.list()?.size?:0)
                listLoadDirs.add(FileItemEx(it.absolutePath).apply { drawable = image })
            }
            else {
                val type = FileUtil.toFileType(FileUtil.getFileExtType(file.extension))
                image = if(type == FileType.Image) BitmapDrawable(context.resources, BitmapFactory.decodeFile(file.absolutePath, BitmapFactory.Options().apply { inSampleSize = 4 } )) else null
//                val item = FileItem(name = FileUtil.getFileName(file.name), path = file.absolutePath, type = type,
//                    ext = FileUtil.getFileExt(file.name), byteSize = file.length(), time = TIME.format(Date(file.lastModified())), drawable = image, childCount = file.list()?.size?:0)
                listLoadDirs.add(FileItemEx(it.absolutePath).apply { drawable = image })
            }
        }
        sort(listLoadDirs)

        return listLoadDirs
    }

    companion object {
        private val TIME = SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREA)
    }
}