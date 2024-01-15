package com.example.mms.service

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.mms.Utils.compareLocalDateTimeOnlyDays
import com.example.mms.Utils.dateToLocalDatetime
import com.example.mms.Utils.hourMinuteToString
import com.example.mms.Utils.stringHourMinuteToInt
import com.example.mms.constant.TYPE_PRIS_PONCTUELLE
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.model.Cycle
import com.example.mms.model.CycleHourWeight
import com.example.mms.model.HourWeight
import com.example.mms.model.MedicineStorage
import com.example.mms.model.OneTake
import com.example.mms.model.ShowableHourWeight
import com.example.mms.model.ShowableTakes
import com.example.mms.model.SpecificDaysHourWeight
import com.example.mms.model.Takes
import com.example.mms.model.Task
import com.example.mms.model.medicines.MType
import java.time.LocalDateTime
import java.util.Date

class TasksService(context: Context) {
    private var db = SingletonDatabase.getDatabase(context)

    fun getCurrentUserTasks(): List<Task> {
        val currentUserId = this.db.userDao().getConnectedUser()?.email ?: return listOf()
        return this.db.taskDao().getUserTasks(currentUserId)
    }

    fun storeTask(task: Task) {
        val now = LocalDateTime.now()
        task.createdAt = now
        task.updatedAt = now
        this.db.taskDao().insert(task)
    }

    fun storeCycle(cycle: Cycle) {
        // Cycle
        this.db.cycleDao().insert(cycle)
        val cycleId = this.db.cycleDao().getLastInserted()?.id ?: 0

        val task = this.db.taskDao().getTask(cycle.taskId)
        val shouldTake = shouldTakeThisCycleAt(task, cycle, LocalDateTime.now())

        cycle.hourWeights.forEach {
            // HourWeight
            this.db.hourWeightDao().insert(it)
            val hourWeightId = this.db.hourWeightDao().getLastInserted()?.id ?: 0

            // CycleHourWeight
            val cycleHourWeight = CycleHourWeight(
                cycleId,
                hourWeightId
            )
            if (shouldTake) {
                this.db.takesDao().insert(
                    Takes(
                        hourWeightId,
                        LocalDateTime.now().toLocalDate().atStartOfDay(),
                        LocalDateTime.now(),
                        false
                    )
                )
            }

            this.db.cycleDao().insert(cycleHourWeight)
        }
    }

    fun storeSpecificDays(specificDays: SpecificDaysHourWeight) {
        this.db.hourWeightDao().insert(specificDays.hourWeight!!)
        val hourWeightId = this.db.hourWeightDao().getLastInserted()?.id ?: 0

        specificDays.hourWeightId = hourWeightId
        this.db.specificDaysDao().insert(specificDays)
    }

    fun storeOneTake(medicineCIS: Long, weight: Int) {
        val now = LocalDateTime.now()
        // Insert a task
        val task = Task(
            0,
            TYPE_PRIS_PONCTUELLE,
            now,
            now,
            now,
            now,
            medicineCIS,
            this.db.userDao().getConnectedUser()?.email!!
        )
        this.storeTask(task)
        val taskId = this.db.taskDao().getLastInserted()?.id ?: 0

        // Insert a HourWeight
        val hourWeight = HourWeight(
            0,
            "${now.hour}:${now.minute}",
            weight
        )
        this.db.hourWeightDao().insert(hourWeight)
        val hourWeightId = this.db.hourWeightDao().getLastInserted()?.id ?: 0

        // Insert a Takes
        val takes = Takes(
            hourWeightId,
            now.toLocalDate().atStartOfDay(),
            now,
            true
        )
        this.db.takesDao().insert(takes)

        // Insert a oneTake
        val oneTake = OneTake(
            taskId,
            hourWeightId
        )
        this.db.oneTakeDao().insert(oneTake)
    }

    fun getTasksAt(userId: String, date: Date): MutableList<Task> {
        val allTasks = this.db.taskDao().getUserTasks(userId)

        val tasks = mutableListOf<Task>()

        allTasks.forEach {
            val task = this.getByIdAt(it.id, dateToLocalDatetime(date))

            if (!task.isEmpty()) {
                tasks.add(task)
            }
        }

        return tasks
    }

    fun getTasksFilled(oneTakeTasks: Boolean = false) : MutableList<Task> {
        val allTasks = this.getCurrentUserTasks()

        val tasks = mutableListOf<Task>()

        allTasks.forEach {
            val task = getTaskFilled(it, oneTakeTasks)
            if (task != null) {
                tasks.add(task)
            }
        }

        return tasks
    }

    fun getTaskFilled(
        it: Task,
        oneTakeTasks: Boolean = false
    ) : Task? {
        val task = it
        val cycle = this.db.taskDao().getTaskCycle(it.id)
        val specificDays = this.db.taskDao().getTaskSpecificDays(it.id)

        if (cycle != null) {
            val listHourWeight = this.db.cycleDao().getCycleHourWeight(cycle.id)

            cycle.hourWeights = listHourWeight.map {
                this.db.hourWeightDao().getHourWeight(it.hourWeightId)
            }.toMutableList()

            task.cycle = cycle
        }

        for (specificDay in specificDays) {
            specificDay.hourWeight =
                this.db.hourWeightDao().getHourWeight(specificDay.hourWeightId)
            task.specificDays.add(specificDay)
        }

        if (oneTakeTasks) {
            val oneTake = this.db.oneTakeDao().find(it.id)
            if (oneTake != null) {
                task.oneTakeHourWeight = this.db.hourWeightDao().getHourWeight(oneTake.hourWeightId)
            }
        }

        return if (task.isEmpty()) null else task

    }

    fun getByIdAt(taskId: Long, date: LocalDateTime): Task {
        val task = this.db.taskDao().getTask(taskId)
        val cycle = this.db.taskDao().getTaskCycle(taskId)
        val specificDays = this.db.taskDao().getTaskSpecificDays(taskId)
        val oneTake = this.db.oneTakeDao().find(taskId)

        if (cycle != null) {
            if (this.shouldTakeThisCycleAt(task, cycle, date)) {
                val listHourWeight = this.db.cycleDao().getCycleHourWeight(cycle.id)

                cycle.hourWeights = listHourWeight.map {
                    this.db.hourWeightDao().getHourWeight(it.hourWeightId)
                }.toMutableList()

                task.cycle = cycle
            }
        }

        for (specificDay in specificDays) {
            if (this.shouldTakeThisSpecificDayAt(task, specificDay, date)) {
                specificDay.hourWeight =
                    this.db.hourWeightDao().getHourWeight(specificDay.hourWeightId)
                task.specificDays.add(specificDay)
            }
        }

        if (oneTake != null && task.createdAt.toLocalDate() == date.toLocalDate()) {
            task.oneTakeHourWeight = this.db.hourWeightDao().getHourWeight(oneTake.hourWeightId)
        }

        return task
    }

    fun getNumberOfTaskDoneToday(listSHW: MutableList<ShowableHourWeight>) : Pair<Int,Int> {
        var numberTakes = 0
        var totalTakes = 0
        for (shw in listSHW) {
            var takes : Takes? = null
            val tt = Thread {
                takes = this.db.takesDao().getTakes(shw.hourWeight.id, LocalDateTime.now().toLocalDate().atStartOfDay())
            }
            tt.start()
            tt.join()
            if (takes != null) {
                totalTakes++
                if (takes!!.isDone) {
                    numberTakes++
                }
            }
        }
        return Pair(numberTakes,totalTakes)
    }


    fun shouldTakeThisCycleAt(task: Task, cycle: Cycle, date: LocalDateTime): Boolean {
        val dateStartDay = date.toLocalDate().atStartOfDay()
        var pointerDate = task.updatedAt.toLocalDate().atStartOfDay()
        var lastPointerDate = pointerDate

        // La tache a été créée après le jour demandé, on ne doit pas la prendre
        if (task.createdAt.toLocalDate().atStartOfDay() > dateStartDay ||
            task.startDate.toLocalDate().atStartOfDay() > dateStartDay ||
            task.endDate.toLocalDate().atStartOfDay() < dateStartDay) {
            return false
        }

        // La tache a été créée le jour même, on doit la prendre
        if (compareLocalDateTimeOnlyDays(pointerDate, dateStartDay)) {
            return true
        }

        if (pointerDate < dateStartDay) { // apres
            while (pointerDate.toLocalDate().atStartOfDay() < dateStartDay) {
                pointerDate = pointerDate.plusHours(cycle.hoursOfTreatment.toLong())

                if (lastPointerDate <= dateStartDay && dateStartDay < pointerDate) {
                    return true
                }

                pointerDate = pointerDate.plusHours(cycle.hoursOfRest.toLong())
                lastPointerDate = pointerDate
            }
        } else { // avant
            while (pointerDate.toLocalDate().atStartOfDay() < dateStartDay) {
                pointerDate = pointerDate.minusHours(cycle.hoursOfTreatment.toLong())

                if (pointerDate <= dateStartDay && dateStartDay < lastPointerDate) {
                    return true
                }

                pointerDate = pointerDate.minusHours(cycle.hoursOfRest.toLong())
                lastPointerDate = pointerDate
            }
        }

        return compareLocalDateTimeOnlyDays(pointerDate, dateStartDay)
    }


    fun shouldTakeThisSpecificDayAt(
        task: Task,
        specificDay: SpecificDaysHourWeight,
        date: LocalDateTime
    ): Boolean {
        return date.dayOfWeek.value == specificDay.day + 1 &&
                task.createdAt.toLocalDate().atStartOfDay() < date.toLocalDate().atStartOfDay()
    }

    fun getHourNextCycle(cycle: Cycle): Pair<Int, Int> {
        for (hW in cycle.hourWeights) {
            val hour = hW.hour
            val splited = hour.split(":")
            val hourInt = splited[0].toInt()
            val minuteInt = splited[1].toInt()
            if (cycle.currentHourInTreatment < hourInt || (cycle.currentHourInTreatment == hourInt && cycle.currentHourInTreatment < minuteInt)) {
                return Pair(hourInt, minuteInt)
            }
        }
        return Pair(0, 0)
    }


    fun getNextTakeDate(task: Task): LocalDateTime {

        val date = LocalDateTime.now()
        val hourCycle = getHourNextCycle(task.cycle)
        var nextDate = date.withHour(hourCycle.first).withMinute(hourCycle.second)

        if (shouldTakeThisCycleAt(task, task.cycle, nextDate)) {
            return nextDate
        }

        while (!shouldTakeThisCycleAt(task, task.cycle, nextDate)) {
            nextDate = nextDate.plusDays(1)
        }

        return nextDate
    }

    fun getOrCreateTakes(hourWeightId: Int, date: LocalDateTime): Takes {
        var takes = this.db.takesDao().getTakes(hourWeightId, date.toLocalDate().atStartOfDay())

        if (takes == null) {
            this.db.takesDao()
                .insert(Takes(hourWeightId, date.toLocalDate().atStartOfDay(), date, false))
            takes = this.db.takesDao().getTakes(hourWeightId, date.toLocalDate().atStartOfDay())
        }

        return takes!!
    }

    fun createShoawbleHourWeightsFromTasks(tasks: List<Task>): MutableList<ShowableHourWeight> {
        val items = mutableListOf<ShowableHourWeight>()

        for (task in tasks) {
            var medicineName = ""
            var medicineType = MType()
            var medicineStorage: MedicineStorage? = null
            val thread = Thread {
                val medicine = this.db.medicineDao().getByCIS(task.medicineCIS)
                medicineName = medicine!!.name
                medicineType = medicine.type
                medicineStorage = this.db.medicineStorageDao().getMedicineStorageByMedicineId(medicine.code_cis)
            }
            thread.start()
            thread.join()

            // Get all the hour weights for each task
            val cycleHourWeights = task.cycle.hourWeights
            val specificDaysHourWeights = mutableListOf<HourWeight>()
            task.specificDays.forEach {
                if (it.hourWeight != null) {
                    specificDaysHourWeights.add(it.hourWeight!!)
                }
            }
            val hourWeights = (cycleHourWeights + specificDaysHourWeights).toMutableList()

            if (task.oneTakeHourWeight != null) {
                hourWeights.add(task.oneTakeHourWeight!!)
            }

            // Add the ShowableHourWeight to the list
            for (hourWeight in hourWeights) {
                items.add(
                    ShowableHourWeight(
                        medicineName,
                        medicineType,
                        task,
                        hourWeight,
                        medicineStorage
                    )
                )
            }
        }

        items.sortBy { it.hourWeight.hour }

        return items
    }

    fun generateHourWeightForInterval(interval: Int, beginHour: Pair<Int, Int>, endHour: Pair<Int, Int>, weight: Int): MutableList<HourWeight> {
        val hourWeights = mutableListOf<HourWeight>()
        val now = LocalDateTime.now()
        var date = LocalDateTime.now()

        date = date
            .withHour(beginHour.first)
            .withMinute(beginHour.second)
            .withSecond(0)
            .withNano(0)

        while (date.dayOfMonth == now.dayOfMonth &&
               date.monthValue == now.monthValue &&
                date.year == now.year &&
            (date.hour < endHour.first || (date.hour == endHour.first && date.minute <= endHour.second))) {
            hourWeights.add(HourWeight(0, hourMinuteToString(date.hour, date.minute), weight))
            date = date.plusHours(interval.toLong())
        }

        return hourWeights
    }

    fun getAllHourWeightFromTask(task: Task): MutableList<HourWeight> {
        val hourWeights = mutableListOf<HourWeight>()

        if (task.cycle != null) {
            hourWeights.addAll(task.cycle.hourWeights)
        }

        for (specificDay in task.specificDays) {
            if (specificDay.hourWeight != null) {
                hourWeights.add(specificDay.hourWeight!!)
            }
        }

        return hourWeights
    }

    fun createOrGetTodaysSwHourWeight(userId: String): MutableList<ShowableHourWeight> {
        // get today's tasks
        val tasks = this.getTasksAt(userId, Date())

        // get all the hour weights for the day
        val hourWeights = this.createShoawbleHourWeightsFromTasks(tasks)

        // create a Takes for each hour weight
        val now = LocalDateTime.now()
        for (hourWeight in hourWeights) {
            this.getOrCreateTakes(hourWeight.hourWeight.id, now)
        }

        return hourWeights
    }

    fun createOrGetOneTodaysSwHourWeight(task: Task): MutableList<ShowableHourWeight> {
        return this.createShoawbleHourWeightsFromTasks(listOf(task))
    }

    fun getTodaysHourWeightsForAllUsers(): MutableList<ShowableHourWeight> {
        val allShowableHourWeights = mutableListOf<ShowableHourWeight>()
        val thread = Thread {
            val users = this.db.userDao().getAllUsers()

            for (user in users) {
                val showableHourWeights = this.createOrGetTodaysSwHourWeight(user.email)
                allShowableHourWeights.addAll(showableHourWeights)
            }
        }
        thread.start()
        thread.join()

        return allShowableHourWeights
    }

    fun removeAlreadyPassedHourWeights(hourWeights: MutableList<ShowableHourWeight>): MutableList<ShowableHourWeight> {
        val now = LocalDateTime.now()
        val newHourWeights = mutableListOf<ShowableHourWeight>()

        for (hourWeight in hourWeights) {
            val hourMinute = stringHourMinuteToInt(hourWeight.hourWeight.hour)

            if (now.hour < hourMinute.first || (now.hour == hourMinute.first && now.minute < hourMinute.second)) {
                newHourWeights.add(hourWeight)
            }
        }

        return newHourWeights
    }

    fun getUserStats(): Pair<Int,Int> {
        val tasks = this.getTasksFilled()
        var totalTakes = 0
        var totalDone = 0
        for (task in tasks) {
            for (hourWeight in task.cycle.hourWeights) {
                val takes = this.db.takesDao().getAllFromHourWeightId(hourWeight.id)
                if (takes.isNotEmpty()) {
                    totalTakes += takes.size
                    takes.forEach { it.isDone.let { it1 -> if (it1) totalDone++ } }
                }
            }
            for (specificDay in task.specificDays) {
                val takes = this.db.takesDao().getAllFromHourWeightId(specificDay.hourWeightId)
                if (takes.isNotEmpty()) {
                    totalTakes += takes.size
                    takes.forEach { it.isDone.let { it1 -> if (it1) totalDone++ } }
                }
            }

        }
        return if (totalTakes == 0) Pair(0,0) else Pair(totalDone,(totalDone * 100) / totalTakes)
    }

    fun getCurrentUserAllShowableTakes(): MutableList<ShowableTakes> {
        val shTakes = mutableListOf<ShowableTakes>()
        val thread = Thread {
            val tasks = this.getTasksFilled(true)
            val showableHourWeight = this.createShoawbleHourWeightsFromTasks(tasks)

            for (hourWeight in showableHourWeight) {
                val takes = this.db.takesDao().getAllFromHourWeightId(hourWeight.hourWeight.id)

                for (take in takes) {
                    if (take.isDone) {
                        shTakes.add(
                            ShowableTakes(
                                hourWeight.medicineName,
                                hourWeight.medicineType.weight ?: "",
                                take.takeAt,
                                hourWeight.hourWeight.weight
                            )
                        )
                    }
                }
            }
        }
        thread.start()
        thread.join()

        return shTakes
    }
}
