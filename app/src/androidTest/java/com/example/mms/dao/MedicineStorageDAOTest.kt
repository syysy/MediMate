package com.example.mms.dao

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Query
import androidx.test.core.app.ApplicationProvider
import com.example.mms.database.inApp.AppDatabase
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.model.MedicineStorage
import org.junit.Assert.assertThrows
import org.junit.Test

class MedicineStorageDAOTest {
    private lateinit var db: AppDatabase
    private lateinit var medicineStorageDAO: MedicineStorageDAO

    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = SingletonDatabase.getDatabase(context)
        medicineStorageDAO = db.medicineStorageDao()
    }

    @Test
    fun testInsert() {
        setUp()
        val medicineStorage = MedicineStorage(64793681, 10, 1)
        medicineStorageDAO.insert(medicineStorage)
        val medicineStorageFromDB = medicineStorageDAO.getMedicineStorageByMedicineId(64793681)
        assert(medicineStorageFromDB != null)
        assert(medicineStorageFromDB!!.medicineId == 64793681L)
        assert(medicineStorageFromDB.storage == 10)
        assert(medicineStorageFromDB.alertValue == 1)
        medicineStorageDAO.delete(64793681)
    }

    @Test
    fun testInsertError() {
        val medicineStorage = MedicineStorage(1, 10, 1)
        assertThrows(UninitializedPropertyAccessException::class.java) {
            medicineStorageDAO.insert(medicineStorage)
        }
    }

    @Test
    fun testGetMedicineStorageByMedicineIdFail() {
        setUp()
        val medicineStorage = medicineStorageDAO.getMedicineStorageByMedicineId(123456789)
        assert(medicineStorage == null)
    }

}

@Query("DELETE FROM MedicineStorage WHERE medicineId = :code_cis")
private fun MedicineStorageDAO.delete(code_cis: Long){}