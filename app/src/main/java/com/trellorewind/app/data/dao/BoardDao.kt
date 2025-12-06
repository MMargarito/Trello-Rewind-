package com.trellorewind.app.data.dao

import androidx.room.*
import com.trellorewind.app.data.entity.BoardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BoardDao {
    
    @Query("SELECT * FROM boards ORDER BY createdAt DESC")
    fun getAllBoards(): Flow<List<BoardEntity>>
    
    @Query("SELECT * FROM boards WHERE id = :boardId")
    fun getBoardById(boardId: Int): Flow<BoardEntity?>
    
    @Query("SELECT COUNT(*) FROM boards")
    suspend fun getBoardCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoard(board: BoardEntity): Long
    
    @Update
    suspend fun updateBoard(board: BoardEntity)
    
    @Delete
    suspend fun deleteBoard(board: BoardEntity)
    
    @Query("DELETE FROM boards WHERE id = :boardId")
    suspend fun deleteBoardById(boardId: Int)
}

