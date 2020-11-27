package com.example.projects.mdir.listener

import com.example.projects.mdir.data.FileItemEx

interface OnFileClickListener {
    fun onClickFile(item: FileItemEx)
    fun onLongClickFile(item: FileItemEx)
}