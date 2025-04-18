package fr.isen.lheritier.isensmartcompanion.screen

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.isen.lheritier.isensmartcompanion.data.Event
import fr.isen.lheritier.isensmartcompanion.data.RetrofitInstance
import fr.isen.lheritier.isensmartcompanion.database.insertEventsToDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun BottomNavigationBar(navController: NavController) {
    // Liste des éléments de la BottomNavigationBar
    val items = listOf(
        Triple("main", "Accueil", Icons.Default.Home),
        Triple("events", "Événements", Icons.Default.Event),
        Triple("agenda", "Agenda", Icons.Default.CalendarToday),
        Triple("history", "Historique", Icons.Default.History)
    )

    // NavigationBar
    NavigationBar(
        containerColor = Color(0xFFB2EBF2), // Une couleur de fond douce et moderne
        contentColor = Color(0xFF00796B), // Une couleur primaire
        modifier = Modifier
            .fillMaxWidth() // S'assure que la barre occupe toute la largeur
            .padding(0.dp) // Supprime le padding interne qui pourrait poser problème
    ) {
        // Parcours des éléments pour afficher les items de la barre de navigation
        items.forEach { (route, label, icon) ->
            // Vérifie si la route actuelle est la même que celle du item
            val isSelected = navController.currentBackStackEntry?.destination?.route == route

            // Animation et effet de sélection avec un fond coloré et arrondi
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) Color(0xFF004D40) else Color(0xFF616161), // Couleur de l'icône sélectionnée
                        modifier = Modifier.size(30.dp) // Augmente la taille des icônes
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isSelected) Color(0xFF004D40) else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp // Augmenter légèrement la taille du texte
                        ),
                        maxLines = 1, // Assurer qu'il n'y ait qu'une seule ligne
                        overflow = TextOverflow.Ellipsis // Ajouter une ellipse si le texte est trop long
                    )
                },
                selected = isSelected,
                onClick = {
                    // Naviguer vers la nouvelle route
                    navController.navigate(route) {
                        // Empêche l'ajout de multiples destinations identiques dans la stack
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .padding(8.dp) // Espacement autour des éléments
                    .background(
                        color = if (isSelected) Color(0xFF80DEEA) else Color.Transparent, // Fond coloré lors de la sélection
                        shape = MaterialTheme.shapes.medium // Coins arrondis
                    )
                    .padding(12.dp) // Espacement interne
            )
        }
    }
}




@Composable
fun EventsScreen() {
    val events = remember { mutableStateOf<List<Event>>(emptyList()) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        RetrofitInstance.api.getEvents().enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    events.value = response.body() ?: emptyList()
                    Log.d("EventsScreen", "Événements chargés : ${events.value}")
                    coroutineScope.launch(Dispatchers.IO) {
                        insertEventsToDatabase(context, events.value)
                    }
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Toast.makeText(context, "Erreur de connexion", Toast.LENGTH_LONG).show()
            }
        })
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(events.value) { event ->
            EventItemStyled(event = event) { clickedEvent ->
                val intent = Intent(context, EventDetailActivity::class.java).apply {
                    putExtra("event", event)
                }
                context.startActivity(intent)
            }
        }
    }
}

@Composable
fun EventItemStyled(event: Event, onEventClick: (Event) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEventClick(event) }
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFE1F5FE), Color(0xFFB3E5FC))
                )
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Bandeau coloré à gauche
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF0288D1))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = event.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color(0xFF01579B)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Date",
                        tint = Color(0xFF0288D1),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = event.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Lieu",
                        tint = Color(0xFF0288D1),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
