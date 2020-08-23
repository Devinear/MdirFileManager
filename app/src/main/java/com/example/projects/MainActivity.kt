package com.example.projects

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.projects.databinding.ActivityMainBinding
import com.example.projects.maps.MapActivity
import com.example.projects.mdir.FileManagerActivity

class MainActivity : AppCompatActivity() {

//    private lateinit var btMdir : Button
//    private lateinit var btMap  : Button
//    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding : ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.btnMdir.setOnClickListener {
            startActivity(Intent(this, FileManagerActivity::class.java))
        }
        binding.btnMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

//        setContentView(R.layout.activity_main)
//        btMdir = findViewById(R.id.btn_mdir)
//        btMdir.setOnClickListener { startActivity(Intent(this, FileManagerActivity::class.java)) }
//        btMap = findViewById(R.id.btn_map)
//        btMap.setOnClickListener { startActivity(Intent(this, MapActivity::class.java)) }
    }
}
