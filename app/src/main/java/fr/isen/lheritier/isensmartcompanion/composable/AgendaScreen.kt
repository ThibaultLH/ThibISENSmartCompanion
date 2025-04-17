package fr.isen.lheritier.isensmartcompanion.composable

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import fr.isen.lheritier.isensmartcompanion.data.Event

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

@Composable
fun AgendaScreen() {
    val context = LocalContext.current
    val courses = mockCourses()
    val allEvents = mockEvents() // Liste de tous les événements simulés

    val pinnedEventIds = PreferencesManager.getEnabledEventIds(context)
    Log.d("AgendaScreen", "Pinned Event IDs: $pinnedEventIds")
    val pinnedEvents = allEvents.filter { it.id in pinnedEventIds }
    Log.d("AgendaScreen", "Pinned Events: $pinnedEvents")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "📚 Cours",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(courses) { course ->
                CourseItem(course)
            }
        }

        Spacer(Modifier.height(32.dp))

        // Afficher les événements suivis (pinned)
        Text(
            text = "📌 Mes événements suivis",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (pinnedEvents.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(pinnedEvents) { event ->
                    EventItem(event)
                }
            }
        } else {
            Text(
                text = "Aucun événement suivi.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun EventItem(event: Event) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.title, style = MaterialTheme.typography.titleMedium)
            Text(text = event.date, style = MaterialTheme.typography.bodySmall)
            Text(text = event.location, style = MaterialTheme.typography.bodySmall)
        }
    }
}

fun mockEvents(): List<Event> {
    return listOf(
        Event(
            id = "1",
            title = "Hackathon ISEN",
            description = "Concours de programmation entre étudiants.",
            date = "20/04/2025",
            location = "ISEN Lille",
            category = "Informatique"
        ),
        Event(
            id = "2",
            title = "Conférence sur l'IA",
            description = "Tout savoir sur l'intelligence artificielle.",
            date = "25/04/2025",
            location = "ISEN Toulon",
            category = "Technologie"
        ),
        Event(
            id = "3",
            title = "Portes ouvertes ISEN",
            description = "Découverte des laboratoires et des projets étudiants.",
            date = "27/04/2025",
            location = "ISEN Brest",
            category = "Découverte"
        )
    )
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


