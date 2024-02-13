package com.example.mms.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.mms.R
import com.example.mms.Utils.goTo
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.databinding.FragmentFindDoctorBinding
import com.example.mms.model.Doctor
import com.example.mms.service.DoctorApiService


class FindDoctorFragment: Fragment() {

    private var _binding: FragmentFindDoctorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFindDoctorBinding.inflate(inflater, container, false)

        binding.backButton.root.setOnClickListener {
            this.backToModifyMedecin()
        }

        binding.searchDoctor.setOnClickListener {
            // get input's data
            val rpps = binding.rppsCode.text.toString().trim()
            val firstName = binding.docteurPrenom.text.toString()
            val lastName = binding.docteurNom.text.toString()

            val doctorApi = DoctorApiService.getInstance(this.requireContext())

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

        return binding.root
    }

    private fun addDoctors(doctors: List<Doctor>) {
        if (doctors.size == 1) {
            val doctor = doctors[0]

            this.insertDoctor(doctor)
            this.toast(getString(R.string.medecin_ajoute))
        } else {
            TODO("proposer une liste de docteurs")
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this.requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun insertDoctor(doctor: Doctor) {
        val thread = Thread {
            val db = SingletonDatabase.getDatabase(this.requireContext())
            val doctorDao = db.doctorDao()

            doctorDao.insert(doctor)
        }
        thread.start()
        thread.join()

        this.toast(getString(R.string.medecin_ajoute))
        this.backToModifyMedecin()
    }

    private fun backToModifyMedecin() {
        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.action_navigation_find_doctor_to_navigation_modify_medecin)
    }
}
