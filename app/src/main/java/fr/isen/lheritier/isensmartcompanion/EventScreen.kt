package fr.isen.lheritier.isensmartcompanion

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import fr.isen.lheritier.isensmartcompanion.data.Event
import fr.isen.lheritier.isensmartcompanion.data.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun EventsScreen() {
    val events = remember { mutableStateOf<List<Event>>(emptyList()) }
    val context = LocalContext.current
    val showAddEventDialog = remember { mutableStateOf(false) }
    val selectedEvent = remember { mutableStateOf<Event?>(null) }

    LaunchedEffect(Unit) {
        RetrofitClient.apiService.getEvents().enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    events.value = response.body() ?: emptyList()
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
                selectedEvent.value = clickedEvent // Stocker l'événement cliqué
                showAddEventDialog.value = true // Afficher le formulaire
            }
        }
    }

    if (showAddEventDialog.value) {
        AddEventDialog(
            parentEvent = selectedEvent.value,
            onEventAdded = { newEvent ->
                events.value = events.value + newEvent
                showAddEventDialog.value = false
                selectedEvent.value = null
            },
            onDismiss = {
                showAddEventDialog.value = false
                selectedEvent.value = null
            }
        )
    }
}

@Composable
fun AddEventDialog(parentEvent: Event?, onEventAdded: (Event) -> Unit, onDismiss: () -> Unit) {
    //  Formulaire pour ajouter un nouvel événement
    //  Utilisez des TextField, Button, etc.
    //  Incluez peut-être des champs pré-remplis ou des options liées à parentEvent
    //  Appelez onEventAdded(newEvent) pour ajouter l'événement
    //  Appelez onDismiss() pour fermer le dialogue
}


@Composable
fun EventItem(event: Event, onEventClick: (Event) -> Unit) {
    // Ce composant représente un événement dans la liste
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onEventClick(event)
            },
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = event.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = event.date)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = event.location)
        }
    }
}