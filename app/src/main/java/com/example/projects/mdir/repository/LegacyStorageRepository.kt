package com.example.projects.mdir.repository

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import com.example.projects.R
import com.example.projects.mdir.common.ExtType
import com.example.projects.mdir.common.FileType
import com.example.projects.mdir.common.FileUtil
import com.example.projects.mdir.data.FileItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LegacyStorageRepository : AbsStorageRepository() {

    override fun loadDirectory(context: Context, path: String): MutableList<FileItem> {
        return loadDirectory(context, File(path))
    }

    override fun loadDirectory(context: Context, file: File): MutableList<FileItem> {
        if(!file.exists()) return mutableListOf()

        val listLoadDirs = mutableListOf<FileItem>()

        val list = file.listFiles()
        val listDirs = mutableListOf<FileItem>()
        val listFiles = mutableListOf<FileItem>()

        val time = SimpleDateFormat(context.getString(R.string.date_format_pattern), Locale.KOREA)

        list.forEach {
            var image : BitmapDrawable? = null
            if (it.isDirectory) {
                val subList = it.listFiles { file -> file.isFile && FileUtil.getFileExtType(file.extension) == ExtType.Image }
                if(subList.isNotEmpty()) {
                    image = BitmapDrawable(context.resources,
                        BitmapFactory.decodeFile(subList[0].absolutePath, BitmapFactory.Options().apply { inSampleSize = 4 } ))
                }
                val item = FileItem(name = it.name, path = it.absolutePath, type = FileType.Dir,
                    ext = "", byteSize = 0L, time = time.format(Date(it.lastModified())), drawable = image, childCount = it.list()?.size?:0)
                listDirs.add(item)
            }
            else {
                val type = FileUtil.toFileType(FileUtil.getFileExtType(file.extension))
                if(type == FileType.Image)
                    image = BitmapDrawable(context.resources, BitmapFactory.decodeFile(file.absolutePath, BitmapFactory.Options().apply { inSampleSize = 4 } ))

                val item = FileItem(name = FileUtil.getFileName(file.name), path = file.absolutePath, type = type,
                    ext = FileUtil.getFileExt(file.name), byteSize = file.length(), time = time.format(Date(file.lastModified())), drawable = image, childCount = file.list()?.size?:0)
                listFiles.add(item)
            }
//            it.takeIf { it.isDirectory }?.apply {
//
//                var image : BitmapDrawable? = null
//                val subList = it.listFiles { file -> file.isFile && FileUtil.getFileExtType(file.extension) == ExtType.Image }
//                if(subList.isNotEmpty()) {
//                    image = BitmapDrawable(context.resources,
//                        BitmapFactory.decodeFile(subList[0].absolutePath, BitmapFactory.Options().apply { inSampleSize = 4 } ))
//                }
//                val item = FileItem(name = it.name, path = it.absolutePath, type = FileType.Dir,
//                    ext = "", byteSize = 0L, time = time.format(Date(it.lastModified())), drawable = image, childCount = it.list()?.size?:0)
//                listDirs.add(item)
//            }
        }

        return listLoadDirs
    }
}