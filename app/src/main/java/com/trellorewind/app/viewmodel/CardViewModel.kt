package com.trellorewind.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trellorewind.app.data.dao.CardDao
import com.trellorewind.app.data.entity.CardEntity
import com.trellorewind.app.data.entity.CardStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CardViewModel(
    private val cardDao: CardDao
) : ViewModel() {
    
    private val _currentBoardId = MutableStateFlow<Int?>(null)
    val currentBoardId: StateFlow<Int?> = _currentBoardId.asStateFlow()
    
    private val _cards = MutableStateFlow<List<CardEntity>>(emptyList())
    val cards: StateFlow<List<CardEntity>> = _cards.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun setCurrentBoard(boardId: Int) {
        _currentBoardId.value = boardId
        viewModelScope.launch {
            cardDao.getCardsByBoardId(boardId).collect { cardList ->
                _cards.value = cardList
            }
        }
    }
    
    fun createCard(boardId: Int, text: String, status: CardStatus = CardStatus.TODO) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val card = CardEntity(
                    boardId = boardId,
                    text = text,
                    status = status
                )
                cardDao.insertCard(card)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to create card"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateCard(card: CardEntity) {
        viewModelScope.launch {
            try {
                cardDao.updateCard(card)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update card"
            }
        }
    }
    
    fun updateCardStatus(cardId: Int, newStatus: CardStatus) {
        viewModelScope.launch {
            try {
                cardDao.updateCardStatus(cardId, newStatus)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update card status"
            }
        }
    }
    
    fun deleteCard(card: CardEntity) {
        viewModelScope.launch {
            try {
                cardDao.deleteCard(card)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete card"
            }
        }
    }
    
    /**
     * Restores a previously deleted card by reinserting it with all its original data
     */
    fun createCardWithId(card: CardEntity) {
        viewModelScope.launch {
            try {
                cardDao.insertCard(card)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to restore card"
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}

