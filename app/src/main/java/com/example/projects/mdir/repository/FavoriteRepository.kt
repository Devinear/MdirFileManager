package com.example.projects.mdir.repository

class FavoriteRepository {

    private val favorite = mutableSetOf<String>()

    fun add(path: String) : Boolean = favorite.add(path)

    fun remove(path: String) : Boolean = favorite.remove(path)

    fun clear() = favorite.clear()

    fun contains(path: String) : Boolean = favorite.contains(path)

    fun isEmpty() : Boolean = favorite.isEmpty()

    fun size() : Int = favorite.size

    fun getAll() : List<String> = favorite.toList()

    companion object {
        val INSTANCE : FavoriteRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { FavoriteRepository() }
    }
}