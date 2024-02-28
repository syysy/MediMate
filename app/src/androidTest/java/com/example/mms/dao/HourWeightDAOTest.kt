package com.example.mms.dao

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.test.core.app.ApplicationProvider
import com.example.mms.database.inApp.AppDatabase
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.model.HourWeight
import org.junit.Assert.assertThrows
import org.junit.Test

class HourWeightDAOTest {

    private lateinit var db: AppDatabase
    private lateinit var hourWeightDAO: HourWeightDAO

    fun setUp() {
        print("Setting up")
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = SingletonDatabase.getDatabase(context)
        hourWeightDAO = db.hourWeightDao()
    }

    fun cleanUp() {
        print("Cleaning up")
        while (hourWeightDAO.getLastInserted() != null) {
            hourWeightDAO.delete(hourWeightDAO.getLastInserted()!!)
        }
    }

    @Test
    fun testInsert() {
        setUp()
        val hourWeight = HourWeight(1, "12:00", 70)
        hourWeightDAO.insert(hourWeight)
        val lastInserted = hourWeightDAO.getLastInserted()
        assert(lastInserted != null)
        assert(lastInserted!!.hour == hourWeight.hour)
        assert(lastInserted.weight == hourWeight.weight)
        cleanUp()
    }

    @Test
    fun testInsertFail(){
        setUp()
        val hourWeight = HourWeight(10, "12:00", 70)
        hourWeightDAO.insert(hourWeight)
        assertThrows(SQLiteConstraintException::class.java) {
            hourWeightDAO.insert(hourWeight)
        }
        cleanUp()
    }

    @Test
    fun testDelete() {
        setUp()
        val hourWeight = HourWeight(1, "12:00", 70)
        hourWeightDAO.insert(hourWeight)
        val hourWeightRetrieved = hourWeightDAO.getHourWeight(1)
        assert(hourWeightRetrieved != null)
        hourWeightDAO.delete(hourWeightRetrieved)
        val hourWeightRetrievedAfterDelete = hourWeightDAO.getHourWeight(1)
        assert(hourWeightRetrievedAfterDelete == null)
        cleanUp()
    }
}