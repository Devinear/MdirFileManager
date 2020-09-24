package com.example.projects.mdir.listener

import com.example.projects.mdir.data.FileItem

interface OnFileClickListener {
    fun onClickFile(item: FileItem)
    fun onLongClickFile(item: FileItem)
}