package com.example.mms.service

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.example.mms.constant.TYPE_PRIS_2JOURNALIERE
import com.example.mms.dao.TakesDAO
import com.example.mms.dao.TaskDAO
import com.example.mms.dao.UserDAO
import com.example.mms.database.inApp.AppDatabase
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.model.HourWeight
import com.example.mms.model.ShowableHourWeight
import com.example.mms.model.Takes
import com.example.mms.model.Task
import com.example.mms.model.User
import com.example.mms.model.medicines.MType
import org.junit.Test
import java.time.LocalDateTime

class GetNumberOfTasksDoneTodayTests {
    private lateinit var db: AppDatabase
    private lateinit var taskDAO: TaskDAO
    private lateinit var userDAO: UserDAO
    private lateinit var takesDAO: TakesDAO
    private lateinit var tasksService: TasksService
    private var hourWeightId1 : Int = 0
    private var hourWeightId2 : Int = 0
    private var hourWeightId3 : Int = 0


    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        tasksService = TasksService(context)
        db = SingletonDatabase.getDatabase(context)
        taskDAO = db.taskDao()
        userDAO = db.userDao()
        takesDAO = db.takesDao()
        val user = User("Doe", "John", "john.doe@mail.com", "01/01/2000", "M", 80, 180, false, "1234", "", "", "", false)
        userDAO.insertUser(user)
    }

    private fun getTask1() = Task(1, "oneTake", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), 64793681, "john.doe@mail.com")
    private fun getTask2() = Task(2, "oneTake", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), 64793681, "john.doe@mail.com")
    private fun getTask3() = Task(3, "oneTake", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), 64793681, "john.doe@mail.com")

    private fun getHourWeight1() = HourWeight(0, "1", 1)
    private fun getHourWeight2() = HourWeight(0, "2", 1)
    private fun getHourWeight3() = HourWeight(0, "3", 1)

    private fun getTakesTaken1() = Takes(db.hourWeightDao().getLastInserted()!!.id, LocalDateTime.now(), LocalDateTime.now(), true)
    private fun getTakesTaken2() = Takes(db.hourWeightDao().getLastInserted()!!.id, LocalDateTime.now(), LocalDateTime.now(), true)
    private fun getTakeNotTaken() = Takes(db.hourWeightDao().getLastInserted()!!.id, LocalDateTime.now(), LocalDateTime.now(), false)

    private fun getShowableHourWeight1() = ShowableHourWeight("oneTake", MType("solution buvable","poudre pour solution buvable en sachet-dose","100 mg"), getTask1(), db.hourWeightDao().getHourWeight(hourWeightId1), null)
    private fun getShowableHourWeight2() = ShowableHourWeight("oneTake", MType("solution buvable","poudre pour solution buvable en sachet-dose","100 mg"), getTask2(), db.hourWeightDao().getHourWeight(hourWeightId2), null)
    private fun getShowableHourWeight3() = ShowableHourWeight("oneTake", MType("solution buvable","poudre pour solution buvable en sachet-dose","100 mg"), getTask3(), db.hourWeightDao().getHourWeight(hourWeightId3), null)

    private fun insertTask(){
        taskDAO.insert(getTask1())
        taskDAO.insert(getTask2())
        taskDAO.insert(getTask3())
    }

    private fun insertHourWeightAndTakes(){
        db.hourWeightDao().insert(getHourWeight1())
        takesDAO.insert(getTakesTaken1())
        hourWeightId1 = db.hourWeightDao().getLastInserted()!!.id
        db.hourWeightDao().insert(getHourWeight2())
        takesDAO.insert(getTakesTaken2())
        hourWeightId2 = db.hourWeightDao().getLastInserted()!!.id
        db.hourWeightDao().insert(getHourWeight3())
        takesDAO.insert(getTakeNotTaken())
        hourWeightId3 = db.hourWeightDao().getLastInserted()!!.id
    }


    private fun clearDatabaseOfTests(){
        taskDAO.delete(getTask1())
        taskDAO.delete(getTask2())
        taskDAO.delete(getTask3())
        db.hourWeightDao().delete(getHourWeight1())
        db.hourWeightDao().delete(getHourWeight2())
        db.hourWeightDao().delete(getHourWeight3())
        userDAO.deleteUser("john.doe@mail.com")
    }

    @Test
    fun testGetNumberOfTasksDoneTodayNoTasks() {
        setUp()
        val result = tasksService.getNumberOfTaskDoneToday(mutableListOf())
        assert(result.first == 0)
        assert(result.second == 0)
        userDAO.deleteUser("john.doe@mail.com")
    }

    /*@Test
    fun testGetNumberOfTaskDoneToday3Tasks2Done() {
        setUp()
        insertTask()
        insertHourWeightAndTakes()
        val result = tasksService.getNumberOfTaskDoneToday(mutableListOf(getShowableHourWeight1(),getShowableHourWeight2(),getShowableHourWeight3()))
        Log.d("testGetNumberOfTaskDoneToday3Tasks", result.toString())
        assert(result.first == 2)
        assert(result.second == 3)
        clearDatabaseOfTests()
    }*/
}
