package com.example.projects.mdir.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import com.example.projects.R

class CustomProgressDialog(context: Context) : AlertDialog(context) {

    private val message by lazy { findViewById<AppCompatTextView>(R.id.message) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_progress)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun setMessage(message: String) {
        this.message?.text = message
    }

}