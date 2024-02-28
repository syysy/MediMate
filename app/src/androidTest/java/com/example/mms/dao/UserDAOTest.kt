package com.example.mms.dao

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.test.core.app.ApplicationProvider
import com.example.mms.database.inApp.AppDatabase
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.model.User
import org.junit.Assert.assertThrows
import org.junit.Test

class UserDAOTest {
    private lateinit var db: AppDatabase
    private lateinit var userDAO: UserDAO

    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = SingletonDatabase.getDatabase(context)
        userDAO = db.userDao()
    }

    @Test
    fun testInsertUser() {
        setUp()
        val user = getUser1()
        userDAO.insertUser(user)
        val userFromDB = userDAO.getUser("john.doe@mail.com")
        assert(userFromDB == user)
        userDAO.deleteUser("john.doe@mail.com")
    }

    @Test
    fun testGetAllUsers() {
        setUp()
        val user = getUser1()
        val user2 = getUser2()
        userDAO.insertUser(user)
        userDAO.insertUser(user2)
        val users = userDAO.getAllUsers()
        assert(users.contains(user))
        assert(users.contains(user2))
        userDAO.deleteAllUsers()
    }

    @Test
    fun testUpdateIsConnected() {
        setUp()
        val user = getUser1()
        userDAO.insertUser(user)
        userDAO.updateIsConnected(true, "john.doe@mail.com")
        val userFromDB = userDAO.getUser("john.doe@mail.com")
        assert(userFromDB!!.isConnected)
        userDAO.deleteAllUsers()
    }

    @Test
    fun testGetConnectedUser() {
        setUp()
        val user = getUser1()
        userDAO.insertUser(user)
        userDAO.updateIsConnected(true, "john.doe@mail.com")
        val userFromDB = userDAO.getConnectedUser()
        assert(userFromDB!!.email == user.email)
        userDAO.deleteAllUsers()
    }


    fun getUser1(): User {
        return User(
            "Doe",
            "John",
            "john.doe@mail.com",
            "01/01/2000",
            "M",
            70,
            180,
            false,
            "1234",
            "",
            "",
            "",
            false
        )
    }

    fun getUser2(): User {
        return User(
            "Doe",
            "Jane",
            "jane.doe@mail.com",
            "01/01/2000",
            "F",
            70,
            180,
            false,
            "1234",
            "",
            "",
            "",
            false
        )
    }
}