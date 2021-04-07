package com.example.projects.mdir.repository

//sealed class SortBy {
//    internal object Name : SortBy()
//    internal object Type : SortBy()
//    internal object Size : SortBy()
//    internal object Date : SortBy()
//    internal object Favorite : SortBy()
//}

enum class SortBy(val position : Int) {
    Name(0),
    Type(1),
    Size(2),
    Date(3),
    Favorite(4),
}