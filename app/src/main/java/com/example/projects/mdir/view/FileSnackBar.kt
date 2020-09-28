package com.example.projects.mdir.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.example.projects.R

class FileSnackBar : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet) : super(context, attr)
    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr, defStyleAttr)

    val view : View by lazy {
        inflate(context, R.layout.layout_snackbar, null)
    }
}