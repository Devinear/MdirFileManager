package com.example.projects.mdir.view

import android.os.Parcel
import android.os.Parcelable
import com.example.projects.mdir.common.BrowserType
import com.example.projects.mdir.common.Category

class BrowserData() : Parcelable {

    private lateinit var _type : BrowserType
    val type
        get() = _type

    private var _category : Category? = null
    val category
        get() = _category

    constructor(parcel: Parcel) : this() {
        val input = parcel.readInt()

        for(type in BrowserType.values()) {
            if(type.ordinal == input) {
                this._type = type
                break
            }
        }
    }

    constructor(type: BrowserType, category: Category? = null) : this() {
        this._type = type
        this._category = category
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(_type.ordinal)
    }

    /* 데이터가 어떤 종류인지 설명
    * Parcelable 객체가 File Descriptor를 포함하고 있따면 CONTENTS_FILE_DESCRIPTOR를 리턴하고 그 외는 0을 리턴 */
    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<BrowserData> {
        override fun createFromParcel(parcel: Parcel): BrowserData {
            return BrowserData(parcel)
        }

        override fun newArray(size: Int): Array<BrowserData?> {
            return arrayOfNulls(size)
        }
    }
}