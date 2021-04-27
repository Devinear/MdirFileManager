package com.example.projects.mdir

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projects.R
import com.example.projects.mdir.repository.AbsStorageRepository
import com.example.projects.mdir.repository.InitRepository
import com.example.projects.mdir.repository.LegacyStorageRepository

class IntroActivity : AppCompatActivity(R.layout.activity_intro) {

    private val repository: AbsStorageRepository by lazy {
        LegacyStorageRepository.INSTANCE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository.initRepository(object :InitRepository {
            override fun finish(complete: Boolean) {
                startActivity(Intent(this@IntroActivity, FileManagerActivity::class.java))
                finish()
            }
        })
    }
}