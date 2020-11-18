package com.example.projects.mdir.repository

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.example.projects.mdir.data.FileItem
import com.example.projects.mdir.data.FileItemEx
import java.io.File

abstract class AbsStorageRepository {

    internal var sortBy : SortBy = SortBy.Name
    internal var sortOrder : SortOrder = SortOrder.Ascending
    internal var query : String = ""

    open fun loadDirectory(context: Context, file: File) = mutableListOf<FileItemEx>()
    open fun loadDirectory(context: Context, path: String) = mutableListOf<FileItemEx>()
    open fun loadDirectory(context: Context, tree: DocumentFile) = mutableListOf<FileItemEx>()

    fun sort(sortBy: SortBy, sortOrder: SortOrder) {
        this.sortBy = sortBy
        this.sortOrder = sortOrder
    }

    fun search(query: String) {
        this.query = query
    }
}