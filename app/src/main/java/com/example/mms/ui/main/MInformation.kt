package com.example.mms.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.databinding.ActivityInfoMedicamentBinding
import com.example.mms.model.medicines.Medicine

class MInformation: AppCompatActivity() {
    private lateinit var binding: ActivityInfoMedicamentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityInfoMedicamentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get information
        val cis = intent.extras?.getString("cis")
        var medicine: Medicine? = null

        val t = Thread {
            val mediDb = SingletonDatabase.getDatabase(this).medicineDao()
            medicine = mediDb.getByCIS(cis!!.toLong())
        }
        t.start()
        t.join()

        // display information
        binding.nomMedicament.text = intent.extras?.getString("nom")
        binding.codeCisText.text = cis
        binding.typeText.text = medicine?.type?.generic.toString()
        binding.compoText.text = "${medicine?.composition?.substance_name} ${medicine?.composition?.substance_dosage}"
        binding.usageText.text = medicine?.usage?.route_administration.toString()


        // When back button clicked, go back on Dairy without changing
        binding.backButton.buttonArrowBack.setOnClickListener {
            finish()
        }
    }
}