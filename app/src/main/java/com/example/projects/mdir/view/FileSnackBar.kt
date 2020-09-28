package com.example.projects.mdir.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.projects.R
import com.example.projects.databinding.LayoutSnackbarBinding
import com.example.projects.mdir.common.ExtType
import com.example.projects.mdir.common.FileUtil
import com.example.projects.mdir.data.FileItem
import java.io.File

@SuppressLint("ViewConstructor")
class FileSnackBar(context: Context, val item: FileItem, val path: String) : View(context) {

    private lateinit var binding : LayoutSnackbarBinding

//    val view : View by lazy {
//        inflate(context, R.layout.layout_snackbar, null)
//    }
    val view : View
        get() = binding.root

    init {
        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_snackbar, null, false)
    }

    fun rename() = Toast.makeText(context, "RENAME", Toast.LENGTH_SHORT).show()

    fun copy() = Toast.makeText(context, "COPY", Toast.LENGTH_SHORT).show()

    fun share() {
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

    fun delete() = Toast.makeText(context, "DELETE", Toast.LENGTH_SHORT).show()

    fun favorite() = Toast.makeText(context, "FAVORITE", Toast.LENGTH_SHORT).show()
}