package com.example.mms.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@Entity
class Version (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var versionNumber: Int
)
