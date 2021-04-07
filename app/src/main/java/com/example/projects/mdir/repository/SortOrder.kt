package com.example.projects.mdir.repository

//sealed class SortOrder {
//    internal object Ascending : SortOrder()
//    internal object Descending : SortOrder()
//}

enum class SortOrder(val position : Int) {
    Ascending(0),
    Descending(1),
}