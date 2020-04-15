package com.example.mdirfilemanager.common

import androidx.annotation.ColorRes
import com.example.mdirfilemanager.R

enum class FileType(@ColorRes val color : Int, val abbr: String /*abbreviation*/, val sort: Int) {
    UpDir(R.color.type_dir, "[ Up-Dir ]", 1),
    Dir(R.color.type_dir,   "[ SubDir ]", 2),
    Default(R.color.type_default, "", 3),
    None(R.color.type_white,"", 4),
    Storage(R.color.type_storage, "", 5),
    Image(R.color.type_blue_1, "", 6),
    Video(R.color.type_green_1, "", 7),
    Audio(R.color.type_yellow, "", 8)
}

enum class ExtType {
    Image,
    Video,
    Audio,
    Document,
    Default
}