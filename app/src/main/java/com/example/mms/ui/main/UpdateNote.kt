package com.example.mms.ui.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import com.example.mms.databinding.ActivityAddNoteBinding
import com.example.mms.databinding.ActivityUpdateNoteBinding

class UpdateNote : AppCompatActivity() {

    companion object {
        const val CLE = "cle"
    }

    private lateinit var binding: ActivityUpdateNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityUpdateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.extras?.getInt("id")
        val position = intent.extras?.getInt("position")
        binding.textNote.setText(intent.extras?.getString("text"))

        // When back button clicked, go back on Dairy without changing
        binding.backButton.buttonArrowBack.setOnClickListener {
            finish()
        }

        // When button delete clicked, send code = RESULT_OK (-1), and the position
        binding.deleteButton.setOnClickListener {
            val deleteValue = Intent().putExtra("delete", "deleteOk").putExtra("position", position)
            setResult(AppCompatActivity.RESULT_OK, deleteValue)
            finish()
        }

        // When button update clicked, send code = RESULT_OK (-1), the position and the note
        binding.updateButton.setOnClickListener {
            val saisi = binding.textNote.text.toString()

            val valeurRetour = Intent()
                                .putExtra(CLE, saisi)
                                .putExtra("id", id)
                                .putExtra("position", position)
            setResult(AppCompatActivity.RESULT_OK, valeurRetour)
            finish()
        }
    }
}