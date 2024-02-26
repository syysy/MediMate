package com.example.mms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mms.model.Cycle
import com.example.mms.model.CycleHourWeight
import com.example.mms.model.SideEffects

@Dao
interface SideEffectsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertManySideEffects(sideEffects: List<SideEffects>)

    @Query("SELECT Count(*) FROM side_effects")
    fun getNbElements(): Int
}
