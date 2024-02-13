package com.example.mms.ui.doctor

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mms.databinding.ActivityModifyMedecinBinding

class ModifyMedecinFragment : AppCompatActivity() {
    private lateinit var binding: ActivityModifyMedecinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityModifyMedecinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.root.setOnClickListener {
            finish()
        }

        binding.searchDoctor.setOnClickListener {
            val intent = Intent(this, FindDoctorFragment::class.java)
            startActivity(intent)
        }

        binding.buttonValiderMedecin.setOnClickListener {
            // TODO: save the doctor
        }
    }
}
