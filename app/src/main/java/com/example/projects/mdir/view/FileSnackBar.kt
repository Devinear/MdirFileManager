package com.example.projects.mdir.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.projects.R
import com.example.projects.mdir.common.ExtType
import com.example.projects.mdir.common.FileUtil
import com.example.projects.mdir.data.FileItem
import java.io.File

@SuppressLint("ViewConstructor")
class FileSnackBar(context: Context, val item: FileItem, val path: String) : View(context) {

    val view : View by lazy {
        inflate(context, R.layout.layout_snackbar, null)
    }

    init {
        view.findViewById<TextView>(R.id.bt_rename).setOnClickListener {
            Toast.makeText(context, "RENAME", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<TextView>(R.id.bt_copy).setOnClickListener {  }
        view.findViewById<TextView>(R.id.bt_share).setOnClickListener { share() }
        view.findViewById<TextView>(R.id.bt_delete).setOnClickListener {  }
        view.findViewById<TextView>(R.id.bt_snack_favorite).setOnClickListener {  }
    }

    private fun share() {
        // java.lang.ClassCastException: java.net.URI cannot be cast to android.os.Parcelable 발생
        context.startActivity(Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            type = when (FileUtil.getFileExtType(item.ext)) {
                ExtType.Image -> "image/*"
                ExtType.Video -> "video/*"
                ExtType.Audio -> "audio/*"
                else -> "application/*"
            }
            putExtra(Intent.EXTRA_STREAM, File("$path/${item.name}").toURI())
        }, "Share: ${item.name}.${item.ext}"))
    }
}