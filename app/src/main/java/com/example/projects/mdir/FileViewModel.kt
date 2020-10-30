package com.example.projects.mdir

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel

class FileViewModel(val app: Application) : AndroidViewModel(app) {



    fun onClickStorage() {
        Toast.makeText(app, "STORAGE", Toast.LENGTH_SHORT).show()
    }

}