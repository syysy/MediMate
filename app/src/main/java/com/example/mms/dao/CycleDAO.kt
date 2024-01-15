package com.example.mms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mms.model.Cycle
import com.example.mms.model.CycleHourWeight

@Dao
interface CycleDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(cycle: Cycle)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(cycleHourWeight: CycleHourWeight)

    @Query("SELECT * FROM cycle WHERE taskId = :taskId")
    fun getCycle(taskId: Long): Cycle

    @Query("SELECT * FROM cycle WHERE id = (SELECT MAX(id) FROM cycle)")
    fun getLastInserted(): Cycle?

    @Query("SELECT * FROM cycleHourWeight WHERE cycleId = :cycleId")
    fun getCycleHourWeight(cycleId: Int): List<CycleHourWeight>
}
