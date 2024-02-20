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
import com.example.mms.databinding.FragmentDairyBinding
import com.example.mms.model.DairyNote
import com.example.mms.ui.add.AddNote
import com.example.mms.ui.add.ScanLoading

class DairyFragment: Fragment() {

    private var _binding: FragmentDairyBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var textAdapter: DailyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        this._binding = FragmentDairyBinding.inflate(inflater, container, false)
        val root: View = this.binding.root
        val noteRV: RecyclerView = this.binding.notesMyNotes
        val items = ArrayList<DairyNote>()

        // recover and add note in recyclerView (noteRV)
        val launcherAddNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result -> if (result.resultCode.toString() == "-1") {
                        items.add(DairyNote(result.data?.extras?.get(AddNote.CLE).toString()))
                        textAdapter.notifyItemInserted(items.size)
                    }
        }

        // recover and update or delete note in recyclerView (noteRV) at the given position
        val launcherUpdateNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
                val pos = result.data?.extras?.getInt("position")
                val code = result.resultCode.toString()

                if (code == "-1" && result.data?.extras?.getString("delete") == "deleteOk") {
                    items.removeAt(pos!!)
                    textAdapter.notifyItemRemoved(pos)
                    textAdapter.notifyItemRangeChanged(pos, binding.notesMyNotes.size)
                } else if (code == "-1") {
                    items.add(pos!!, DairyNote(result.data?.extras?.get(UpdateNote.CLE).toString()))
                    textAdapter.notifyItemChanged(pos)
                    items.removeAt(pos+1)
                    textAdapter.notifyItemRemoved(pos+1)
                }
        }

        // set adapter
        textAdapter = DailyAdapter(requireContext(), items) { text:String, pos:Int -> updateNote(root, launcherUpdateNote, text, pos) }
        noteRV.layoutManager = LinearLayoutManager(requireContext())
        noteRV.adapter = textAdapter

        // launch activity to add a note (AddNote.kt)
        binding.floatingActionButtonAddNote.setOnClickListener {
            launcherAddNote.launch(Intent(root.context, AddNote::class.java))
        }

        return root
    }

    private fun updateNote (root: View, launcher: ActivityResultLauncher<Intent>, textVal: String, textPos: Int) {
        launcher.launch(Intent(root.context, UpdateNote::class.java)
            .putExtra("text", textVal)
            .putExtra("position", textPos))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null
    }
}