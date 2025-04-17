package fr.isen.lheritier.isensmartcompanion.data

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.isen.lheritier.isensmartcompanion.Api.Interaction
import fr.isen.lheritier.isensmartcompanion.composable.AppDatabase
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(database: AppDatabase) {
    val scope = rememberCoroutineScope()

    // Collecte des interactions à partir de la base de données
    val interactions by database.interactionDao().getAllFlow().collectAsState(initial = emptyList<Interaction>())

    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Historique") },
            actions = {
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer tout")
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(interactions) { interaction ->
                InteractionItem(
                    interaction = interaction,
                    onDelete = {
                        scope.launch {
                            database.interactionDao().delete(interaction)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    // Dialogue de confirmation pour supprimer tout l'historique
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmation") },
            text = { Text("Voulez-vous supprimer tout l'historique ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            // Suppression de toutes les interactions
                            database.interactionDao().deleteAll()
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Confirmer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractionItem(interaction: Interaction, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = interaction.date, // Assure-toi que `date` est une propriété de `Interaction`
                    style = MaterialTheme.typography.bodySmall
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer")
                }
            }

            Text(
                text = "User: ${interaction.question}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = "Boat: ${interaction.response}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

