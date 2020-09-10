package com.example.projects.mdir.data

import android.graphics.drawable.BitmapDrawable
import com.example.projects.mdir.common.FileType

data class FileItem(var name: String, /*var type: String,*/ var type: FileType, var ext: String, var byteSize: Long, var time: String, var drawable: BitmapDrawable? = null)