package com.example.mms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mms.model.OneTake

@Dao
interface OneTakeDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(oneTake: OneTake)

    @Query("SELECT * FROM oneTake WHERE taskId = :taskId")
    fun find(taskId: Long): OneTake?
}
