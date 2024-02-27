package com.example.mms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mms.model.MedicineStorage

@Dao
interface MedicineStorageDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(medicineStorage: MedicineStorage)
    @Query("SELECT * FROM medicineStorage WHERE medicineId = :medicineId")
    fun getMedicineStorageByMedicineId(medicineId: Long): MedicineStorage?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(medicineStorage: MedicineStorage)

}

