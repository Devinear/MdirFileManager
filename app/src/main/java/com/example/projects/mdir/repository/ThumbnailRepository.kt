package com.example.projects.mdir.repository

import android.graphics.Bitmap

class ThumbnailRepository {

    private val thumbs = mutableMapOf<String, Bitmap>()

    fun insertThumb(path: String, thumb: Bitmap) {
        thumbs[path].also {
            it?.recycle()
            thumb
        }
    }

    fun getThumb(path: String) : Bitmap? {
        return thumbs[path]
    }

    fun isExist(path: String) : Boolean {
        return thumbs[path] != null
    }

    fun deleteThumb(path: String) {
        thumbs[path]?.recycle()
        thumbs.remove(path)
    }

    fun deleteAll() {
        thumbs.forEach { (_, thumb) -> thumb.recycle() }
        thumbs.clear()
    }

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ThumbnailRepository() }
    }
}