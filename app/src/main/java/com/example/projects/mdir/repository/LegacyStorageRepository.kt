package com.example.projects.mdir.repository

import android.content.Context
import android.util.Log
import com.example.projects.mdir.common.Category
import com.example.projects.mdir.common.FileType
import com.example.projects.mdir.common.FileUtil
import com.example.projects.mdir.data.FileItemEx
import java.io.File
import java.util.*

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LegacyStorageRepository : AbsStorageRepository() {

    override fun loadDirectory(context: Context, path: String, isShowSystem: Boolean): MutableList<FileItemEx> {
        return loadDirectory(context, File(path), isShowSystem)
    }

    override fun loadDirectory(context: Context, file: File, isShowSystem: Boolean): MutableList<FileItemEx> {
        Log.d(TAG, "loadDirectory PATH:" + file.absoluteFile)
        if(!file.exists()) return mutableListOf()

        val list = file.listFiles().toMutableList()
//        if(!isShowSystem) {
//            list.removeAll { item -> return@removeAll item.name[0] == '.' }
//        }
        val root = FileItemEx(file.absolutePath)
        list.forEach {
            val item = FileItemEx(it.absolutePath).apply { up = root }
            root.childs.add(item)
//            listLoadDirs.add(item)

            if(item.isDirectory)
                loadDir(item)
        }
        val listLoadDirs = mutableListOf<FileItemEx>().apply {
            addAll(root.childs)
        }
//        innerSort(listLoadDirs)

        return listLoadDirs
    }

    private fun loadDir(root: FileItemEx) {
        Log.d(TAG, "loadDir PATH:" + root.absoluteFile)
        if(!root.isDirectory) return

        root.listFiles().toMutableList().forEach { file ->
            val item = FileItemEx(file.absolutePath)
            item.up = root
            root.childs.add(item)

            if(file.isDirectory)
                loadDir(item)
        }
    }

    override fun loadDirectory(context: Context, path: String, category: Category, isShowSystem: Boolean): MutableList<FileItemEx> {
        val root = File(path)
        val requestType = FileUtil.toFileType(category = category)
        if(!root.exists() || requestType == FileType.None) return mutableListOf()

        val listCategory = mutableListOf<FileItemEx>()
        loadFile(context = context,
                listRoot = root.listFiles().toMutableList(),
                listOut = listCategory,
                type = requestType,
                isShowSystem = isShowSystem)

//        simpleSort(listCategory)
        return listCategory
    }

    private fun loadFile(context: Context, listRoot: MutableList<File>, listOut: MutableList<FileItemEx>, type: FileType, isShowSystem: Boolean) {
        if(!isShowSystem) {
            listRoot.removeAll { item -> return@removeAll item.name[0] == '.' }
        }
        listRoot.forEach { file ->
            FileItemEx(file.absolutePath).apply {
                if(exType == FileType.Dir) {
                    loadFile(context = context,
                            listRoot = listFiles().toMutableList(),
                            listOut = listOut,
                            type = type,
                            isShowSystem = isShowSystem)
                }
                else if(exType == type) {
                    listOut.add(this)
                }
            }
        }
    }

//    private fun innerSort(list: MutableList<FileItemEx>) {
//        list.sortWith(kotlin.Comparator { o1, o2 ->
//            innerComparator(o1, o2, sortPairFir).let {
//                // 1차 정렬 결과가 동등하다면 2차 정렬을 실행함.
//                if(it != 0)
//                    return@Comparator it
//                else
//                    return@Comparator innerComparator(o1, o2, sortPairSec)
//            }
//        })
//    }
//
//    private fun simpleSort(list: MutableList<FileItemEx>, ascending: Boolean = true)
//            = list.sortWith(kotlin.Comparator { o1, o2 -> o1.name.compareTo(o2.name) * ( if(ascending) 1 else -1 ) })

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
        private const val TAG = "[DE][RE] Legacy"
    }
}