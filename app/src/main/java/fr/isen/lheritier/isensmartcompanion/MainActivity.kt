package fr.isen.lheritier.isensmartcompanion

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.isensmartcompanion.R

import fr.isen.lheritier.isensmartcompanion.ui.theme.ISENSmartCompanionTheme

// Classe Event
data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val category: String
)

fun getEventList(): List<Event> {
    return listOf(
        Event(1, "Soirée BDE", "Une soirée organisée par le BDE", "12 Mars 2025", "ISEN", "Social"),
        Event(2, "Gala de Charité", "Un gala pour récolter des fonds", "25 Mars 2025", "Palais des Congrès", "Cultural"),
        Event(3, "Journée de Cohésion", "Une journée pour renforcer la cohésion", "10 Avril 2025", "ISEN", "Sport")
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ISENSmartCompanionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8F6FC)
                ) {
                    AppNavigation()
                }
            }

        }
    }
}

// 🚀 1. Gère la navigation entre les écrans
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "main", // L'écran principal
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("main") { MainScreen() }
            composable("events") { EventsScreen() }
            composable("agenda") { AgendaScreen() }
            composable("history") { HistoryScreen() }
            // Route pour l'écran de détail de l'événement
            composable("eventDetail/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId")?.toInt() ?: 0
                EventDetailScreen(eventId = eventId)
            }
        }
    }
}

// 🚀 2. Barre de Navigation en bas
@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = Color(0xFFE3E3ED)
    ) {
        val items = listOf("main" to "Accueil", "events" to "Événements", "agenda" to "Agenda", "history" to "Historique")

        items.forEach { (route, label) ->
            NavigationBarItem(
                icon = { Icon(painter = painterResource(id = R.drawable.ic_arrow), contentDescription = label) },
                label = { Text(label) },
                selected = false,
                onClick = { navController.navigate(route) }
            )
        }
    }
}

// 🚀 3. Écran principal (Accueil)
@Composable
fun MainScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "ISEN",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F)
            )
            Text(
                text = "Smart Companion",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
        InputSection()
    }
}

// 🚀 4. Champ de texte en bas de l'écran
@Composable
fun InputSection() {
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
                onClick = { /* Action du bouton */ },
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

@Composable
fun EventDetailScreen(eventId: Int) {
    // Simuler la récupération de l'événement par son ID
    val event = getEventById(eventId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),


        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = event.title,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Description :")
        Text(text = event.description, fontStyle = FontStyle.Italic)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Date : ${event.date}")
        Text(text = "Lieu : ${event.location}")
        Text(text = "Catégorie : ${event.category}")
    }
}

// Cette fonction simule la récupération d'un événement par son ID.
fun getEventById(eventId: Int): Event {
    val events = listOf(
        Event(1, "Soirée BDE", "Une soirée de cohésion pour les étudiants.", "2025-03-20", "ISEN Toulon", "Social"),
        Event(2, "Gala de l'ISEN", "Le gala annuel de l'école.", "2025-04-10", "Palais des Congrès", "Culture"),
        Event(3, "Journée de cohésion", "Journée d'activités et d'intégration.", "2025-03-28", "ISEN Toulon", "Cohésion")
    )
    return events.first { it.id == eventId }
}




// 🚀 6. Écran Agenda
@Composable
fun AgendaScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Agenda", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

// 🚀 7. Écran Historique
@Composable
fun HistoryScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Historique", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

// 🚀 8. Nouvelle activité pour afficher le détail d'un événement
class EventDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ISENSmartCompanionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Détail de l'Événement", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
