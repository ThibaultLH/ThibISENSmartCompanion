package fr.isen.lheritier.isensmartcompanion.composable

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.isen.lheritier.isensmartcompanion.data.Interaction
import fr.isen.lheritier.isensmartcompanion.database.DatabaseManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class InteractionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DatabaseManager.getDatabase(application)

    fun recordInteraction(question: String, response: String) {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val interaction = Interaction(date = date, question = question, response = response)

        viewModelScope.launch(Dispatchers.IO) {
            db.interactionDao().insert(interaction)
        }
    }

}

