package fr.isen.lheritier.isensmartcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.isensmartcompanion.R
import fr.isen.lheritier.isensmartcompanion.composable.AgendaScreen
import fr.isen.lheritier.isensmartcompanion.composable.AppDatabase
import fr.isen.lheritier.isensmartcompanion.composable.BottomNavigationBar
import fr.isen.lheritier.isensmartcompanion.composable.InteractionViewModel
import fr.isen.lheritier.isensmartcompanion.data.HistoryScreen
import fr.isen.lheritier.isensmartcompanion.composable.MainScreen
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
                    // Passer l'instance de appDatabase à AppNavigation
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

    // On passe le viewModel à MainScreen et HistoryScreen
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

@Composable
fun InputSection(viewModel: InteractionViewModel) {
    var text by remember { mutableStateOf("") }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Red, shape = MaterialTheme.shapes.medium)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    // Appeler la fonction du ViewModel pour gérer l'interaction avec Gemini
                    viewModel.handleUserMessage(text)
                    text = ""  // Réinitialiser la zone de texte après l'envoi
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFD32F2F), shape = CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow),
                    contentDescription = "Envoyer",
                    tint = Color.White
                )
            }
        }
    }
}

