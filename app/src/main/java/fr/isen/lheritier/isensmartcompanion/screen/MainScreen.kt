package fr.isen.lheritier.isensmartcompanion.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
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
import fr.isen.lheritier.isensmartcompanion.Api.Gemini
import fr.isen.lheritier.isensmartcompanion.composable.InteractionViewModel
import kotlinx.coroutines.launch
import fr.isen.lheritier.isensmartcompanion.data.Event

@Composable
fun MainScreen(viewModel: InteractionViewModel, modifier: Modifier = Modifier) {
    var question by remember { mutableStateOf("") }
    var responses by remember { mutableStateOf(listOf<String>()) }
    var isLoading by remember { mutableStateOf(false) }
    var pinnedEvents by remember { mutableStateOf<List<Event>>(emptyList()) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFCDD2), Color(0xFFFFF9C4)) // Dégradé de fond rose -> jaune clair
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "ISEN",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFD32F2F)
                )
                Text(
                    "Smart Companion",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF616161)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(responses) { response ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF8E1) // Jaune pâle
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Text(
                            text = response,
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFF3E2723)
                        )
                    }
                }

                items(pinnedEvents) { event ->
                    EventItemDisplay(event)
                }
            }

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = Color(0xFFD32F2F)
                )
            }

            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                placeholder = { Text("Posez votre question", color = Color.Gray) },
                trailingIcon = {
                    IconButton(onClick = {
                        if (question.isNotBlank()) {
                            scope.launch {
                                isLoading = true
                                val response = Gemini.getGeminiResponse(question)
                                if (response != null) {
                                    responses = responses + "User : $question\nBoat : $response"
                                    viewModel.recordInteraction(question, response)
                                    question = ""
                                } else {
                                    Toast.makeText(context, "Erreur IA", Toast.LENGTH_SHORT).show()
                                }
                                isLoading = false
                            }
                        } else {
                            Toast.makeText(context, "Veuillez écrire une question", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Envoyer",
                            tint = Color(0xFFD32F2F)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Red
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun EventItemDisplay(event: Event) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Optionnel : clic */ }
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE0F7FA)
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF80DEEA), Color(0xFFE0F7FA))
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
                    tint = Color(0xFF006064),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF004D40)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Date",
                    tint = Color(0xFF006064),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = event.date,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF004D40))
                )

                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Lieu",
                    tint = Color(0xFF006064),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = event.location,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF004D40))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = event.description,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF004D40)),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

