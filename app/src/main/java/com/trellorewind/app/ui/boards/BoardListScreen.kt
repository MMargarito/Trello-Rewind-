package com.trellorewind.app.ui.boards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trellorewind.app.data.entity.BoardEntity
import com.trellorewind.app.interaction.rememberSoundManager
import com.trellorewind.app.viewmodel.BoardViewModel
import com.trellorewind.app.viewmodel.BillingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardListScreen(
    boardViewModel: BoardViewModel,
    billingViewModel: BillingViewModel,
    onBoardClick: (Int) -> Unit,
    onSettingsClick: () -> Unit
) {
    val boards by boardViewModel.boards.collectAsState(initial = emptyList())
    val isPremium by billingViewModel.isPremium.collectAsState()
    val scope = rememberCoroutineScope()
    val soundManager = rememberSoundManager()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showCreateDialog by remember { mutableStateOf(false) }
    var showUpgradeDialog by remember { mutableStateOf(false) }
    
    // For undo functionality
    var deletedBoard by remember { mutableStateOf<BoardEntity?>(null) }
    
    // Function to handle board deletion with undo
    val handleBoardDelete: (BoardEntity) -> Unit = { board ->
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
            }
            deletedBoard = null
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Kanhero") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        val canCreate = billingViewModel.canCreateBoard(boardViewModel.getBoardCount())
                        if (canCreate) {
                            showCreateDialog = true
                        } else {
                            showUpgradeDialog = true
                        }
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Board")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (boards.isEmpty()) {
                EmptyBoardsState(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(boards) { board ->
                        BoardCard(
                            board = board,
                            onClick = { onBoardClick(board.id) }
                        )
                    }
                }
            }
            
            // Premium Banner
            AnimatedVisibility(
                visible = !isPremium && boards.size >= BillingViewModel.FREE_BOARD_LIMIT,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Free Tier: 3 Boards Max",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { showUpgradeDialog = true }) {
                            Text("Unlock Unlimited Boards")
                        }
                    }
                }
            }
        }
    }
    
    if (showCreateDialog) {
        CreateBoardDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title ->
                boardViewModel.createBoard(title)
                soundManager.playBoardCreated()
                showCreateDialog = false
            }
        )
    }
    
    if (showUpgradeDialog) {
        UpgradeDialog(
            onDismiss = { showUpgradeDialog = false },
            onUpgrade = {
                // Handle upgrade - billing flow will be triggered
                showUpgradeDialog = false
            }
        )
    }
}

@Composable
fun BoardCard(
    board: BoardEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = board.title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EmptyBoardsState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No boards yet",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create your first board to get started!",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CreateBoardDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var boardTitle by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Board") },
        text = {
            OutlinedTextField(
                value = boardTitle,
                onValueChange = { boardTitle = it },
                label = { Text("Board Title") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (boardTitle.isNotBlank()) onCreate(boardTitle) },
                enabled = boardTitle.isNotBlank()
            ) {
                Text("Create")
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
fun UpgradeDialog(
    onDismiss: () -> Unit,
    onUpgrade: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Unlock Unlimited Boards") },
        text = {
            Column {
                Text("Get unlimited boards with a one-time payment of $10.00")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Create unlimited boards\n• Lifetime access\n• No subscriptions",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            Button(onClick = onUpgrade) {
                Text("Upgrade Now")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Maybe Later")
            }
        }
    )
}

