package com.example.mms.ui.createAccount

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.mms.database.inApp.AppDatabase
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.MainActivity
import com.example.mms.R
import com.example.mms.Utils.hashString
import com.example.mms.databinding.FragmentCreateAccountPinBinding

class CAPinFragment : Fragment() {

    private var _binding: FragmentCreateAccountPinBinding? = null
    private lateinit var db: AppDatabase
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewModel = ViewModelProvider(requireActivity())[SharedCAViewModel::class.java]
        db = SingletonDatabase.getDatabase(requireContext())
        _binding = FragmentCreateAccountPinBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(com.example.mms.R.id.nav_create_account) as NavHostFragment
        val navController = navHostFragment.navController

        binding.codePin.backButton.buttonArrowBack.setOnClickListener {
            navController.navigate(com.example.mms.R.id.action_navigation_CAPin_to_navigation_CAInformations)
        }

        binding.codePin.userEmail.isVisible = false
        binding.codePin.textView5.text = getString(R.string.create_pin)

        var numbers = mutableListOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        numbers = numbers.shuffled() as MutableList<Int>
        val listButtons = mutableListOf(
            binding.codePin.button0,
            binding.codePin.button1,
            binding.codePin.button2,
            binding.codePin.button3,
            binding.codePin.button4,
            binding.codePin.button5,
            binding.codePin.button6,
            binding.codePin.button7,
            binding.codePin.button8,
            binding.codePin.button9
        )
        for (i in 0 until numbers.size) {
            listButtons[i].text = numbers[i].toString()
        }

        var currentIndex = 0
        val listCodePin = mutableListOf(
            binding.codePin.codePin1,
            binding.codePin.codePin2,
            binding.codePin.codePin3,
            binding.codePin.codePin4
        )
        listCodePin.forEach { it.inputType = InputType.TYPE_CLASS_NUMBER; it.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(1))}
        binding.codePin.buttonDelete.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                listCodePin[currentIndex].setText("")
            }
        }
        for (button in listButtons) {
            button.setOnClickListener {
                listCodePin[currentIndex].setText(button.text.toString())
                currentIndex++
                if (currentIndex > 3) {
                    currentIndex = 3
                }
            }
        }

        Thread {
            val isLinkedToBiometric = db.userDao().getBiometricUsers()
            if (isLinkedToBiometric.isNotEmpty()) {
                requireActivity().runOnUiThread {
                    binding.switchBiometric.visibility = View.INVISIBLE
                    binding.switchBiometric.isEnabled = false
                }
            }
        }.start()

        binding.buttonCreate.setOnClickListener {
            if(listCodePin[0].text.toString() == "" || listCodePin[1].text.toString() == "" || listCodePin[2].text.toString() == "" || listCodePin[3].text.toString() == "") {
                Toast.makeText(requireContext(), getString(R.string.pin_empty), Toast.LENGTH_SHORT).show()
            }else {
                Thread {
                    val user = viewModel.userData.value
                    val codePin =
                        listCodePin[0].text.toString() + listCodePin[1].text.toString() + listCodePin[2].text.toString() + listCodePin[3].text.toString()
                    user?.codePin = hashString(codePin)
                    user?.isLinkedToBiometric = binding.switchBiometric.isChecked
                    db.userDao().insertUser(user!!)
                    requireActivity().runOnUiThread {
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }
                }.start()
            }

        }

        binding.codePin.buttonReturnToBiometrics.isEnabled = false
        binding.codePin.buttonReturnToBiometrics.visibility = View.INVISIBLE

        return root

    }

}