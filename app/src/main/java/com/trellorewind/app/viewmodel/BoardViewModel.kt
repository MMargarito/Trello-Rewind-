package com.trellorewind.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trellorewind.app.data.dao.BoardDao
import com.trellorewind.app.data.entity.BoardEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BoardViewModel(
    private val boardDao: BoardDao
) : ViewModel() {
    
    val boards = boardDao.getAllBoards()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun createBoard(title: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val board = BoardEntity(title = title)
                boardDao.insertBoard(board)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to create board"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateBoard(board: BoardEntity) {
        viewModelScope.launch {
            try {
                boardDao.updateBoard(board)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update board"
            }
        }
    }
    
    fun deleteBoard(board: BoardEntity) {
        viewModelScope.launch {
            try {
                boardDao.deleteBoard(board)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete board"
            }
        }
    }
    
    /**
     * Restores a previously deleted board by reinserting it
     */
    fun restoreBoard(board: BoardEntity) {
        viewModelScope.launch {
            try {
                boardDao.insertBoard(board)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to restore board"
            }
        }
    }
    
    suspend fun getBoardCount(): Int {
        return boardDao.getBoardCount()
    }
    
    fun clearError() {
        _error.value = null
    }
}

