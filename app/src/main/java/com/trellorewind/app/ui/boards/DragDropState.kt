package com.trellorewind.app.ui.boards

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.trellorewind.app.data.entity.CardEntity
import com.trellorewind.app.data.entity.CardStatus

/**
 * State holder for drag and drop operations
 */
class DragDropState {
    
    var isDragging by mutableStateOf(false)
        private set
    
    var draggedCard by mutableStateOf<CardEntity?>(null)
        private set
    
    var dragOffset by mutableStateOf(Offset.Zero)
        private set
    
    var targetColumn by mutableStateOf<CardStatus?>(null)
        private set
    
    // Store column bounds for drop zone detection
    private val columnBounds = mutableMapOf<CardStatus, Rect>()
    
    /**
     * Start dragging a card
     */
    fun startDrag(card: CardEntity, offset: Offset) {
        isDragging = true
        draggedCard = card
        dragOffset = offset
        targetColumn = null
    }
    
    /**
     * Update drag position
     */
    fun updateDragPosition(offset: Offset) {
        dragOffset = offset
        // Check which column the card is over
        targetColumn = columnBounds.entries.firstOrNull { (_, bounds) ->
            bounds.contains(offset)
        }?.key
    }
    
    /**
     * End drag operation
     * Returns the new status if dropped in a valid column, null otherwise
     */
    fun endDrag(): CardStatus? {
        val result = targetColumn
        isDragging = false
        draggedCard = null
        dragOffset = Offset.Zero
        targetColumn = null
        return result
    }
    
    /**
     * Cancel drag operation
     */
    fun cancelDrag() {
        isDragging = false
        draggedCard = null
        dragOffset = Offset.Zero
        targetColumn = null
    }
    
    /**
     * Register a column's bounds for drop detection
     */
    fun registerColumnBounds(status: CardStatus, bounds: Rect) {
        columnBounds[status] = bounds
    }
    
    /**
     * Clear all registered column bounds
     */
    fun clearColumnBounds() {
        columnBounds.clear()
    }
}

@Composable
fun rememberDragDropState(): DragDropState {
    return remember { DragDropState() }
}




