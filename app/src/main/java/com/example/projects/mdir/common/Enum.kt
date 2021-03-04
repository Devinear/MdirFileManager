package com.example.projects.mdir.common

import androidx.annotation.ColorRes
import com.example.projects.R

enum class FileType(@ColorRes val color : Int, val abbr: String /*abbreviation*/, val sort: Int, val drawableRes: Int, val extType: ExtType) {
    UpDir(R.color.type_dir, "[ Up-Dir ]", 1, R.drawable.outline_arrow_upward_white_48, ExtType.Default),
    Dir(R.color.type_dir,   "[ SubDir ]", 2, R.drawable.outline_folder_white_48, ExtType.Default),
    Default(R.color.type_default, "", 3, R.drawable.outline_insert_drive_file_white_48, ExtType.Default),
    None(R.color.type_white,"", 4, R.drawable.outline_all_inclusive_white_48, ExtType.Default),
    Storage(R.color.type_storage, "", 5, R.drawable.outline_sd_card_white_48, ExtType.Default),
    Image(R.color.type_yellow_1, "", 6, R.drawable.outline_insert_photo_white_48, ExtType.Image),
    Video(R.color.type_blue_2, "", 7, R.drawable.outline_slideshow_white_48, ExtType.Video),
    Audio(R.color.colorBurlyWood, "", 8, R.drawable.outline_audiotrack_white_48, ExtType.Audio),
    Document(R.color.colorBlueViolet, "", 9, R.drawable.outline_description_white_48, ExtType.Document),
    Zip(R.color.type_green_1, "", 10, R.drawable.outline_save_white_48, ExtType.Zip),
    APK(R.color.type_green_2, "", 11, R.drawable.outline_android_white_48, ExtType.Apk)
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