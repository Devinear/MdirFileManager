package com.example.projects.mdir.view

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel

class HomeViewModel(val context: Context) : ViewModel() {

    fun onClickStorage() {
        Toast.makeText(context, "STORAGE", Toast.LENGTH_SHORT).show()
    }
}