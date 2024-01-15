package com.example.mms.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mms.model.User

@Dao
interface UserDAO {
    @Query("SELECT * FROM user")
    fun getAllUsers(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Query("DELETE FROM user")
    fun deleteAllUsers()

    @Query("DELETE FROM user WHERE email = :email")
    fun deleteUser(email: String)

    @Query("UPDATE user SET isConnected = :isConnected WHERE email = :email")
    fun updateIsConnected(isConnected: Boolean, email: String)

    @Query("SELECT * FROM user WHERE isConnected = 1")
    fun getConnectedUser(): User?

    @Query("SELECT * FROM user WHERE email = :email")
    fun getUser(email: String): User?

    @Query("SELECT * FROM user WHERE isLinkedToBiometric = 1")
    fun getBiometricUsers(): List<User>

   @Update
   fun updateUser(user: User)

}
