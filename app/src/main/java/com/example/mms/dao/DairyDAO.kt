package com.example.mms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mms.model.DairyNote

@Dao
interface DairyDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(note: DairyNote)

    @Query("SELECT * FROM notes")
    fun getAllNote(): MutableList<DairyNote>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNote(noteId: Int): DairyNote

    @Query("UPDATE notes SET note = :note WHERE id = :noteId")
    fun updateNote(note: String, noteId: Int)

    @Query("DELETE FROM notes WHERE id = :noteId")
    fun deleteNote(noteId: Int)
}
