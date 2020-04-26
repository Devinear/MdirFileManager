package com.example.projects

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.projects.mdir.FileManagerActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btMdir : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btMdir = findViewById(R.id.btn_mdir)
        btMdir.setOnClickListener {
            startActivity(Intent(this, FileManagerActivity::class.java))
        }
    }
}
