package com.example.mms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mms.model.Takes
import java.time.LocalDateTime

@Dao
interface TakesDAO {

    @Query("SELECT * FROM takes")
    fun getAll(): MutableList<Takes>

    @Query("SELECT * FROM takes WHERE hourWeightId = :hourWeightId AND date = :date")
    fun getTakes(hourWeightId: Int, date: LocalDateTime): Takes?

    @Query("SELECT * FROM takes WHERE hourWeightId = :hourWeightId")
    fun getAllFromHourWeightId(hourWeightId: Int): MutableList<Takes>

    @Query("UPDATE takes SET isDone = :isDone, takeAt = :takeAt WHERE hourWeightId = :hourWeightId AND date = :date")
    fun updateIsDone(isDone: Boolean, hourWeightId: Int, date: LocalDateTime, takeAt: LocalDateTime)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(takes: Takes)
}
