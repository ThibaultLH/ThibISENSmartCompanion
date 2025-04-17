package fr.isen.lheritier.isensmartcompanion.composable

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.isen.lheritier.isensmartcompanion.data.Event
import fr.isen.lheritier.isensmartcompanion.data.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                selected = false,
                onClick = { navController.navigate(route) }
            )
        }
    }
}

@Composable
fun EventsScreen() {
    val events = remember { mutableStateOf<List<Event>>(emptyList()) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Log.d("AgendaScreen", "AAAAA $events")

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
    LazyColumn {
        items(events.value) { event ->
            EventItem(event = event) { clickedEvent ->
                val intent = Intent(context, EventDetailActivity::class.java).apply {
                    putExtra("event", event)
                }
                context.startActivity(intent)
            }
        }
    }
}

@Composable
fun EventItem(event: Event, onEventClick: (fr.isen.lheritier.isensmartcompanion.data.Event) -> Unit) {
    Card(
        modifier = androidx.compose.ui.Modifier.Companion
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onEventClick(event)},
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = androidx.compose.ui.Modifier.Companion.padding(16.dp)
        ) {
            Text(
                text = event.title,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Companion.Bold,
                fontSize = 20.sp
            )
            androidx.compose.foundation.layout.Spacer(
                modifier = androidx.compose.ui.Modifier.Companion.height(
                    8.dp
                )
            )
            Text(text = event.date)
            androidx.compose.foundation.layout.Spacer(
                modifier = androidx.compose.ui.Modifier.Companion.height(
                    8.dp
                )
            )
            Text(text = event.location)
        }
    }
}