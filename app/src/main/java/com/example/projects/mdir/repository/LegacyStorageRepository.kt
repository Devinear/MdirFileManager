package com.example.projects.mdir.repository

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.example.projects.mdir.data.FileItem
import java.io.File

class LegacyStorageRepository : AbsStorageRepository() {

    override fun loadDirectory(file: File): MutableList<FileItem> {
        return super.loadDirectory(file)
    }

    override fun loadDirectory(path: String): MutableList<FileItem> {
        return super.loadDirectory(path)
    }

    override fun loadDirectory(tree: DocumentFile, context: Context): MutableList<FileItem> {
        return super.loadDirectory(tree, context)
    }
}