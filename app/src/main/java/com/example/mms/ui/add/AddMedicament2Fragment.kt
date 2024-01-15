package com.example.mms.ui.add

import android.app.Activity.RESULT_CANCELED
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.mms.R
import com.example.mms.constant.TYPE_PRIS_AUTRE
import com.example.mms.constant.TYPE_PRIS_JOURNALIERE
import com.example.mms.constant.TYPE_PRIS_PONCTUELLE
import com.example.mms.constant.frequenceIdToString
import com.example.mms.databinding.FragmentAddMedicament2Binding

class AddMedicament2Fragment : Fragment() {
    private var _binding: FragmentAddMedicament2Binding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SharedAMViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMedicament2Binding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.backButton.buttonArrowBack.setOnClickListener {
            requireActivity().setResult(RESULT_CANCELED)
            requireActivity().finish()
        }

        this.viewModel = ViewModelProvider(requireActivity())[SharedAMViewModel::class.java]

        for (radio in listOf(
            binding.radioUnPlusieursFoisParJour,
            binding.radioUnique,
            binding.radioPlus,
        )) {
            radio.setOnClickListener {
                this.setTypeIntoViewModel(radio.id)
            }
        }

        binding.nextButton.setOnClickListener {
            val task = this.viewModel.taskData.value ?: return@setOnClickListener

            requireActivity().runOnUiThread {
                val nextFragmentId = when (task.type) {
                    TYPE_PRIS_JOURNALIERE -> R.id.action_AM2_Fragment_to_AMUnOuPlusieursParJours_Fragment
                    TYPE_PRIS_PONCTUELLE -> R.id.action_AM2_Fragment_to_AMOneTake_Fragment
                    TYPE_PRIS_AUTRE -> R.id.action_AM2_Fragment_to_AMPlus_Fragment
                    else -> {
                        return@runOnUiThread
                    }
                }

                val navHostFragment =
                    requireActivity().supportFragmentManager.findFragmentById(R.id.nav_add_medicament) as NavHostFragment
                val navController = navHostFragment.navController
                navController.navigate(nextFragmentId)
            }
        }

        return root
    }

    private fun setTypeIntoViewModel(type: Int) {
        val task = this.viewModel.taskData.value

        requireActivity().runOnUiThread {
            if (task != null) {
                task.type = frequenceIdToString[type] ?: "autre"
                this.viewModel.setTask(task)
            } else {
                Toast.makeText(
                    this@AddMedicament2Fragment.context,
                    R.string.error,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
