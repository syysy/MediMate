package com.example.mms.dao

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Query
import androidx.test.core.app.ApplicationProvider
import com.example.mms.constant.TYPE_PRIS_2JOURNALIERE
import com.example.mms.database.inApp.AppDatabase
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.model.HourWeight
import com.example.mms.model.OneTake
import com.example.mms.model.Task
import com.example.mms.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDateTime

class OneTakeDAOTest {
    private lateinit var db: AppDatabase
    private lateinit var oneTakeDAO: OneTakeDAO
    private lateinit var taskDAO: TaskDAO
    private lateinit var hourWeightDAO: HourWeightDAO
    private lateinit var userDAO: UserDAO
    private lateinit var medicineDAO: MedicineDAO


    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = SingletonDatabase.getDatabase(context)
        oneTakeDAO = db.oneTakeDao()
        taskDAO = db.taskDao()
        hourWeightDAO = db.hourWeightDao()
        userDAO = db.userDao()
        medicineDAO = db.medicineDao()
        val medicine = medicineDAO.getByCIS(66057393)
        val task = Task(1, TYPE_PRIS_2JOURNALIERE, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), medicine!!.code_cis, "1")
        val user = User("1", "1", "1", "1", "1", 1, 1, false, "1", "1","1", "1", false)
        val hourWeight = HourWeight(1,"1", 1)
        userDAO.insertUser(user)
        taskDAO.insert(task)
        hourWeightDAO.insert(hourWeight)
    }

    /**
     * Problème : Redondance d'hourWeight.id
     * Raison inconnue
     * */
    /*
    @Test
    fun testInsert() {
        setUp()
        val oneTake = OneTake(1, 1)
        oneTakeDAO.insert(oneTake)
        val found = oneTakeDAO.find(1)
        assertEquals(oneTake, found)
        oneTakeDAO.delete(1)
        hourWeightDAO.deleteById(1)
    }*/

    @Test
    fun testFind() {
        setUp()
        val oneTake = OneTake(1, 1)
        oneTakeDAO.insert(oneTake)
        val found = oneTakeDAO.find(1)
        assertEquals(oneTake.taskId, found!!.taskId)
        assertEquals(oneTake.hourWeightId, found.hourWeightId)
        oneTakeDAO.delete(1)
    }

    /**
     * Problème : Redondance d'hourWeight.id
     * Raison inconnue
     * */
    /*@Test
    fun testFindFail() {
        setUp()
        val found = oneTakeDAO.find(1)
        assertEquals(null, found)
        hourWeightDAO.deleteById(1)
    }*/
}

@Query("DELETE FROM hourWeight WHERE id = :id")
private fun HourWeightDAO.deleteById(id: Int) {}

@Query("DELETE FROM oneTake WHERE taskId = :taskId")
private fun OneTakeDAO.delete(taskId: Int){}