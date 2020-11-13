package com.example.projects.mdir.repository

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.example.projects.mdir.data.FileItem

class ScopeStorageRepository : AbsStorageRepository() {

    override fun loadDirectory(path: String): MutableList<FileItem> {
        return super.loadDirectory(path)
    }

    override fun loadDirectory(tree: DocumentFile, context: Context): MutableList<FileItem> {
        return super.loadDirectory(tree, context)
    }
}