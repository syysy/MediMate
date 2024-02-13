package com.example.mms.ui.doctor

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mms.R
import com.example.mms.adapter.DoctorsAdapter
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.databinding.ActivityFindDoctorBinding
import com.example.mms.model.Doctor
import com.example.mms.service.DoctorApiService


class FindDoctorFragment : AppCompatActivity() {

    private lateinit var binding: ActivityFindDoctorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityFindDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.root.setOnClickListener {
            this.backToModifyMedecin()
        }

        binding.searchDoctor.setOnClickListener {
            // get input's data
            val rpps = binding.rppsCode.text.toString().trim()
            val firstName = binding.docteurPrenom.text.toString()
            val lastName = binding.docteurNom.text.toString()

            val doctorApi = DoctorApiService.getInstance(this)

            if (rpps.isNotBlank()) {
                doctorApi.getDoctorByRPPS(rpps, { doctors ->
                    this.addDoctors(doctors)
                }, {
                    // show a toast error
                    this.toast(getString(R.string.erreur_recherche_docteur))
                })
            } else if (firstName.isNotBlank() && lastName.isNotBlank()) {
                doctorApi.getDoctorByName(firstName, lastName, { doctors ->
                    this.addDoctors(doctors)
                }, {
                    // show a toast error
                    this.toast(getString(R.string.erreur_recherche_docteur))
                })
            } else {
                // show a toast error
                this.toast(getString(R.string.fill_fields))
            }
        }
    }

    private fun addDoctors(doctors: List<Doctor>) {
        this.openDoctorChoiceDialog(doctors)
//        if (doctors.size == 1) {
//            val doctor = doctors[0]
//
//            this.insertDoctor(doctor)
//            this.toast(getString(R.string.medecin_ajoute))
//        } else {
//            TODO("proposer une liste de docteurs")
//        }
    }

    private fun openDoctorChoiceDialog(doctors: List<Doctor>) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_dialog_doctor)

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.rv_doctors)
        val adapter = DoctorsAdapter(doctors) {
            this.insertDoctor(it)
            dialog.dismiss()
            this.backToAdvice()
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnClose = dialog.findViewById<View>(R.id.btn_close_doctors)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun insertDoctor(doctor: Doctor) {
        val thread = Thread {
            val db = SingletonDatabase.getDatabase(this)
            val doctorDao = db.doctorDao()

            doctorDao.insert(doctor)
        }
        thread.start()
        thread.join()

        this.toast(getString(R.string.medecin_ajoute))
        this.backToModifyMedecin()
    }

    private fun backToModifyMedecin() {
        finish()
    }

    private fun backToAdvice() {
        finish()
    }
}
