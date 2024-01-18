package com.example.mms.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mms.database.inApp.AppDatabase
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.databinding.ActivitySettingsBinding
import com.example.mms.ui.loader.LoaderActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        db = SingletonDatabase.getDatabase(this)

        binding.backButton.buttonArrowBack.setOnClickListener {
            finish()
        }

        binding.buttonDelete.setOnClickListener {
            // delete user
            Thread {
                val user = db.userDao().getConnectedUser()
                if (user != null) {
                    db.userDao().deleteUser(user.email)
                }
            }.start()

            // go to loader activity
            val intent = Intent(this, LoaderActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
