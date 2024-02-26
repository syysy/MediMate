package com.example.mms.dao

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.mms.constant.TYPE_PRIS_2JOURNALIERE
import com.example.mms.database.inApp.AppDatabase
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.model.Cycle
import com.example.mms.model.Task
import com.example.mms.model.User
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class CycleDAOTest {
    private lateinit var cycleDAO: CycleDAO
    private lateinit var taskDAO: TaskDAO
    private lateinit var userDAO: UserDAO
    private lateinit var medicineDAO: MedicineDAO
    private lateinit var db: AppDatabase

    fun setUp() {
        print("Setting up")
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = SingletonDatabase.getDatabase(context)
        cycleDAO = db.cycleDao()
        taskDAO = db.taskDao()
        userDAO = db.userDao()
        medicineDAO = db.medicineDao()
        val medicine = medicineDAO.getByCIS(66057393)
        val task = Task(1, TYPE_PRIS_2JOURNALIERE, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), medicine!!.code_cis, "1")
        val task2 = Task(2, TYPE_PRIS_2JOURNALIERE, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), medicine.code_cis, "1")
        val user = User("1", "1", "1", "1", "1", 1, 1, false, "1", "1","1", "1", false)
        userDAO.insertUser(user)
        taskDAO.insert(task)
        taskDAO.insert(task2)
    }

    fun tearDown() {
        print("Tearing down")
        taskDAO.deleteById(1)
        userDAO.deleteUser("1")
    }

    @Test
    fun testInsertChecked() {
        setUp()
        val cycle = Cycle(1, 1, 1, 1, 1)
        cycleDAO.insert(cycle)
        val lastInserted = cycleDAO.getLastInserted()
        assert(lastInserted != null)
        assert(lastInserted!!.id == 1)
        assert(lastInserted.taskId == 1L)
        assert(lastInserted.hoursOfTreatment == 1)
        assert(lastInserted.currentHourInTreatment == 1)
        assert(lastInserted.hoursOfRest == 1)
        tearDown()
    }

    @Test
    fun testInsertFail() {
        setUp()
        val cycleInitial = Cycle(1, 1, 1, 1, 1)
        val cycleDoublon = Cycle(1, 1, 1, 1, 1)
        cycleDAO.insert(cycleInitial)
        assertThrows(SQLiteConstraintException::class.java) {
            cycleDAO.insert(cycleDoublon)
        }
        tearDown()
    }

    @Test
    fun testGetCycle() {
        setUp()
        val cycle = Cycle(1, 1, 1, 1, 1)
        cycleDAO.insert(cycle)
        val retrievedCycle = cycleDAO.getCycle(1)
        assert(retrievedCycle.id == 1)
        assert(retrievedCycle.taskId == 1L)
        assert(retrievedCycle.hoursOfTreatment == 1)
        assert(retrievedCycle.currentHourInTreatment == 1)
        assert(retrievedCycle.hoursOfRest == 1)
        tearDown()
    }

    @Test
    fun testGetCycleUnexisting() {
        setUp()
        val retrievedCycle = cycleDAO.getCycle(1)
        assert(retrievedCycle==null)
        tearDown()
    }

    @Test
    fun testGetLastInserted(){
        setUp()
        val cycle1 = Cycle(1, 1, 1, 1, 1)
        val cycle2 = Cycle(2, 2, 2, 2, 2)
        cycleDAO.insert(cycle1)
        cycleDAO.insert(cycle2)
        val lastInserted = cycleDAO.getLastInserted()
        assert(lastInserted != null)
        assert(lastInserted!!.id == 2)
        assert(lastInserted.taskId == 2L)
        assert(lastInserted.hoursOfTreatment == 2)
        assert(lastInserted.currentHourInTreatment == 2)
        assert(lastInserted.hoursOfRest == 2)
        tearDown()
    }

    @Test
    fun testGetLastInsertedUnexisting(){
        setUp()
        val lastInserted = cycleDAO.getLastInserted()
        assert(lastInserted == null)
        tearDown()
    }

    @Test
    fun testGetLastInsertedOneCycle(){
        setUp()
        val cycle = Cycle(1, 1, 1, 1, 1)
        cycleDAO.insert(cycle)
        val lastInserted = cycleDAO.getLastInserted()
        assert(lastInserted != null)
        assert(lastInserted!!.id == 1)
        assert(lastInserted.taskId == 1L)
        assert(lastInserted.hoursOfTreatment == 1)
        assert(lastInserted.currentHourInTreatment == 1)
        assert(lastInserted.hoursOfRest == 1)
        tearDown()
    }
}