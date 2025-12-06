package com.trellorewind.app.ui.boards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.trellorewind.app.data.entity.CardEntity

/**
 * Overlay that shows the dragged card following the pointer
 */
@Composable
fun DraggingCardOverlay(
    dragDropState: DragDropState,
    modifier: Modifier = Modifier
) {
    val draggedCard = dragDropState.draggedCard
    val isDragging = dragDropState.isDragging
    
    if (isDragging && draggedCard != null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .zIndex(1000f) // Ensure it's on top
        ) {
            Card(
                modifier = Modifier
                    .width(200.dp)
                    .graphicsLayer {
                        translationX = dragDropState.dragOffset.x
                        translationY = dragDropState.dragOffset.y
                    }
                    .shadow(12.dp, RoundedCornerShape(8.dp))
                    .alpha(0.9f),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = draggedCard.text,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                        maxLines = 3
                    )
                }
            }
        }
    }
}




