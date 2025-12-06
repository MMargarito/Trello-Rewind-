package com.trellorewind.app.ui.boards

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trellorewind.app.data.entity.CardEntity

/**
 * Dialog for editing an existing card's text
 */
@Composable
fun EditCardDialog(
    card: CardEntity,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var cardText by remember { mutableStateOf(card.text) }
    var hasChanges by remember { mutableStateOf(false) }
    
    // Track if text has changed
    LaunchedEffect(cardText) {
        hasChanges = cardText != card.text
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Card") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = cardText,
                    onValueChange = { cardText = it },
                    label = { Text("Card Text") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 8,
                    supportingText = {
                        Text(
                            text = "${cardText.length} characters",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (cardText.isNotBlank()) {
                        onSave(cardText.trim())
                    }
                },
                enabled = cardText.isNotBlank() && hasChanges
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}




