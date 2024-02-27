package com.example.mms.ui.add

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mms.MainActivity
import com.example.mms.R
import com.example.mms.Utils.getFormattedDate
import com.example.mms.Utils.goToInAddFragments
import com.example.mms.adapter.InteractionsAdapter
import com.example.mms.adapter.RecapSpecificDaysAdapter
import com.example.mms.dao.InteractionDao
import com.example.mms.database.inApp.AppDatabase
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.databinding.FragmentAddRecapBinding
import com.example.mms.model.Cycle
import com.example.mms.model.Interaction
import com.example.mms.model.Task
import com.example.mms.model.medicines.Medicine
import com.example.mms.service.NotifService
import com.example.mms.service.TasksService
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AddMedicamentRecapFragment : Fragment() {
    private var _binding: FragmentAddRecapBinding? = null
    private val binding get() = _binding!!
    private lateinit var tasksService: TasksService
    private lateinit var viewModel: SharedAMViewModel

    private lateinit var saveFunction: (Task) -> Unit
    private lateinit var medicine: Medicine
    private var taskIsOnlyOneTime: Boolean = false
    private lateinit var db: AppDatabase
    private lateinit var interactions: List<Interaction>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[SharedAMViewModel::class.java]
        tasksService = TasksService(requireContext())
        db = SingletonDatabase.getDatabase(requireContext())

        _binding = FragmentAddRecapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // get values from viewModel
        val task = viewModel.taskData.value!!
        val cycle = viewModel.cycle.value
        val specificDays = viewModel.specificDays.value
        val medicineName = viewModel.medicineName.value

        Thread {
            val db = SingletonDatabase.getDatabase(requireContext())
            medicine = db.medicineDao().getByCIS(task.medicineCIS)!!

            requireActivity().runOnUiThread {
                // set medicine informations
                binding.typeMedecine.text = medicine.type.complet
                binding.dosageMedicine.text = medicine.type.weight
                binding.nameMedicine.text = medicineName
            }
        }.start()

        val interactionDao = InteractionDao(requireContext())

        val thread = Thread {
            this.interactions = interactionDao.thisMedicineInteractsWith(medicine, this.tasksService.getCurrentUserMedicines())
        }
        thread.start()
        thread.join()

        if (this.interactions.isNotEmpty()) {
            binding.btnEffetsSecondaires.visibility = View.VISIBLE
            binding.imageDanger.visibility = View.VISIBLE
        }

        if (cycle != null) {
            // Cycle
            saveFunction = { addedTask ->
                saveCycle(addedTask)
            }

            task.cycle = cycle

            // display informations
            binding.hourTask.text = getHoursCycle(cycle)
            binding.intervalTask.text = task.type
            binding.dateNextTask.text = getFormattedDate(tasksService.getNextTakeDate(task))

        } else if (specificDays != null && specificDays.isNotEmpty()) {
            // SpecificDays
            saveFunction = { addedTask ->
                saveSpecificDays(addedTask)
            }

            binding.hourTask.text = ""
            binding.intervalTask.text = task.type

            // init an adapter to display all hours
            val sdAdapter = RecapSpecificDaysAdapter(requireContext(), specificDays)
            val recyclerView = binding.rvSpecificdays
            recyclerView.adapter = sdAdapter
            recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        } else {
            // OneTake
            taskIsOnlyOneTime = true

            // display informations
            val now = LocalDateTime.now()
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
            val dateString = now.format(dateFormatter)

            binding.dateNextTask.text = dateString

            binding.intervalTask.text = task.type
            binding.hourTask.text = ""
        }

        binding.btnEffetsSecondaires.setOnClickListener {
            this.openInteractionsDialog()
        }

        binding.backButton.buttonArrowBack.setOnClickListener {
            goToInAddFragments(requireActivity(), R.id.action_recap_to_start_end_date)
        }

        binding.btnTaskValidate.setOnClickListener {
            this.checkIfUserAlreadyTakeThisSubstance()
        }

        return root
    }

    /**
     * Save a cycle in database
     *
     * @param addedTask the task to save
     */
    private fun saveCycle(addedTask: Task) {
        val cycle = viewModel.cycle.value!!
        cycle.taskId = addedTask.id
        tasksService.storeCycle(cycle)
    }

    /**
     * Save specific days in database
     */
    private fun saveSpecificDays(addedTask: Task) {
        val specificDays = viewModel.specificDays.value!!
        for (specificDay in specificDays) {
            specificDay.taskId = addedTask.id
            this.tasksService.storeSpecificDays(specificDay)
        }
    }

    /**
     * Save a one take task in database
     */
    private fun saveOneTakeTask() {
        val cis = viewModel.taskData.value!!.medicineCIS
        val weight = viewModel.oneTakeWeight.value!!

        val thread = Thread {
            this.tasksService.storeOneTake(cis, weight)
        }
        thread.start()
        thread.join()
    }

    /**
     * Save the task in database and redirect to main activity
     */
    private fun saveAndRedirect() {
        if (viewModel.storage.value != null) {
            // save storage
            val t = Thread {
                db.medicineStorageDao().insert(viewModel.storage.value!!)
            }
            t.start()
            t.join()
        }
        var idLastInserted = -1L

        if (taskIsOnlyOneTime) {
            saveOneTakeTask()
        } else {
            val thread = Thread {
                val db = SingletonDatabase.getDatabase(this.requireContext())
                val taskDAO = db.taskDao()
                val userDAO = db.userDao()

                val currentUserId = userDAO.getConnectedUser()!!.email

                // store the task
                val task = viewModel.taskData.value!!
                task.userId = currentUserId
                this.tasksService.storeTask(task)

                val addedTask = taskDAO.getLastInserted()!!
                idLastInserted = addedTask.id
                saveFunction(addedTask)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // planify notification
                    val now = LocalDateTime.now()
                    val taskWithHW = this.tasksService.getByIdAt(addedTask.id, now)
                    var todaysSwHourWeightsTask = this.tasksService.createOrGetOneTodaysSwHourWeight(taskWithHW)
                    todaysSwHourWeightsTask = this.tasksService.removeAlreadyPassedHourWeights(todaysSwHourWeightsTask)

                    if (todaysSwHourWeightsTask.isNotEmpty()) {
                        val notifService = NotifService(this.requireContext())
                        notifService.planifyOneNotification(todaysSwHourWeightsTask.first())
                    }
                }
            }
            thread.start()
            thread.join() // wait for thread to finish
        }

        // if the user come from OCR, we redirect him to the main activity
        if (viewModel.fromOCR.value!!){
            val intent = Intent().putExtra("taskId", idLastInserted )
            requireActivity().setResult(RESULT_OK, intent)
            requireActivity().finish()
        } else {
            val intent = Intent(this.requireContext(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    /**
     * Get all hours of a cycle
     */
    private fun getHoursCycle(cycle: Cycle): String {
        var hours = ""
        for (hourWeight in cycle.hourWeights) {
            val hour = hourWeight.hour
            hours += "$hour, "
        }

        return hours
    }

    private fun openInteractionsDialog() {
        val interactionsAdapters = InteractionsAdapter(interactions)

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dialog_interaction)

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.rv_interactions)
        recyclerView.adapter = interactionsAdapters
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        val btnClose = dialog.findViewById<View>(R.id.btn_close_interactions)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun checkIfUserAlreadyTakeThisSubstance() {
        var userAlreadyTakeSameSubstance = false

        val thread = Thread {
            userAlreadyTakeSameSubstance = this.tasksService.ifUserAlreadyTakeThisSubstance(medicine)
        }
        thread.start()
        thread.join()

        if (userAlreadyTakeSameSubstance) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.attention))
            builder.setMessage(getString(R.string.meme_substance_ajouter_quand_meme, medicine.name))

            builder.setPositiveButton(getString(R.string.oui)) { _, _ ->
                saveAndRedirect()
            }

            builder.setNegativeButton(getString(R.string.non)) { _, _ ->
            }

            builder.show()
        } else {
            saveAndRedirect()
        }
    }
}
