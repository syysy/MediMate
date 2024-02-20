package com.example.mms.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mms.R
import com.example.mms.Utils.goToInAddFragments
import com.example.mms.databinding.FragmentAddMedicamentOneTakeBinding

class AddMedicamentOneTakeFragment : Fragment() {
    private var _binding: FragmentAddMedicamentOneTakeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SharedAMViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[SharedAMViewModel::class.java]

        viewModel.setPreviousFragmentId(findNavController().currentDestination!!.id)

        _binding = FragmentAddMedicamentOneTakeBinding.inflate(inflater, container, false)

        val root: View = binding.root

        binding.backButton.buttonArrowBack.setOnClickListener {
            goToInAddFragments(requireActivity(), R.id.action_AMOneTake_to_AM2_Fragment)
        }

        binding.nextButton.setOnClickListener {
            val weight = binding.inputWeight.text.toString().toIntOrNull() ?: -1

            // Check if the weight is a positive number
            if (weight < 0) {
                Toast.makeText(
                    requireContext(),
                    this.requireContext().getString(R.string.weight_sup_a_0),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            viewModel.setOneTakeWeight(weight)
            goToInAddFragments(requireActivity(), R.id.action_AMOneTake_to_Recap)
        }

        return root
    }
}
