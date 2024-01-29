package com.example.mms.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.mms.R
import com.example.mms.Utils.goTo
import com.example.mms.databinding.FragmentFindDoctorBinding


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
            val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.action_navigation_find_doctor_to_navigation_modify_medecin)
        }

        return binding.root

    }
}
