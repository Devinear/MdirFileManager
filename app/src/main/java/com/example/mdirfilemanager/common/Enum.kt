package com.example.mdirfilemanager.common

import androidx.annotation.ColorRes
import com.example.mdirfilemanager.R

enum class FileType(@ColorRes val color : Int) {
    Default(R.color.type_default),
    Storage(R.color.type_storage),
    Dir(R.color.type_dir),
    None(R.color.type_white)
}