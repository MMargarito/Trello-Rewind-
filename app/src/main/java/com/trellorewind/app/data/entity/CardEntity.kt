package com.trellorewind.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = BoardEntity::class,
            parentColumns = ["id"],
            childColumns = ["boardId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("boardId")]
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val boardId: Int,
    val text: String,
    val status: CardStatus,
    val position: Int = 0, // For ordering cards within a column
    val createdAt: Long = System.currentTimeMillis()
)

enum class CardStatus {
    TODO,
    DOING,
    DONE
}

