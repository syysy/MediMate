package com.example.mms.ui.add

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mms.database.inApp.AppDatabase
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.databinding.ActivityAddNoteBinding
import com.example.mms.model.DairyNote
import com.example.mms.service.DairyService

class AddNote : AppCompatActivity() {

    companion object {
        const val CLE = "cle"
    }

    private lateinit var binding: ActivityAddNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.extras?.getInt("id")

        // When back button clicked, go back on Dairy without adding
        binding.backButton.buttonArrowBack.setOnClickListener {
            finish()
        }

        // When button add clicked, send code = RESULT_OK (-1), and the note
        binding.addButton.setOnClickListener {
            val saisi = binding.textNote.text.toString()

            val valeurRetour = Intent()
                                .putExtra(CLE, saisi)
                                .putExtra("id", id)
            setResult(RESULT_OK, valeurRetour)
            finish()
        }
    }
}