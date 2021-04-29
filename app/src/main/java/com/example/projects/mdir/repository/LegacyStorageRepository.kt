package com.example.projects.mdir.repository

import android.content.Context
import android.util.Log
import com.example.projects.mdir.common.Category
import com.example.projects.mdir.common.FileType
import com.example.projects.mdir.common.FileUtil
import com.example.projects.mdir.common.Setting
import com.example.projects.mdir.data.FileItemEx
import java.io.File

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LegacyStorageRepository : AbsStorageRepository() {

    private val listFile = mutableListOf<FileItemEx>()
    private val mapDir = mutableMapOf<String, FileItemEx>()

    private var isInit = false

    override fun initRepository(listener: InitRepository?, isForce: Boolean) {
        Log.d(TAG, "initRepository isInit:$isInit")

        if(!isInit || isForce) {
            loadRoot()
            isInit = true
            listener?.finish(complete = true)
        }
        else {
            listener?.finish(complete = false)
        }
    }

    override fun loadDirectory(context: Context, path: String, refresh: Boolean): MutableList<FileItemEx> {
        // refresh : 기존의 데이터를 무시하고 전부 갱신할 것인가
        Log.d(TAG, "loadDirectory PATH:[$path] MAP:[${mapDir.size}]")
        mapDir[path]?.apply {
            val list = this.subFiles
            return mutableListOf<FileItemEx>().apply {
                if(Setting.hideSystem)
                    list.removeAll { item -> return@removeAll item.name[0] == '.' && item.name != ".nomedia" }
                addAll(list)
            }
        }

        val file = File(path)
        if(!file.exists()) return mutableListOf()

        val list = file.listFiles().toMutableList()
        val hideSystem = Setting.hideSystem

        val root = FileItemEx(file.absolutePath)
        list.forEach {
            val item = FileItemEx(it.absolutePath).apply { parentDir = root }
            if(hideSystem && item.name[0] == '.' && item.name != ".nomedia")
                return@forEach

            root.subFiles.add(item)
            if(item.isDirectory) {
                loadDir(item, hideSystem = hideSystem)
            }
        }
        return mutableListOf<FileItemEx>().apply {

            addAll(root.subFiles)
        }
    }

    private fun loadRoot() {
        Log.d(TAG, "loadRoot")
        val root = FileItemEx(FileUtil.LEGACY_ROOT)
//        val hideSystem = Setting.hideSystem

        mapDir.clear()
        mapDir[root.absolutePath] = root

        root.listFiles()?.forEach {
//            if(hideSystem && it.name[0] == '.' && it.name != ".nomedia")
//                return@forEach

            val item = FileItemEx(it.absolutePath).apply { parentDir = root }
            root.subFiles.add(item)

            if(item.isDirectory) {
                mapDir[item.absolutePath] = item
//                loadDir(root = item, hideSystem = hideSystem)
                loadDir(root = item, hideSystem = false)
            }
        }

        listFile.clear()
        listFile.addAll(root.subFiles)
        Log.d(TAG, "loadRoot Size:${listFile.size}")
    }

    private fun loadDir(root: FileItemEx, hideSystem: Boolean = true) {
        root.listFiles()?.forEach { subFile ->
            if(hideSystem && subFile.name[0] == '.' && subFile.name != ".nomedia")
                return@forEach

            val item = FileItemEx(subFile.absolutePath)
            item.parentDir = root
            root.subFiles.add(item)

            if(subFile.isDirectory) {
                mapDir[item.absolutePath] = item
                loadDir(item)
            }
        }
    }

    override fun request(context: Context, category: Category): MutableList<FileItemEx> {
        Log.d(TAG, "request Category:[${category.name}] OldList:${listFile.size}")
        if(listFile.isEmpty()) {
            loadRoot()
        }

        // 카테고리의 경우
        // 폴더 안에 .nomedia 파일이 존재하는 경우 해당 폴더 및 하위 폴더 내용을 추가하지 않는다.
        val hideNomedia = Setting.hideNomedia

        val type = FileUtil.toFileType(category = category)
        if(type == FileType.None) {
            return mutableListOf()
        }

        return mutableListOf<FileItemEx>().also {
            it.add(FileItemEx(FileUtil.LEGACY_ROOT))
            loadType(root = listFile, out = it, type = type, hideNomedia = hideNomedia)
            it.removeAll { dir -> dir.subFiles.size == 0 }
            it.takeIf {
                it.size > 0 && it[0].name == "0"
            }?.apply {
                it[0].simpleName = "내장 메모리"
            }
        }
    }

    private fun loadType(root: MutableList<FileItemEx>, out: MutableList<FileItemEx>, type: FileType, hideNomedia: Boolean = false) {
        root.forEach { file ->
            if(hideNomedia && file.name == ".nomedia") {
                // '.nomedia'를 숨기는 경우 해당 파일이 포함된 폴더 및 하위 폴더를 숨겨야 한다.
                Log.d(TAG, "loadType NoMedia Parent:[${file.parent}]")
//                out.find {
//                    it.absolutePath.contains(file.parent)
//                }?.apply {
//                    subFiles.clear()
//                }
                // 여러 폴더가 포함될 수 있으므로..
                out.forEach {
                    if(it.absolutePath.contains(file.parent))
                        it.subFiles.clear()
                }
                return
            }
            else if(file.exType == FileType.Dir && file.subFiles.isNotEmpty()) {
                // 폴더 이므로 새로 만들어도 된다.
                out.add(FileItemEx(file.absolutePath))
                loadType(file.subFiles, out, type, hideNomedia)
            }
            else if(file.exType == type) {
                out.find {
                    it.absolutePath == file.parent
                }?.apply {
                    subFiles.add(file)
                }
            }
        }
    }

//    override fun loadDirectory(context: Context, path: String, category: Category, refresh: Boolean): MutableList<FileItemEx> {
//        val root = File(path)
//        val requestType = FileUtil.toFileType(category = category)
//        if(!root.exists() || requestType == FileType.None) return mutableListOf()
//
//        val listCategory = mutableListOf<FileItemEx>()
//        loadFile(context = context,
//                listRoot = root.listFiles().toMutableList(),
//                listOut = listCategory,
//                type = requestType)
//
////        simpleSort(listCategory)
//        return listCategory
//    }

//    private fun loadFile(context: Context, listRoot: MutableList<File>, listOut: MutableList<FileItemEx>, type: FileType) {
//        if(!isShowSystem) {
//            listRoot.removeAll { item -> return@removeAll item.name[0] == '.' }
//        }
//        listRoot.forEach { file ->
//            FileItemEx(file.absolutePath).apply {
//                if(exType == FileType.Dir) {
//                    loadFile(context = context,
//                            listRoot = listFiles().toMutableList(),
//                            listOut = listOut,
//                            type = type)
//                }
//                else if(exType == type) {
//                    listOut.add(this)
//                }
//            }
//        }
//    }

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
//
//    private fun innerComparator(o1: FileItemEx, o2: FileItemEx, option: Pair<SortBy, SortOrder>) : Int {
//        val ascending = if(option.second == SortOrder.Ascending) 1 else -1
//        return when(option.first) {
//            is SortBy.Name -> o1.name.compareTo(o2.name) * ascending
//            is SortBy.Date -> (o1.lastModified() - o2.lastModified()).toInt() * ascending
//            is SortBy.Size -> (o1.length() - o2.length()).toInt() * ascending
//            else/*is SortBy.Type*/ -> o1.exType.sort - o2.exType.sort * ascending
//        }
//    }

    companion object {
        private const val TAG = "[DE][RE] Legacy"

        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            LegacyStorageRepository()
        }
    }
}