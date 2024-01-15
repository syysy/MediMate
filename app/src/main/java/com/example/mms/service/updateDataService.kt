package com.example.mms.service

import android.content.Context
import android.util.Log
import com.example.mms.database.inApp.SingletonDatabase
import com.example.mms.database.mongoObjects.MongoVersion
import com.example.mms.errors.ConnectionFailed
import com.example.mms.model.Version

class UpdateDataService(context: Context) {
    private val apiService = ApiService.getInstance(context)
    private val database = SingletonDatabase.getDatabase(context)

    private var mongoVersion: MongoVersion? = null

    var nbMedicinesToDownload = 0

    fun needToUpdate(callback: (needToUpdate: Boolean) -> Unit, callbackError: () -> Unit) {
        var localVersion: Version? = null
        val thread = Thread {
            localVersion = this.database.versionDao().getFirst() ?: Version(0, 0)
        }
        thread.start()
        thread.join()


        this.apiService.getMedicinesCodesToUpdate(localVersion!!.versionNumber, { mongoVersion ->
            this.mongoVersion = mongoVersion

            if (this.mongoVersion == null) {
                callback(false)
            }

            callback(this.mongoVersion!!.updated_documents_cis.isNotEmpty())
        }, callbackError)
    }

    fun updateLocalVersion() {
        if (this.mongoVersion == null) {
            return
        }

        val thread = Thread {
            this.database.versionDao().insert(this.mongoVersion!!.toVersion())
        }
        thread.start()
        thread.join()
    }

    fun numberOfItemsToDownload(): Int {
        return this.mongoVersion?.updated_documents_cis?.size ?: 0
    }

    fun isNotFinish(): Boolean {
        return !this.isFinish()
    }

    fun isFinish(): Boolean {
        return this.nbMedicinesToDownload >= this.mongoVersion!!.updated_documents_cis.size
    }

    fun nextDownload(callback: () -> Unit, errorCallback: () -> Unit) {
        if (this.isFinish()) {
            callback()
            return
        }

        val actualMedicineCIS = this.mongoVersion!!.updated_documents_cis[this.nbMedicinesToDownload]
        this.apiService.getMedicine(actualMedicineCIS,
            { medicine ->
                Thread {
                    this.database.medicineDao().insert(medicine)
                    this.nbMedicinesToDownload++

                    callback()
                }.start()
            },
            {
                errorCallback()
            }
        )
    }

    fun downloadAllMedicineFromMongo() {
        val medicines = this.apiService.getAllMedicine() ?: throw ConnectionFailed()

        val thread = Thread {
            this.database.medicineDao().insertMany(medicines)
        }
        thread.start()
        thread.join()
    }
}
