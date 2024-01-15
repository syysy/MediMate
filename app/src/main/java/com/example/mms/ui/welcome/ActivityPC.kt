package com.example.mms.ui.welcome

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mms.databinding.ActivityPcBinding

class ActivityPC : AppCompatActivity() {

    private lateinit var binding: ActivityPcBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityPcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton = binding.backButton
        backButton.root.setOnClickListener {
            finish()
        }
    }
}