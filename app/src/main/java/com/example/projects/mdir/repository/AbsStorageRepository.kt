package com.example.projects.mdir.repository

import com.example.projects.mdir.data.FileItem

abstract class AbsStorageRepository {

    internal var sortBy : SortBy = SortBy.Name

    internal var sortOrder : SortOrder = SortOrder.Ascending

    open fun loadDirectory(path: String) = mutableListOf<FileItem>()

}