package com.example.mms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mms.model.Cycle
import com.example.mms.model.CycleHourWeight
import com.example.mms.model.Doctor
import com.example.mms.model.Version

@Dao
interface DoctorDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(doctor: Doctor)

    @Query("SELECT * FROM doctor LIMIT 1")
    fun get(): Doctor?
}
