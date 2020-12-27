package com.example.projects.mdir.repository

import com.example.projects.mdir.App
import com.example.projects.mdir.FileViewModel
import com.example.projects.mdir.data.FileItemEx
import com.example.projects.mdir.repository.favorite.FavoriteData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteRepository {

    private val dao = App.FAVORITE_DATABASE.favoriteDao()

    fun add(item: FileItemEx) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insert(FavoriteData(
                    id = item.absolutePath,
                    isDir = item.isDirectory,
                    time = System.currentTimeMillis()
                ))
        }
    }

    fun getAll(viewModel: FileViewModel) {
        val list = mutableListOf<String>()
        CoroutineScope(Dispatchers.IO).launch {
            dao.getAll().forEach { list.add(it.id) }
            viewModel.responseFavoriteList(list)
        }
    }

//    private val favorite = mutableSetOf<String>()
//
//    fun add(path: String) : Boolean = favorite.add(path)
//
//    fun remove(path: String) : Boolean = favorite.remove(path)
//
//    fun clear() = favorite.clear()
//
//    fun contains(path: String) : Boolean = favorite.contains(path)
//
//    fun isEmpty() : Boolean = favorite.isEmpty()
//
//    fun size() : Int = favorite.size
//
//    fun getAll() : List<String> = favorite.toList()

    companion object {
        val INSTANCE : FavoriteRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { FavoriteRepository() }
    }
}