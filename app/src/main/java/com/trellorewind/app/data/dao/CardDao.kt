package com.trellorewind.app.data.dao

import androidx.room.*
import com.trellorewind.app.data.entity.CardEntity
import com.trellorewind.app.data.entity.CardStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    
    @Query("SELECT * FROM cards WHERE boardId = :boardId ORDER BY position ASC, createdAt ASC")
    fun getCardsByBoardId(boardId: Int): Flow<List<CardEntity>>
    
    @Query("SELECT * FROM cards WHERE boardId = :boardId AND status = :status ORDER BY position ASC, createdAt ASC")
    fun getCardsByStatus(boardId: Int, status: CardStatus): Flow<List<CardEntity>>
    
    @Query("SELECT * FROM cards WHERE id = :cardId")
    suspend fun getCardById(cardId: Int): CardEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity): Long
    
    @Update
    suspend fun updateCard(card: CardEntity)
    
    @Delete
    suspend fun deleteCard(card: CardEntity)
    
    @Query("DELETE FROM cards WHERE id = :cardId")
    suspend fun deleteCardById(cardId: Int)
    
    @Query("UPDATE cards SET status = :newStatus WHERE id = :cardId")
    suspend fun updateCardStatus(cardId: Int, newStatus: CardStatus)
    
    @Query("UPDATE cards SET position = :position WHERE id = :cardId")
    suspend fun updateCardPosition(cardId: Int, position: Int)
}

