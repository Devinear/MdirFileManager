package com.example.projects.mdir.repository

sealed class SortBy {
    internal object Favorite : SortBy()
    internal object Name : SortBy()
    internal object Size : SortBy()
    internal object Date : SortBy()
}