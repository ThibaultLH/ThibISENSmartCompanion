package fr.isen.lheritier.isensmartcompanion.composable

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.lheritier.isensmartcompanion.Api.Gemini
import kotlinx.coroutines.launch

@Composable
fun MainScreen(viewModel: InteractionViewModel, modifier: Modifier = Modifier) {
    var question by remember { mutableStateOf("") }
    var responses by remember { mutableStateOf(listOf<String>()) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Titre
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("ISEN", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                Text("Smart Companion", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
            }

            // Liste des réponses IA
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                items(responses) { response ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3))
                    ) {
                        Text(
                            text = response,
                            modifier = Modifier.padding(12.dp),
                            color = Color.Black
                        )
                    }
                }
            }

            // Barre de chargement
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Champ de texte + bouton d'envoi
            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                placeholder = { Text("Posez votre question") },
                trailingIcon = {
                    IconButton(onClick = {
                        if (question.isNotBlank()) {
                            scope.launch {
                                isLoading = true
                                val response = Gemini.getGeminiResponse(question)
                                if (response != null) {
                                    // Ajoute la question et la réponse dans la liste locale
                                    responses = responses + "User : $question\nBoat : $response"
                                    // Appeler le ViewModel pour enregistrer l'interaction dans la base de données
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
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Envoyer", tint = Color.Red)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
