package com.example.projects.mdir.data

import android.graphics.drawable.BitmapDrawable
import com.example.projects.mdir.common.FileType

data class FileItem(var name: String,
                    var ext: String,
                    var fullname: String = "",
                    var path: String,
                    var type: FileType,
                    var byteSize: Long,
                    var time: String,
                    var drawable: BitmapDrawable? = null,
                    var childCount: Int = 0)