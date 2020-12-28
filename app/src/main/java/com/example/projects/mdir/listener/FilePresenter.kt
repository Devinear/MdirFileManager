package com.example.projects.mdir.listener

import com.example.projects.mdir.data.FileItemEx

interface FilePresenter {
    fun onItemClick(item: FileItemEx?)
    fun onMenuClick(item: FileItemEx?)
}