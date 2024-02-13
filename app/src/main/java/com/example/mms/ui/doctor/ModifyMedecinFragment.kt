package com.example.mms.ui.doctor

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.mms.R
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.databinding.ActivityModifyMedecinBinding
import com.example.mms.model.Doctor

class ModifyMedecinFragment : AppCompatActivity() {
    private lateinit var binding: ActivityModifyMedecinBinding
    private val db = SingletonDatabase.getDatabase(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var actualDoctor: Doctor? = null
        val thread = Thread {
            actualDoctor = this.db.doctorDao().get()
        }
        thread.start()
        thread.join()

        binding = ActivityModifyMedecinBinding.inflate(layoutInflater)

        supportActionBar?.hide()

        if (actualDoctor != null) {
            // TODO: récup pas les infos du médecin
            binding.editNumeroMedecin.setText(actualDoctor!!.phone ?: "")
            binding.editMailMedecin.setText(actualDoctor!!.email ?: "")
        }

        binding = ActivityModifyMedecinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.root.setOnClickListener {
            finish()
        }

        binding.searchDoctor.setOnClickListener {
            val intent = Intent(this, FindDoctorFragment::class.java)
            startActivity(intent)
            finish()
        }

        if (actualDoctor == null) {
            binding.buttonValiderMedecin.setBackgroundResource(R.drawable.button_style_3_disable)
            binding.buttonValiderMedecin.setTextColor(resources.getColor(R.color.clickable_blue_disable))
            binding.buttonValiderMedecin.isEnabled = false
        }

        binding.buttonValiderMedecin.setOnClickListener {
            if (actualDoctor == null) {
                return@setOnClickListener
            }

            if (binding.editNumeroMedecin.text.toString().isNotEmpty()) {
                actualDoctor!!.phone = binding.editNumeroMedecin.text.toString()
            } else if (binding.editMailMedecin.text.toString().isNotEmpty()) {
                actualDoctor!!.email = binding.editMailMedecin.text.toString()
            } else {
                return@setOnClickListener
            }

            val t = Thread {
                this.db.doctorDao().insert(actualDoctor!!)
            }
            t.start()
            t.join()
            finish()
        }
    }
}
