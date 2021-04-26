package com.example.projects.mdir

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.projects.R

class IntroActivity : AppCompatActivity(R.layout.activity_intro) {

    private val viewModel by lazy {
        ViewModelProvider(this).get(FileViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler().postDelayed({
            startActivity(Intent(this, FileManagerActivity::class.java))
            finish()
        }, 100)
    }
}