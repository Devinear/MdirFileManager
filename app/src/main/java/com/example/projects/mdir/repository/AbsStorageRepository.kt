package com.example.projects.mdir.repository

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.example.projects.mdir.data.FileItemEx
import java.io.File

abstract class AbsStorageRepository {

    internal var sortPairFir : Pair<SortBy, SortOrder> = Pair(SortBy.Type, SortOrder.Ascending)
    internal var sortPairSec : Pair<SortBy, SortOrder> = Pair(SortBy.Name, SortOrder.Ascending)

    internal var query : String = ""

    open fun loadDirectory(context: Context, file: File) = mutableListOf<FileItemEx>()
    open fun loadDirectory(context: Context, path: String) = mutableListOf<FileItemEx>()
    open fun loadDirectory(context: Context, tree: DocumentFile) = mutableListOf<FileItemEx>()

    fun sort(sortBy: SortBy, sortOrder: SortOrder, sortBySec: SortBy = SortBy.Name, sortOrderSec: SortOrder = SortOrder.Ascending) {
        sortPairFir = Pair(sortBy, sortOrder)
        sortPairSec = Pair(sortBySec, sortOrderSec)
    }

    fun search(query: String) {
        this.query = query
    }
}