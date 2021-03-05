package com.example.projects.mdir.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.ThumbnailUtils
import android.os.Build
import android.util.Size
import com.example.projects.mdir.common.ExtType
import java.io.File

class ThumbnailRepository {

    private val thumbs = mutableMapOf<String, Bitmap>()

    private fun insertThumb(path: String, thumb: Bitmap) {
        thumbs[path]?.recycle()
        thumbs[path] = thumb
    }

    fun requestDrawable(context: Context, path: String, type: ExtType = ExtType.Image) : BitmapDrawable? {
        return if(isExist(path)) {
            BitmapDrawable(context.resources, thumbs[path])
        }
        else {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val bitmap = when (type) {
                        ExtType.Video, ExtType.Audio ->
                            ThumbnailUtils.createVideoThumbnail(File(path), Size(300, 400), null)
                        else ->
                            BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = 4 })
                    }
                    insertThumb(path = path, thumb = bitmap)
                    BitmapDrawable(context.resources, bitmap)
                }
                else {
                    return null
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }

    private fun isExist(path: String) : Boolean {
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