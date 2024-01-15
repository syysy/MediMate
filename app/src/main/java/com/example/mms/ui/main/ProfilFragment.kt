package com.example.mms.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mms.database.inApp.AppDatabase
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.R
import com.example.mms.Utils.getAgeFromStringBirthDate
import com.example.mms.contrat.ModifiyAccountContrat
import com.example.mms.databinding.FragmentProfilBinding
import com.example.mms.service.TasksService
import com.example.mms.ui.loader.LoaderActivity
import com.example.mms.ui.modifyAccount.ModifyAccountActivity
import com.example.mms.ui.settings.SettingsActivity

class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private lateinit var db: AppDatabase
    private lateinit var viewModel: MainViewModel

    private val binding get() = _binding!!

    val modifyContrat : ActivityResultLauncher<Void?> = registerForActivityResult(
        ModifiyAccountContrat()
    ) {
        if (it.name != "") {
            viewModel.setUserData(it)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        db = SingletonDatabase.getDatabase(requireContext())
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        val tasksService = TasksService(requireContext())
        val user = viewModel.userData.value

        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.userData.observe(viewLifecycleOwner) {
            binding.userName.text = it.surname
            binding.userNameProfil.text = it.surname
            binding.userAge.text =
                getAgeFromStringBirthDate(it.birthday).toString() + " " + resources.getString(
                    R.string.ans
                )
            binding.userSex.text = it.sexe
            binding.userWeight.text = "${it.weight.toString()} " + resources.getString(R.string.kg)
            binding.userHeight.text = "${it.height.toString()} " + resources.getString(R.string.cm)
        }

        val tt = Thread {
            if (user != null) {
                val usersStats = tasksService.getUserStats()
                Log.d("ProfilFragment", usersStats.toString())
                val takesDone = usersStats.first
                val takesPercent = usersStats.second
                requireActivity().runOnUiThread {
                    binding.progressBarTakes.setProgress(takesPercent)
                    binding.percentTaken.text = takesPercent.toString() + "%"
                    binding.nbDoses.text = takesDone.toString() + " " + resources.getString(R.string.doses)
                    if(takesDone == 0) binding.felicitations.text = ""
                    if(takesPercent <= 50) {
                        binding.felicitations.text = resources.getString(R.string.des_efforts_sont_faire)
                    }else {
                        binding.felicitations.text = resources.getString(R.string.bravo_vous_tes_r_gulier)
                    }
                }
            }
        }
        tt.start()
        tt.join()

        binding.buttonLogout.setOnClickListener {
            Thread {
                if (user != null) db.userDao().updateIsConnected(false, user.email)
                val intent = Intent(this.context, LoaderActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                requireActivity().finish()
            }.start()
        }


        binding.buttonParams.setOnClickListener {
            startActivity(Intent(root.context, SettingsActivity::class.java))
        }

        binding.buttonModifier.setOnClickListener {
            modifyContrat.launch(null)
        }

        binding.buttonLegalNotice.setOnClickListener {
            startActivity(Intent(root.context, LegalNoticeActivity::class.java))
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}