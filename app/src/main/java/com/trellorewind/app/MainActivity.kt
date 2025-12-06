package com.trellorewind.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.trellorewind.app.data.AppDatabase
import com.trellorewind.app.interaction.rememberConfettiManager
import com.trellorewind.app.interaction.rememberHapticsManager
import com.trellorewind.app.interaction.rememberSoundManager
import com.trellorewind.app.ui.boards.BoardListScreen
import com.trellorewind.app.ui.boards.BoardScreen
import com.trellorewind.app.ui.settings.SettingsScreen
import com.trellorewind.app.ui.theme.KanheroTheme
import com.trellorewind.app.ui.theme.ThemeManager
import com.trellorewind.app.ui.theme.rememberThemeManager
import com.trellorewind.app.viewmodel.BoardViewModel
import com.trellorewind.app.viewmodel.BillingViewModel
import com.trellorewind.app.viewmodel.CardViewModel

class MainActivity : ComponentActivity() {
    
    private lateinit var database: AppDatabase
    private lateinit var boardViewModel: BoardViewModel
    private lateinit var cardViewModel: CardViewModel
    private lateinit var billingViewModel: BillingViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize database
        database = AppDatabase.getDatabase(this)
        
        // Initialize ViewModels
        boardViewModel = BoardViewModel(database.boardDao())
        cardViewModel = CardViewModel(database.cardDao())
        billingViewModel = BillingViewModel()
        
        // Initialize billing
        billingViewModel.initializeBilling(this)
        
        setContent {
            val themeManager = rememberThemeManager()
            
            KanheroTheme(darkTheme = themeManager.isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KanheroApp(
                        boardViewModel = boardViewModel,
                        cardViewModel = cardViewModel,
                        billingViewModel = billingViewModel,
                        themeManager = themeManager
                    )
                }
            }
        }
    }
}

@Composable
fun KanheroApp(
    boardViewModel: BoardViewModel,
    cardViewModel: CardViewModel,
    billingViewModel: BillingViewModel,
    themeManager: ThemeManager
) {
    val navController = rememberNavController()
    val hapticsManager = rememberHapticsManager()
    val soundManager = rememberSoundManager()
    val confettiManager = rememberConfettiManager()
    
    // Store boards list to access board titles
    val boards by boardViewModel.boards.collectAsState(initial = emptyList())
    
    NavHost(
        navController = navController,
        startDestination = "board_list"
    ) {
        // Board List Screen
        composable("board_list") {
            BoardListScreen(
                boardViewModel = boardViewModel,
                billingViewModel = billingViewModel,
                onBoardClick = { boardId ->
                    navController.navigate("board/$boardId")
                },
                onSettingsClick = {
                    navController.navigate("settings")
                }
            )
        }
        
        // Individual Board Screen
        composable(
            route = "board/{boardId}",
            arguments = listOf(navArgument("boardId") { type = NavType.IntType })
        ) { backStackEntry ->
            val boardId = backStackEntry.arguments?.getInt("boardId") ?: 0
            val board = boards.find { it.id == boardId }
            
            BoardScreen(
                boardId = boardId,
                boardTitle = board?.title ?: "Board",
                cardViewModel = cardViewModel,
                boardViewModel = boardViewModel,
                onBackClick = { navController.popBackStack() },
                onDeleteBoard = { navController.popBackStack() }
            )
        }
        
        // Settings Screen
        composable("settings") {
            SettingsScreen(
                hapticsManager = hapticsManager,
                soundManager = soundManager,
                confettiManager = confettiManager,
                themeManager = themeManager,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

