package com.example.projects.mdir.repository

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.example.projects.mdir.data.FileItem
import java.io.File

abstract class AbsStorageRepository {

    internal var sortBy : SortBy = SortBy.Name
    internal var sortOrder : SortOrder = SortOrder.Ascending
    internal var query : String = ""

    open fun loadDirectory(context: Context, file: File) = mutableListOf<FileItem>()
    open fun loadDirectory(context: Context, path: String) = mutableListOf<FileItem>()
    open fun loadDirectory(context: Context, tree: DocumentFile) = mutableListOf<FileItem>()

    fun sort(sortBy: SortBy, sortOrder: SortOrder) {
        this.sortBy = sortBy
        this.sortOrder = sortOrder
    }

    fun search(query: String) {
        this.query = query
    }
}