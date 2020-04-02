package com.example.mdirfilemanager.common

import androidx.annotation.ColorRes
import com.example.mdirfilemanager.R

enum class FileType(@ColorRes val color : Int, val abbr: String /*abbreviation*/) {
    Default(R.color.type_default, ""),
    Storage(R.color.type_storage, ""),
    UpDir(R.color.type_dir, "[ Up-Dir ]"),
    Dir(R.color.type_dir,   "[ SubDir ]"),
    None(R.color.type_white,"")
}