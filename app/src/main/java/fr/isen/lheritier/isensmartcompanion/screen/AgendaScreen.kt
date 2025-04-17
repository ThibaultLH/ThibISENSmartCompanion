package fr.isen.lheritier.isensmartcompanion.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import fr.isen.lheritier.isensmartcompanion.composable.PreferencesManager
import fr.isen.lheritier.isensmartcompanion.data.Event
import fr.isen.lheritier.isensmartcompanion.database.AppDatabase

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
        pinnedEvents = PreferencesManager.getEnabledEvents(context, allEvents)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allCourses) { course ->
                CourseItem(course)
            }
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "🔔  Événements  🔔",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

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
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = "Icône événement",
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Date de l'événement",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Text(
                    text = event.date,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                )

                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Lieu de l'événement",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Text(
                    text = event.location,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = event.description,
                style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
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
        Row(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF11998E), Color(0xFF38EF7D))
                    )
                )
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Salle ${course.room}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = course.time,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                )
                Text(
                    text = course.teacher,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                )
            }
        }
    }
}
data class Course(
    val id: Int,
    val title: String,
    val time: String,
    val room: String,
    val teacher: String
)

fun mockCourses(): List<Course> {
    return listOf(
        Course(1, "Programmation Mobile", "08h00 - 09h30", "A101", "Mme. Lefèvre"),
        Course(2, "Réseaux Avancés", "10h00 - 11h30", "B204", "M. Laurent"),
        Course(3, "Machine Learning", "13h00 - 14h30", "C302", "Dr. Nguyen"),
        Course(4, "Cybersécurité", "15h00 - 16h30", "D201", "Mme. Dubois")
    )
}
