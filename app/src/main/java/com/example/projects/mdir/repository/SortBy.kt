package com.example.projects.mdir.repository

sealed class SortBy {
    internal object Name : SortBy()
    internal object Type : SortBy()
    internal object Size : SortBy()
    internal object Date : SortBy()
    internal object Favorite : SortBy()
}