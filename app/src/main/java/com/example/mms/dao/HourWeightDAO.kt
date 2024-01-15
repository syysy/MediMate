package com.example.mms.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mms.model.HourWeight

@Dao
interface HourWeightDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(hourWeight: HourWeight)

    @Query("SELECT * FROM hourWeight WHERE id = (SELECT MAX(id) FROM hourWeight)")
    fun getLastInserted(): HourWeight?

    @Query("SELECT * FROM hourWeight WHERE id = :id")
    fun getHourWeight(id: Int): HourWeight

    @Delete
    fun delete(hourWeight: HourWeight)
}
