package com.example.projects.mdir.common

import androidx.annotation.ColorRes
import com.example.projects.R

enum class FileType(@ColorRes val color : Int, val abbr: String /*abbreviation*/, val sort: Int, val drawableRes: Int) {
    UpDir(R.color.type_dir, "[ Up-Dir ]", 1, R.drawable.outline_arrow_upward_white_48),
    Dir(R.color.type_dir,   "[ SubDir ]", 2, R.drawable.outline_folder_white_48),
    Default(R.color.type_default, "", 3, R.drawable.outline_insert_drive_file_white_48),
    None(R.color.type_white,"", 4, R.drawable.outline_all_inclusive_white_48),
    Storage(R.color.type_storage, "", 5, R.drawable.outline_sd_card_white_48),
    Image(R.color.type_yellow_1, "", 6, R.drawable.outline_insert_photo_white_48),
    Video(R.color.type_blue_2, "", 7, R.drawable.outline_slideshow_white_48),
    Audio(R.color.colorBurlyWood, "", 8, R.drawable.outline_audiotrack_white_48),
    Document(R.color.colorBlueViolet, "", 9, R.drawable.outline_description_white_48),
    Zip(R.color.type_green_1, "", 10, R.drawable.outline_save_white_48),
    APK(R.color.type_green_2, "", 11, R.drawable.outline_android_white_48)
}

enum class ExtType {
    Image,
    Video,
    Audio,
    Document,
    Zip,
    Apk,
    Default
}

enum class ShowType {
    Img,
    Zip,
    Doc,
    All
}

enum class LayoutType {
    Linear,
    Grid
}

// Android - '내 파일' 카테고리
enum class Category {
    Image,
    Video,
    Audio,
    Document,
    Download,
    APK,
    Zip
}

enum class FragmentType {
    None,
    Home,
    Browser,
    Find,
    Setting
}

enum class BrowserType {
    None,
    Storage,
    Category,
    Find,
    Favorite,
    Recent
}

enum class Command {
    Favorite,
    Rename,
    Share,
    Info,
    Delete
}