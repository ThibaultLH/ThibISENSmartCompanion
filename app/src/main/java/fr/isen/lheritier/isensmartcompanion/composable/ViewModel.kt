package fr.isen.lheritier.isensmartcompanion.composable

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.isen.lheritier.isensmartcompanion.Api.Gemini
import fr.isen.lheritier.isensmartcompanion.Api.Interaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class InteractionViewModel(application: Application) : AndroidViewModel(application) {

    // Obtenir l'instance de la base de données à partir du contexte d'application
    private val db = DatabaseManager.getDatabase(application)

    // Fonction pour enregistrer une interaction dans la base de données
    fun recordInteraction(question: String, response: String) {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val interaction = Interaction(date = date, question = question, response = response)

        // Sauvegarde dans la base de données en arrière-plan
        viewModelScope.launch(Dispatchers.IO) {
            db.interactionDao().insert(interaction)
        }
    }

    // Fonction pour gérer l'interaction avec Gemini (chatbot)
    fun handleUserMessage(question: String) {
        viewModelScope.launch {
            // Appeler Gemini pour obtenir une réponse
            val response = Gemini.getGeminiResponse(question)

            // Si une réponse est reçue, enregistrez l'interaction dans la base de données
            response?.let {
                recordInteraction(question, it)
            }
        }
    }
}
