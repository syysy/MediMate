package com.example.mms.dao

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Query
import androidx.test.core.app.ApplicationProvider
import com.example.mms.database.inApp.AppDatabase
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.model.medicines.MType
import com.example.mms.model.medicines.Medicine
import org.junit.Assert.assertThrows
import org.junit.Test

class MedicineDAOTest {
    private lateinit var db: AppDatabase
    private lateinit var medicineDAO: MedicineDAO

    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = SingletonDatabase.getDatabase(context)
        medicineDAO = db.medicineDao()
    }

    @Test
    fun testInsert() {
        setUp()
        val medicine = Medicine("Doliprane", 123456789, null, MType(), null, null, null, null, null, null)
        medicineDAO.insert(medicine)
        val result = medicineDAO.getByCIS(123456789)
        assert(result != null)
        medicineDAO.delete(123456789)
    }

    @Test
    fun testInsertFail() {
        setUp()
        val medicine = Medicine("Doliprane", 123456789, null, MType(), null, null, null, null, null, null)
        medicineDAO.insert(medicine)
        assertThrows(SQLiteConstraintException::class.java) {
            medicineDAO.insert(medicine)
        }
        medicineDAO.delete(123456789)
    }

    @Test
    fun testGetByCis() {
        setUp()
        val result = medicineDAO.getByCIS(64793681)
        assert(result is Medicine)
        assert(result?.name == "Doliprane")
    }

    @Test
    fun testGetByCisFail() {
        setUp()
        val result = medicineDAO.getByCIS(123456789)
        assert(result == null)
    }

    @Test
    fun testGetNameByCis() {
        setUp()
        val result = medicineDAO.getNameByCIS(64793681)
        assert(result == "Doliprane")
    }

    @Test
    fun testGetNameByCisFail() {
        setUp()
        val result = medicineDAO.getNameByCIS(123456789)
        assert(result == null)
    }

    @Test
    fun testGetByNameTypeWeight() {
        setUp()
        val result = medicineDAO.getByNameTypeWeight("Doliprane", "Générique", "100 mg")
        assert(result is Medicine)
        assert(result?.code_cis == 64793681L)
    }

    @Test
    fun testGetByNameTypeWeightFail() {
        setUp()
        val result = medicineDAO.getByNameTypeWeight("NoMed", "Générique", "100 mg")
        assert(result == null)
    }
}

@Query("DELETE FROM Medicine WHERE code_cis = :code_cis")
private fun MedicineDAO.delete(code_cis: Long){}