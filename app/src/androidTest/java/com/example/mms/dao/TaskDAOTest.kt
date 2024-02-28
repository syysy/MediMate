package com.example.mms.dao

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.test.core.app.ApplicationProvider
import com.example.mms.constant.TYPE_PRIS_2JOURNALIERE
import com.example.mms.database.inApp.AppDatabase
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.model.HourWeight
import com.example.mms.model.Task
import com.example.mms.model.User
import com.example.mms.model.medicines.Medicine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDateTime

class TaskDAOTest {
    private lateinit var db: AppDatabase
    private lateinit var taskDAO: TaskDAO
    private lateinit var userDAO: UserDAO
    private lateinit var medicineDAO: MedicineDAO
    private lateinit var medicine: Medicine


    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = SingletonDatabase.getDatabase(context)
        taskDAO = db.taskDao()
        userDAO = db.userDao()
        medicineDAO = db.medicineDao()
        medicine = medicineDAO.getByCIS(66057393)!!
        val user = User("1", "1", "1", "1", "1", 1, 1, false, "1", "1","1", "1", false)
        userDAO.insertUser(user)
    }

    @Test
    fun testInsert() {
        setUp()
        val task = Task(1, TYPE_PRIS_2JOURNALIERE, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), medicine.code_cis, "1")
        taskDAO.insert(task)
        val found = taskDAO.getTask(1)
        assertEquals(task.id, found.id)
        assertEquals(task.type, found.type)
        taskDAO.deleteById(1)
    }

    @Test
    fun testInsertFail() {
        setUp()
        val task = Task(1, TYPE_PRIS_2JOURNALIERE, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), medicine.code_cis, "1")
        taskDAO.insert(task)
        assertThrows(SQLiteConstraintException::class.java) {
            taskDAO.insert(task)
        }
    }

    @Test
    fun testGetUsersTasks() {
        setUp()
        val task = Task(1, TYPE_PRIS_2JOURNALIERE, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), medicine.code_cis, "1")
        taskDAO.insert(task)
        val found = taskDAO.getUserTasks("1")
        assertEquals(1, found.size)
        taskDAO.deleteById(1)
    }

    @Test
    fun testGetUsersTasksNoTask() {
        setUp()
        val found = taskDAO.getUserTasks("1")
        assertEquals(0, found.size)
    }
}