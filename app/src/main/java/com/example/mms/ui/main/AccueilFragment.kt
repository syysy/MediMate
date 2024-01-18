package com.example.mms.ui.main

import android.Manifest
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mms.R
import com.example.mms.Utils.extractMonthAndYearFromDate
import com.example.mms.Utils.getNewCalendarDayList
import com.example.mms.Utils.stringToDate
import com.example.mms.adapter.CalendarAdapter
import com.example.mms.adapter.Interface.CalendarAdapterInterface
import com.example.mms.adapter.Interface.OnItemClickListener
import com.example.mms.adapter.TakesAdapter
import com.example.mms.database.inApp.AppDatabase
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.databinding.FragmentAccueilBinding
import com.example.mms.model.CalendarDay
import com.example.mms.model.ShowableHourWeight
import com.example.mms.model.Task
import com.example.mms.service.TasksService
import com.example.mms.ui.add.AddActivity
import com.example.mms.ui.loader.LoaderActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AccueilFragment : Fragment() {

    private var _binding: FragmentAccueilBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val today = Calendar.getInstance()

    private lateinit var calendarDays: MutableList<CalendarDay>
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var takesAdapter: TakesAdapter
    private lateinit var userMedicines: MutableList<Task>
    private lateinit var items: MutableList<ShowableHourWeight>
    private lateinit var db: AppDatabase
    private lateinit var tasksService: TasksService
    private val selectedDate = Date()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        // We get the database singleton instance
        db = SingletonDatabase.getDatabase(requireContext())

        this.tasksService = TasksService(requireContext())

        // If the user has not allowed the app to send notifications, we ask him
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        lifecycleScope.launch {
            // We get the user connected from the database
            val user = withContext(Dispatchers.IO) {
                db.userDao().getConnectedUser()
            }
            // If the user is not connected, we redirect him to the loader activity
            if (user == null) {
                startActivity(Intent(requireContext(), LoaderActivity::class.java))
            } else {
                // else we set the user data in the view model
                viewModel.setUserData(user)
            }
        }
        _binding = FragmentAccueilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // we get calendarDays from database, those days are filled with tasks of the user connected if there are any
        // calendarDays list is filled with 5 days, the day selected, 1 day before and 3 after, init with day selected as today
        val calendarRecyclerView = binding.calendarRecyclerView
        calendarDays = getNewCalendarDayList(mutableListOf(), today.time, this.requireContext())


        // We set the month and year in the view
        binding.calendarCurrentMonthText.text =
            SimpleDateFormat("MMMM", Locale.FRENCH).format(today.time)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        binding.calendarCurrentYearText.text =
            SimpleDateFormat("yyyy", Locale.FRENCH).format(today.time)

        // We set the adapter for the calendar recycler view
        val screenWith = requireActivity().windowManager.currentWindowMetrics.bounds.width()
        val itemWith = screenWith / calendarDays.size

        calendarAdapter = CalendarAdapter(inflater.context, calendarDays, itemWith)
        calendarRecyclerView.adapter = calendarAdapter
        calendarRecyclerView.layoutManager =
            LinearLayoutManager(inflater.context, LinearLayoutManager.HORIZONTAL, false)

        // We get the takes of the day selected and we set the adapter for the recycler view of the takes
        val medicinesRV = binding.medicRecyclerView
        userMedicines = calendarDays[1].listTasks.toMutableList()

        // We get special objects from the list of takes to show them in the view
        this.items = this.tasksService.createShoawbleHourWeightsFromTasks(userMedicines)

        updateSmiley()

        setMonthAndYear(extractMonthAndYearFromDate(this.selectedDate.toString())!!.first, extractMonthAndYearFromDate(this.selectedDate.toString())!!.second)
        takesAdapter = TakesAdapter(root.context, items, db, this.selectedDate, root) { updateSmiley() }
        medicinesRV.layoutManager = LinearLayoutManager(root.context)
        medicinesRV.adapter = takesAdapter

        // We set the listener for the calendar recycler view
        calendarAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                val clickedDay = calendarDays[position]
                // We get the takes of the day selected and we set the adapter for the recycler view of the takes
                val newCalendarDays = getNewCalendarDayList(
                    calendarDays,
                    stringToDate(clickedDay.date),
                    this@AccueilFragment.requireContext()
                )
                // we have to clear the list and add the new days because we can't change the list of the adapter
                calendarDays.clear()
                calendarDays.addAll(newCalendarDays)
                // we have to toggle the selection of the day selected and notify the adapter
                calendarAdapter.toggleDaySelection(calendarDays.indexOfFirst { it.date == clickedDay.date })
                calendarAdapter.notifyDataSetChanged()

                // Same for the takes
                userMedicines.clear()
                userMedicines.addAll(clickedDay.listTasks)
                items.clear()
                items.addAll(
                    this@AccueilFragment.tasksService.createShoawbleHourWeightsFromTasks(
                        userMedicines
                    )
                )
                takesAdapter.updateCurrentDate(stringToDate(clickedDay.date))
                takesAdapter.notifyDataSetChanged()


                // If the day selected is not today, we show the button to go back to today
                if (clickedDay.date != today.time.toString()) binding.floatingActionButtonBackToday.show()
                else binding.floatingActionButtonBackToday.hide()

                // We update the smiley
                updateSmiley()
            }

        })

        // We set the listener for the calendar adapter
        calendarAdapter.setCalendarAdapterInterface(object : CalendarAdapterInterface {
            override fun onMonthYearChanged(month: String, year: String) {
                setMonthAndYear(month, year)
            }
        })


        // We set the listener for the buttons to change the month
        binding.buttonNextMonth.setOnClickListener {
            updateMonth(1)
        }

        binding.buttonPreviousMonth.setOnClickListener {
            updateMonth(-1)
        }

        binding.floatingActionButtonBackToday.hide()

        // On click on the floating action button, we go back to today, we update the smiley and we hide the button and update adapters
        binding.floatingActionButtonBackToday.setOnClickListener {
            today.time = Date()
            // We get the takes of the day selected and we set the adapter for the recycler view of the takes
            val newCalendarDays =
                getNewCalendarDayList(calendarDays, today.time, this.requireContext())

            calendarDays.clear()
            calendarDays.addAll(newCalendarDays)
            val clickedDay = calendarDays.find { it.date == today.time.toString() }!!
            calendarAdapter.toggleDaySelection(calendarDays.indexOfFirst { it.date == today.time.toString() })
            setMonthAndYear(
                extractMonthAndYearFromDate(today.time.toString())?.first.orEmpty(),
                extractMonthAndYearFromDate(today.time.toString())?.second.orEmpty()
            )
            calendarAdapter.notifyDataSetChanged()

            userMedicines.clear()
            userMedicines.addAll(clickedDay.listTasks)
            items.clear()
            items.addAll(this.tasksService.createShoawbleHourWeightsFromTasks(userMedicines))
            takesAdapter.updateCurrentDate(stringToDate(clickedDay.date))
            takesAdapter.notifyDataSetChanged()
            binding.floatingActionButtonBackToday.hide()

            updateSmiley()
        }

        binding.floatingActionButtonAddMedic.setOnClickListener {
            startActivity(Intent(root.context, AddActivity::class.java))
        }

        return root
    }

    // Function to update the smiley
    private fun updateSmiley() {
        val numberTakesTook = this.tasksService.getNumberOfTaskDoneToday(items)
        val now = Date()

        if (!areDatesOnSameDay(now, selectedDate)) {
            binding.imageView.setImageResource(R.drawable.tres_heureux)
            binding.textHome.text = getString(R.string.vous_aurez_medicament_prendre, numberTakesTook.second.toString())
            return
        }

        // If the user has not taken any medicine today, we show the smiley "very happy" and we show a text to tell him to take his medicine
        if (numberTakesTook.second == 0 || items.isEmpty()) {
            binding.imageView.setImageResource(R.drawable.tres_heureux)
            binding.textHome.text = getString(R.string.rien_prendre_aujourd_hui)
        } else {
            // Else we show the smiley corresponding to the percentage of takes took
            val percent = numberTakesTook.first * 100 / numberTakesTook.second
            if (percent < 25) {
                binding.imageView.setImageResource(R.drawable.en_colere)
            } else if (percent < 50) {
                binding.imageView.setImageResource(R.drawable.tres_triste)
            } else if (percent < 75) {
                binding.imageView.setImageResource(R.drawable.neutre)
            } else {
                binding.imageView.setImageResource(R.drawable.tres_heureux)
            }
            binding.textHome.text =
                getString(R.string.vous_avez_pris_medicaments_aujourd_hui, numberTakesTook.first.toString())
        }
    }

    // Function to update the month -> same idea as before with onClickListeners
    fun updateMonth(numberToAdd: Int) {
        val newCalendar = Calendar.getInstance()
        newCalendar.time = stringToDate(getDaySelected().date)
        newCalendar.add(Calendar.MONTH, numberToAdd)

        val newCalendarDays =
            getNewCalendarDayList(calendarDays, newCalendar.time, this.requireContext())

        calendarDays.clear()
        calendarDays.addAll(newCalendarDays)
        val clickedDay = calendarDays.find { it.date == newCalendar.time.toString() }!!
        calendarAdapter.toggleDaySelection(calendarDays.indexOfFirst { it.date == newCalendar.time.toString() })
        setMonthAndYear(
            extractMonthAndYearFromDate(newCalendar.time.toString())?.first.orEmpty(),
            extractMonthAndYearFromDate(newCalendar.time.toString())?.second.orEmpty()
        )

        userMedicines.clear()
        userMedicines.addAll(clickedDay.listTasks)
        items.clear()
        items.addAll(this.tasksService.createShoawbleHourWeightsFromTasks(userMedicines))
        takesAdapter.updateCurrentDate(stringToDate(clickedDay.date))
        takesAdapter.notifyDataSetChanged()

        calendarAdapter.notifyDataSetChanged()

        if (!areDatesOnSameDay(newCalendar.time, Date()))
            binding.floatingActionButtonBackToday.show()
        else
            binding.floatingActionButtonBackToday.hide()

        updateSmiley()
    }

    fun setMonthAndYear(month: String, year: String) {
        binding.calendarCurrentMonthText.text = month
        binding.calendarCurrentYearText.text = year
    }


    fun areDatesOnSameDay(date1: Date, date2: Date): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate1 = dateFormat.format(date1)
        val formattedDate2 = dateFormat.format(date2)

        return formattedDate1 == formattedDate2
    }

    fun getDaySelected(): CalendarDay {
        return calendarDays.find { it.isSelected }!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
