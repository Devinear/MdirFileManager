package com.example.projects.mdir.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.ThumbnailUtils
import android.os.Build
import android.util.Size
import android.util.TypedValue
import com.example.projects.mdir.common.ExtType
import com.example.projects.mdir.common.Image
import java.io.File
import kotlin.math.max

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
                val bitmap = when (type) {
                    ExtType.Video, ExtType.Audio -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                            ThumbnailUtils.createVideoThumbnail(File(path), Size(300, 400), null)
                        else
                            return null
                    }
                    else -> {
                        val option = getBitmapSize(path)
                        val sample : Int =
                            max(option.outWidth, option.outHeight) / dpToPx(context, Image.widthGridItemDp.toFloat())

                        BitmapFactory.decodeFile(
                            path,
                            BitmapFactory.Options().apply {
                                inSampleSize = if(sample > 0) sample else 0
                            })
                    }
                }
                insertThumb(path = path, thumb = bitmap)
                BitmapDrawable(context.resources, bitmap)
            }
            catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }

    private fun getBitmapSize(path: String) : BitmapFactory.Options {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(path, options)
        return options
    }

    private fun dpToPx(context: Context, dp: Float) : Int
        = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()


    private fun pxToDp(context: Context, px: Int) : Float =
        when(val density = context.resources.displayMetrics.density) {
            1.0f -> px / 4f
            1.5f -> px / 8f / 3f
            2.0f -> px / 2f
            else -> px / density
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