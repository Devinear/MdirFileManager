package com.example.projects.mdir

import com.example.projects.mdir.common.FileType

data class FileItem(var name: String, /*var type: String,*/ var type: FileType, var ext: String, var byteSize: Long, var time: String)