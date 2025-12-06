package com.trellorewind.app.ui.boards

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import com.trellorewind.app.data.entity.CardEntity
import com.trellorewind.app.data.entity.CardStatus
import com.trellorewind.app.interaction.HapticsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardItem(
    card: CardEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    dragDropState: DragDropState? = null,
    hapticsManager: HapticsManager? = null,
    onDragStart: () -> Unit = {},
    onDragEnd: (CardEntity, CardStatus?) -> Unit = { _, _ -> },
    onEdit: () -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var cardPosition by remember { mutableStateOf(Offset.Zero) }
    
    // Determine if this card is currently being dragged
    val isBeingDragged = dragDropState?.isDragging == true && 
                         dragDropState.draggedCard?.id == card.id
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(if (isBeingDragged) 8.dp else 2.dp, RoundedCornerShape(8.dp))
            .alpha(if (isBeingDragged) 0.5f else 1f)
            .onGloballyPositioned { coordinates ->
                cardPosition = coordinates.positionInRoot()
            }
            .then(
                if (dragDropState != null && hapticsManager != null) {
                    Modifier.pointerInput(card.id) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { offset ->
                                // Trigger haptic feedback
                                hapticsManager.lightTap()
                                onDragStart()
                                // Start drag operation with global coordinates
                                dragDropState.startDrag(
                                    card,
                                    cardPosition + offset
                                )
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                // Update drag position
                                dragDropState.updateDragPosition(
                                    dragDropState.dragOffset + dragAmount
                                )
                            },
                            onDragEnd = {
                                // Drag operation ends - get target status and notify parent
                                val targetStatus = dragDropState.endDrag()
                                onDragEnd(card, targetStatus)
                            },
                            onDragCancel = {
                                dragDropState.cancelDrag()
                            }
                        )
                    }
                } else Modifier
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onEdit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = card.text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Edit button
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Card",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Delete button
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Card",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Card") },
            text = { Text("Are you sure you want to delete this card?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

