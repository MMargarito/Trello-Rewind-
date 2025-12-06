package com.trellorewind.app.ui.boards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlin.math.roundToInt
import com.trellorewind.app.data.entity.BoardEntity
import com.trellorewind.app.data.entity.CardEntity
import com.trellorewind.app.data.entity.CardStatus
import com.trellorewind.app.interaction.ConfettiAnimation
import com.trellorewind.app.interaction.HapticsManager
import com.trellorewind.app.interaction.SoundManager
import com.trellorewind.app.interaction.rememberConfettiManager
import com.trellorewind.app.interaction.rememberHapticsManager
import com.trellorewind.app.interaction.rememberSoundManager
import com.trellorewind.app.ui.theme.KanheroTheme
import com.trellorewind.app.viewmodel.BoardViewModel
import com.trellorewind.app.viewmodel.CardViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BoardScreen(
    boardId: Int,
    boardTitle: String,
    cardViewModel: CardViewModel,
    boardViewModel: BoardViewModel,
    onBackClick: () -> Unit,
    onDeleteBoard: () -> Unit,
    onBoardRenamed: (String) -> Unit = {}
) {
    val cards by cardViewModel.cards.collectAsState()
    val boards by boardViewModel.boards.collectAsState(initial = emptyList())
    val hapticsManager = rememberHapticsManager()
    val soundManager = rememberSoundManager()
    val confettiManager = rememberConfettiManager()
    val dragDropState = rememberDragDropState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showAddCardDialog by remember { mutableStateOf<CardStatus?>(null) }
    var showEditCardDialog by remember { mutableStateOf<CardEntity?>(null) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showRenameBoardDialog by remember { mutableStateOf(false) }
    var showDeleteBoardConfirm by remember { mutableStateOf(false) }
    var currentBoardTitle by remember { mutableStateOf(boardTitle) }
    
    // For undo functionality
    var deletedCard by remember { mutableStateOf<CardEntity?>(null) }
    var deletedBoard by remember { mutableStateOf<BoardEntity?>(null) }
    
    LaunchedEffect(boardId) {
        cardViewModel.setCurrentBoard(boardId)
    }
    
    // Update title when it changes externally
    LaunchedEffect(boardTitle) {
        currentBoardTitle = boardTitle
    }
    
    // Handler for card deletion with undo
    val handleCardDelete: (CardEntity) -> Unit = { card ->
        deletedCard = card
        cardViewModel.deleteCard(card)
        
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "Card deleted",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                // Restore the card
                deletedCard?.let { 
                    cardViewModel.createCardWithId(it)
                }
            }
            deletedCard = null
        }
    }
    
    // Pager state - declared here so it can be used in handlers
    val pagerState = rememberPagerState(pageCount = { 3 })
    
    // Helper to get page index for a status
    fun getPageForStatus(status: CardStatus): Int = when (status) {
        CardStatus.TODO -> 0
        CardStatus.DOING -> 1
        CardStatus.DONE -> 2
    }
    
    // Handler for when a card is moved (with auto-navigation)
    val handleCardMove: (CardEntity, CardStatus) -> Unit = { card, targetStatus ->
        if (targetStatus != card.status) {
            // Update card status
            cardViewModel.updateCardStatus(card.id, targetStatus)
            
            // Trigger appropriate feedback
            if (targetStatus == CardStatus.DONE) {
                hapticsManager.successTap()
                soundManager.playCardDone()
                confettiManager.trigger()
            } else {
                hapticsManager.mediumTap()
                soundManager.playCardDrop()
            }
            
            // Auto-navigate to the target column
            scope.launch {
                pagerState.animateScrollToPage(getPageForStatus(targetStatus))
            }
        }
    }
    
    // Handler for when a card is dropped (legacy - for drag-drop)
    val handleCardDrop: (CardEntity, CardStatus?) -> Unit = { card, targetStatus ->
        if (targetStatus != null && targetStatus != card.status) {
            handleCardMove(card, targetStatus)
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text(currentBoardTitle) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { showMoreMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More")
                            }
                            DropdownMenu(
                                expanded = showMoreMenu,
                                onDismissRequest = { showMoreMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Rename Board") },
                                    onClick = {
                                        showMoreMenu = false
                                        showRenameBoardDialog = true
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Edit, contentDescription = null)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete Board") },
                                    onClick = {
                                        showMoreMenu = false
                                        showDeleteBoardConfirm = true
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Delete, contentDescription = null)
                                    }
                                )
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            // Get theme-aware colors
            val kanheroColors = KanheroTheme.colors
            
            // Column data for the pager
            val columns = listOf(
                Triple("TO DO", CardStatus.TODO, kanheroColors.todoColumn),
                Triple("DOING", CardStatus.DOING, kanheroColors.doingColumn),
                Triple("DONE", CardStatus.DONE, kanheroColors.doneColumn)
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Horizontal Pager for swipeable columns
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    pageSpacing = 16.dp
                ) { page ->
                    val (title, status, color) = columns[page]
                    
                    KanbanColumn(
                        title = title,
                        status = status,
                        cards = cards.filter { it.status == status },
                        columnColor = color,
                        onAddCard = { showAddCardDialog = status },
                        onCardDelete = handleCardDelete,
                        onCardEdit = { showEditCardDialog = it },
                        onMoveCard = handleCardMove,
                        hapticsManager = hapticsManager,
                        soundManager = soundManager,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                // Page Indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    columns.forEachIndexed { index, (title, _, color) ->
                        val isSelected = pagerState.currentPage == index
                        
                        // Indicator dot with label
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(if (isSelected) 12.dp else 8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
        
        // Dragging card overlay
        DraggingCardOverlay(
            dragDropState = dragDropState,
            modifier = Modifier.fillMaxSize()
        )
        
        // Confetti overlay
        ConfettiAnimation(
            confettiManager = confettiManager,
            modifier = Modifier.fillMaxSize()
        )
    }
    
    // Add Card Dialog
    showAddCardDialog?.let { status ->
        AddCardDialog(
            onDismiss = { showAddCardDialog = null },
            onAdd = { text ->
                cardViewModel.createCard(boardId, text, status)
                soundManager.playCardCreated()
                showAddCardDialog = null
            }
        )
    }
    
    // Edit Card Dialog
    showEditCardDialog?.let { card ->
        EditCardDialog(
            card = card,
            onDismiss = { showEditCardDialog = null },
            onSave = { newText ->
                val updatedCard = card.copy(text = newText)
                cardViewModel.updateCard(updatedCard)
                showEditCardDialog = null
            }
        )
    }
    
    // Rename Board Dialog
    if (showRenameBoardDialog) {
        RenameBoardDialog(
            currentTitle = currentBoardTitle,
            onDismiss = { showRenameBoardDialog = false },
            onRename = { newTitle ->
                val updatedBoard = BoardEntity(id = boardId, title = newTitle)
                boardViewModel.updateBoard(updatedBoard)
                currentBoardTitle = newTitle
                onBoardRenamed(newTitle)
                showRenameBoardDialog = false
            }
        )
    }
    
    // Delete Board Confirmation Dialog
    if (showDeleteBoardConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteBoardConfirm = false },
            title = { Text("Delete Board") },
            text = { Text("Are you sure you want to delete \"$currentBoardTitle\"? This will also delete all cards in this board.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteBoardConfirm = false
                        // Find and store the board for potential undo
                        val board = boards.find { it.id == boardId }
                        if (board != null) {
                            deletedBoard = board
                            boardViewModel.deleteBoard(board)
                            
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Board deleted",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    deletedBoard?.let { 
                                        boardViewModel.restoreBoard(it)
                                    }
                                    deletedBoard = null
                                } else {
                                    // Navigate back after snackbar dismisses
                                    onDeleteBoard()
                                }
                            }
                        } else {
                            onDeleteBoard()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteBoardConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun KanbanColumn(
    title: String,
    status: CardStatus,
    cards: List<CardEntity>,
    columnColor: androidx.compose.ui.graphics.Color,
    onAddCard: () -> Unit,
    onCardDelete: (CardEntity) -> Unit,
    modifier: Modifier = Modifier,
    onMoveCard: (CardEntity, CardStatus) -> Unit = { _, _ -> },
    hapticsManager: HapticsManager? = null,
    soundManager: SoundManager? = null,
    onCardEdit: (CardEntity) -> Unit = {}
) {
    // Card count for the header
    val cardCount = cards.size
    
    Card(
        modifier = modifier
            .fillMaxHeight()
            .background(columnColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = columnColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Column Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Card count badge
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "$cardCount",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                FilledTonalIconButton(
                    onClick = onAddCard,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Card",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Cards List
            if (cards.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No cards yet\nTap + to add one",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(cards, key = { it.id }) { card ->
                        DraggableCardItem(
                            card = card,
                            currentStatus = status,
                            onDelete = { onCardDelete(card) },
                            onEdit = { onCardEdit(card) },
                            onMoveCard = { newStatus -> 
                                onMoveCard(card, newStatus)
                            },
                            hapticsManager = hapticsManager,
                            soundManager = soundManager
                        )
                    }
                }
            }
        }
    }
}

/**
 * Draggable card item with touch support for moving between columns
 */
@Composable
fun DraggableCardItem(
    card: CardEntity,
    currentStatus: CardStatus,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onMoveCard: (CardStatus) -> Unit,
    hapticsManager: HapticsManager?,
    soundManager: SoundManager?
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDragging by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    
    // Determine available move directions
    val canMoveLeft = currentStatus != CardStatus.TODO
    val canMoveRight = currentStatus != CardStatus.DONE
    
    val leftStatus = when (currentStatus) {
        CardStatus.DOING -> CardStatus.TODO
        CardStatus.DONE -> CardStatus.DOING
        else -> null
    }
    
    val rightStatus = when (currentStatus) {
        CardStatus.TODO -> CardStatus.DOING
        CardStatus.DOING -> CardStatus.DONE
        else -> null
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isDragging) {
                    Modifier
                        .zIndex(10f)
                        .graphicsLayer {
                            translationX = dragOffset.x
                            translationY = dragOffset.y
                            scaleX = 1.05f
                            scaleY = 1.05f
                        }
                        .shadow(8.dp, RoundedCornerShape(12.dp))
                } else Modifier
            )
            .pointerInput(card.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        isDragging = true
                        hapticsManager?.lightTap()
                        soundManager?.playCardDrag()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount
                    },
                    onDragEnd = {
                        // Check if dragged far enough to move
                        val threshold = 100f
                        when {
                            dragOffset.x < -threshold && canMoveLeft && leftStatus != null -> {
                                onMoveCard(leftStatus)
                            }
                            dragOffset.x > threshold && canMoveRight && rightStatus != null -> {
                                onMoveCard(rightStatus)
                            }
                        }
                        isDragging = false
                        dragOffset = Offset.Zero
                    },
                    onDragCancel = {
                        isDragging = false
                        dragOffset = Offset.Zero
                    }
                )
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDragging) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Drag hint
            if (!isDragging) {
                Text(
                    text = "Hold & drag to move",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            } else {
                // Show drag direction hints
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (canMoveLeft) {
                        Text(
                            text = "← Drop to move left",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (canMoveRight) {
                        Text(
                            text = "Drop to move right →",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Card text
            Text(
                text = card.text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
            
            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Move left button
                if (canMoveLeft && leftStatus != null) {
                    FilledTonalButton(
                        onClick = { onMoveCard(leftStatus) },
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "← ${if (leftStatus == CardStatus.TODO) "TO DO" else "DOING"}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
                
                // Edit and Delete buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Card",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Card",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                
                // Move right button
                if (canMoveRight && rightStatus != null) {
                    FilledTonalButton(
                        onClick = { onMoveCard(rightStatus) },
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "${if (rightStatus == CardStatus.DOING) "DOING" else "DONE"} →",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
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
                    Text("Delete", color = MaterialTheme.colorScheme.error)
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

@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var cardText by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Card") },
        text = {
            OutlinedTextField(
                value = cardText,
                onValueChange = { cardText = it },
                label = { Text("Card Text") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (cardText.isNotBlank()) onAdd(cardText) },
                enabled = cardText.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RenameBoardDialog(
    currentTitle: String,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit
) {
    var boardTitle by remember { mutableStateOf(currentTitle) }
    var hasChanges by remember { mutableStateOf(false) }
    
    LaunchedEffect(boardTitle) {
        hasChanges = boardTitle != currentTitle && boardTitle.isNotBlank()
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Board") },
        text = {
            OutlinedTextField(
                value = boardTitle,
                onValueChange = { boardTitle = it },
                label = { Text("Board Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onRename(boardTitle.trim()) },
                enabled = hasChanges
            ) {
                Text("Rename")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

