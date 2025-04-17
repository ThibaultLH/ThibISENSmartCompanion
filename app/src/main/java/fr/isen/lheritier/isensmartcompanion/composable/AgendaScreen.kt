package fr.isen.lheritier.isensmartcompanion.composable

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.isen.lheritier.isensmartcompanion.data.Event

@Composable
fun AgendaScreen() {
    val context = LocalContext.current

    var pinnedEvents by remember { mutableStateOf<List<Event>>(emptyList()) }
    var allEvents by remember { mutableStateOf<List<Event>>(emptyList()) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val allCourses = mockCourses()
    val db = remember { AppDatabase.getInstance(context) }

    LaunchedEffect(Unit) {
        allEvents = db.eventDao().getAllEvents()

        Log.d("AgendaScreen", "Tous les événements récupérés : $allEvents")

        pinnedEvents = PreferencesManager.getEnabledEvents(context, allEvents)

        Log.d("AgendaScreen", "Événements suivis : $pinnedEvents")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // En-tête des cours
        Text(
            text = "📚  Cours  📚",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        // Affichage des cours
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allCourses) { course ->
                CourseItem(course)
            }
        }

        Spacer(Modifier.height(32.dp))

        // En-tête des événements
        Text(
            text = "🔔  Événements  🔔  ",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        // Affichage des événements suivis
        if (pinnedEvents.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(pinnedEvents) { event ->
                    EventItem(event) {
                        selectedEvent = event
                        showDialog = true
                    }
                }
            }
        } else {
            Text(
                text = "Aucun événement suivi.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    // Pop-up de détails événement
    if (showDialog && selectedEvent != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = selectedEvent!!.title) },
            text = {
                Column {
                    Text("Date : ${selectedEvent!!.date}")
                    Text("Lieu : ${selectedEvent!!.location}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(selectedEvent!!.description)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Fermer")
                }
            }
        )
    }
}

@Composable
fun EventItem(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.title, style = MaterialTheme.typography.titleMedium)
            Text(text = event.date, style = MaterialTheme.typography.bodySmall)
            Text(text = event.location, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun CourseItem(course: Course) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = course.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "${course.time} - ${course.room}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Enseignant : ${course.teacher}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

// Mock pour les cours
data class Course(
    val id: Int,
    val title: String,
    val time: String,
    val room: String,
    val teacher: String
)

fun mockCourses(): List<Course> {
    return listOf(
        Course(1, "Maths avancées", "08h30 - 10h00", "B203", "M. Dupont"),
        Course(2, "IoT", "10h15 - 12h00", "C105", "Mme. Bernard"),
        Course(3, "Systèmes embarqués", "13h30 - 15h00", "D301", "Dr. Martin")
    )
}
