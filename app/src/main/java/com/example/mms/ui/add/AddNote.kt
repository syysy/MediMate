package com.example.mms.ui.add

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mms.databinding.ActivityAddNoteBinding
class AddNote : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.buttonArrowBack.setOnClickListener {
            finish()
        }

        binding.addButton.setOnClickListener {
            finish()
            Toast.makeText(baseContext, "Il est impossible d'ajouter des notes pour l'intant", Toast.LENGTH_SHORT).show()
        }
    }
}