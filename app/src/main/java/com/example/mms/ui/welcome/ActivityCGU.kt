package com.example.mms.ui.welcome

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mms.databinding.ActivityCguBinding

class ActivityCGU : AppCompatActivity() {

    private lateinit var binding: ActivityCguBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityCguBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton = binding.backButton
        backButton.root.setOnClickListener {
            finish()
        }
    }
}