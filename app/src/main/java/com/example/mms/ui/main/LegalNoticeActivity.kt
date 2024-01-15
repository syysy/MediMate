package com.example.mms.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mms.databinding.ActivityLnBinding

class LegalNoticeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLnBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.backButton.buttonArrowBack.setOnClickListener {
            finish()
        }
    }
}