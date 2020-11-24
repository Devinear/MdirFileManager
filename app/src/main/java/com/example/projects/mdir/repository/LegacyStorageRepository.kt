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
                listLoadDirs.add(FileItemEx(it.absolutePath).apply { drawable = image })
            }
            else {
                val type = FileUtil.toFileType(FileUtil.getFileExtType(file.extension))
                image = if(type == FileType.Image) BitmapDrawable(context.resources, BitmapFactory.decodeFile(file.absolutePath, BitmapFactory.Options().apply { inSampleSize = 4 } )) else null
                listLoadDirs.add(FileItemEx(it.absolutePath).apply { drawable = image })
            }
        }
        sort(listLoadDirs)

        return listLoadDirs
    }

    private fun sort(list: MutableList<FileItemEx>) {
        list.sortWith(kotlin.Comparator { o1, o2 ->
            val ascending = if(sortPairFir.second == SortOrder.Ascending) 1 else -1
            when(sortPairFir.first) {
                is SortBy.Name -> {
                    o1.name.compareTo(o2.name) * ascending
                }
                is SortBy.Date -> {
                    (o1.lastModified() - o2.lastModified()).toInt() * ascending
                }
                is SortBy.Size -> {
                    (o1.length() - o2.length()).toInt() * ascending
                }
                else/*is SortBy.Type*/ -> {
                    o1.exType.sort - o2.exType.sort * ascending
                }
            }
//            when {
//                o1.type.sort != o2.type.sort  -> { o1.type.sort - o2.type.sort }
//                o1.ext.compareTo(o2.ext) != 0 -> { o1.ext.compareTo(o2.ext) }
//                else -> { o1.name.compareTo(o2.name) }
//            }
        })
    }

    companion object {
        private val TIME = SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREA)
    }
}