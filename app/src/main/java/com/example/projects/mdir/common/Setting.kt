package com.example.projects.mdir.common

import com.example.projects.mdir.repository.SortBy
import com.example.projects.mdir.repository.SortOrder

object Setting {
    // 폴더에 '.nomedia' 파일이 포함된 경우 이미지, 비디오 등 미디어로 조회하는 경우 해당 폴더 및 하위 폴더의 모든 내용을 숨긴다.
    // 일반 파일 보는 동작의 경우에는 보여야 한다.
    var hideNomedia = true
    var hideSystem = true // 파일(폴더)의 이름이 '.'으로 시작한 경우 해당 파일(폴더, 하위 포함)을 숨긴다.

    var sortPairFir : Pair<SortBy, SortOrder> = Pair(SortBy.Type, SortOrder.Ascending)
    var sortPairSec : Pair<SortBy, SortOrder> = Pair(SortBy.Type, SortOrder.Ascending)
}