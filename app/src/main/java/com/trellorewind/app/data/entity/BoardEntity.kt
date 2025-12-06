package com.trellorewind.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boards")
data class BoardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis()
)

