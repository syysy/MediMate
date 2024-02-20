package com.example.mms.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.get
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mms.adapter.DailyAdapter
import com.example.mms.database.inApp.AppDatabase
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.databinding.FragmentDairyBinding
import com.example.mms.model.DairyNote
import com.example.mms.service.DairyService
import com.example.mms.ui.add.AddNote
import com.example.mms.ui.add.ScanLoading

class DairyFragment: Fragment() {

    private var _binding: FragmentDairyBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var textAdapter: DailyAdapter
    private lateinit var dairyService: DairyService
    private lateinit var note: DairyNote
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dairyService = DairyService(requireContext())
        db = SingletonDatabase.getDatabase(requireContext())

        this._binding = FragmentDairyBinding.inflate(inflater, container, false)
        val root: View = this.binding.root
        val noteRV: RecyclerView = this.binding.notesMyNotes
        var items = mutableListOf<DairyNote>()

        val t = Thread {
            items = db.dairyDao().getAllNote()
        }
        t.start()
        t.join()

        // recover and add note in recyclerView (noteRV)
        val launcherAddNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
                        if (result.resultCode.toString() == "-1") {
                            note = DairyNote(0, result.data?.extras?.get(AddNote.CLE).toString())
                            items.add(note)
                            textAdapter.notifyItemInserted(items.size)

                            val tInsert = Thread {
                                val db = SingletonDatabase.getDatabase(requireContext())
                                db.dairyDao().insert(note)
                            }
                            tInsert.start()
                        }
        }

        // recover and update or delete note in recyclerView (noteRV) at the given position
        val launcherUpdateNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
                val pos = result.data?.extras?.getInt("position")
                val code = result.resultCode.toString()
                val id = result.data?.extras?.getInt("id")

                if (code == "-1" && result.data?.extras?.getString("delete") == "deleteOk") {
                    items.removeAt(pos!!)
                    textAdapter.notifyItemRemoved(pos)
                    textAdapter.notifyItemRangeChanged(pos, binding.notesMyNotes.size)

                    val tDelete = Thread {
                        val db = SingletonDatabase.getDatabase(requireContext())
                        db.dairyDao().deleteNote(id!!)
                        // id always = 0
                    }
                    tDelete.start()
                } else if (code == "-1") {
                    items.add(pos!!, DairyNote(id!!, result.data?.extras?.get(UpdateNote.CLE).toString()))
                    textAdapter.notifyItemChanged(pos)
                    items.removeAt(pos+1)
                    textAdapter.notifyItemRemoved(pos+1)

                    val tUpdate = Thread {
                        val db = SingletonDatabase.getDatabase(requireContext())
                        db.dairyDao().updateNote(result.data?.extras?.get(UpdateNote.CLE).toString(), id)
                    }
                    tUpdate.start()
                }
        }

        // set adapter
        textAdapter = DailyAdapter(requireContext(), items) { id: Int, text: String, pos: Int -> updateNote(root, launcherUpdateNote, id, text, pos) }
        noteRV.layoutManager = LinearLayoutManager(requireContext())
        noteRV.adapter = textAdapter

        // launch activity to add a note (AddNote.kt)
        binding.floatingActionButtonAddNote.setOnClickListener {
            launcherAddNote.launch(Intent(root.context, AddNote::class.java))
        }

        return root
    }

    private fun updateNote (root: View, launcher: ActivityResultLauncher<Intent>, textId: Int, textVal: String, textPos: Int) {
        launcher.launch(Intent(root.context, UpdateNote::class.java)
            .putExtra("id", textId)
            .putExtra("text", textVal)
            .putExtra("position", textPos))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null
    }
}