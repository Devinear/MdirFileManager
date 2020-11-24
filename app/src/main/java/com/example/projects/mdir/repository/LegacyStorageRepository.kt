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
        innerSort(listLoadDirs)

        return listLoadDirs
    }

    private fun innerSort(list: MutableList<FileItemEx>) {
        list.sortWith(kotlin.Comparator { o1, o2 ->
            innerComparator(o1, o2, sortPairFir).let {
                // 1차 정렬 결과가 동등하다면 2차 정렬을 실행함.
                if(it != 0)
                    return@Comparator it
                else
                    return@Comparator innerComparator(o1, o2, sortPairSec)
            }
        })
    }

    private fun innerComparator(o1: FileItemEx, o2: FileItemEx, option: Pair<SortBy, SortOrder>) : Int {
        val ascending = if(option.second == SortOrder.Ascending) 1 else -1
        return when(option.first) {
            is SortBy.Name -> o1.name.compareTo(o2.name) * ascending
            is SortBy.Date -> (o1.lastModified() - o2.lastModified()).toInt() * ascending
            is SortBy.Size -> (o1.length() - o2.length()).toInt() * ascending
            else/*is SortBy.Type*/ -> o1.exType.sort - o2.exType.sort * ascending
        }
    }

    companion object {
        private val TIME = SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREA)
    }
}