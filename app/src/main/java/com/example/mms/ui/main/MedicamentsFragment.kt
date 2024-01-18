package com.example.mms.ui.main

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mms.R
import com.example.mms.adapter.TakesHistoryAdapter
import com.example.mms.adapter.TaskAdapter
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.databinding.FragmentMecidamentsBinding
import com.example.mms.model.Task
import com.example.mms.service.TasksService
import com.example.mms.ui.add.AddActivity


class MedicamentsFragment : Fragment() {

    private var _binding: FragmentMecidamentsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var tasksService: TasksService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        this._binding = FragmentMecidamentsBinding.inflate(inflater, container, false)
        val root: View = this.binding.root
        val db = SingletonDatabase.getDatabase(requireContext())

        this.tasksService = TasksService(requireContext())

        var items = mutableListOf<Task>()
        val thread = Thread {
            items = this.tasksService.getTasksFilled()
        }
        thread.start()
        thread.join()

        val medicinesRV: RecyclerView = this.binding.medicinesMyMedicines

        binding.noTask.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE

        taskAdapter = TaskAdapter(requireContext(), items, db)
        medicinesRV.layoutManager = LinearLayoutManager(requireContext())
        medicinesRV.adapter = taskAdapter


        binding.btnHistory.setOnClickListener {
            // get data
            val takes = this.tasksService.getCurrentUserAllTakenShowableTakes()

            // set adapter
            val takesHistoryAdapter = TakesHistoryAdapter(requireContext(), takes)

            // create and open dialog
            this.openHistoryDialog(takesHistoryAdapter)
        }

        binding.floatingActionButtonAddMedic.setOnClickListener {
            startActivity(Intent(root.context, AddActivity::class.java))
        }

        return root
    }

    private fun openHistoryDialog(adapter: TakesHistoryAdapter) {
        val dialog = Dialog(this.requireContext())
        dialog.setContentView(R.layout.custom_dialog_history)

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.rv_shtakes)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val btnDelete = dialog.findViewById<View>(R.id.btn_history_cancel)
        btnDelete.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null
    }
}
