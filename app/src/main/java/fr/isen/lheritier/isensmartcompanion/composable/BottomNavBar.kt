package fr.isen.lheritier.isensmartcompanion.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = Color(0xFFE3E3ED)
    ) {
        val items = listOf(
            Triple("main", "Accueil", Icons.Default.Home),
            Triple("events", "Événements", Icons.Default.Event),
            Triple("agenda", "Agenda", Icons.Default.CalendarToday),
            Triple("history", "Historique", Icons.Default.History)
        )

        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = false, // Tu peux gérer la sélection actuelle ici
                onClick = { navController.navigate(route) }
            )
        }
    }
}
