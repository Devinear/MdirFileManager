package com.example.projects.mdir.repository

import com.example.projects.mdir.data.FileItem

class ScopeStorageRepository : AbsStorageRepository() {

    override fun loadDirectory(path: String): MutableList<FileItem> {
        return super.loadDirectory(path)
    }
}