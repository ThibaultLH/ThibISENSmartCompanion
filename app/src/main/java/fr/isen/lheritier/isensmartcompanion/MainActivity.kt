package fr.isen.lheritier.isensmartcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import fr.isen.lheritier.isensmartcompanion.screen.AgendaScreen
import fr.isen.lheritier.isensmartcompanion.database.AppDatabase
import fr.isen.lheritier.isensmartcompanion.screen.BottomNavigationBar
import fr.isen.lheritier.isensmartcompanion.screen.EventsScreen
import fr.isen.lheritier.isensmartcompanion.composable.InteractionViewModel
import fr.isen.lheritier.isensmartcompanion.screen.HistoryScreen
import fr.isen.lheritier.isensmartcompanion.screen.MainScreen
import fr.isen.lheritier.isensmartcompanion.ui.theme.ISENSmartCompanionTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ISENSmartCompanionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8F6FC)
                ) {
                    AppNavigation(appDatabase = appDatabase)
                }
            }
        }
    }
    private val appDatabase: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }
}
@Composable
fun AppNavigation(appDatabase: AppDatabase) {
    val navController = rememberNavController()

    val viewModel: InteractionViewModel = viewModel()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("main") { MainScreen(viewModel = viewModel) }
            composable("history") { HistoryScreen(database = appDatabase) }
            composable("events") { EventsScreen() }
            composable("agenda") { AgendaScreen() }
        }
    }
}


