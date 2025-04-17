package fr.isen.lheritier.isensmartcompanion.composable

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.isen.lheritier.isensmartcompanion.Api.Gemini
import fr.isen.lheritier.isensmartcompanion.data.Event
import fr.isen.lheritier.isensmartcompanion.data.Interaction
import fr.isen.lheritier.isensmartcompanion.database.DatabaseManager
import fr.isen.lheritier.isensmartcompanion.ui.theme.ISENSmartCompanionTheme
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

    fun handleUserMessage(question: String) {
        viewModelScope.launch {
            val response = Gemini.getGeminiResponse(question)

            response?.let {
                recordInteraction(question, it)
            }
        }
    }
}

class EventDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val event: Event? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("event", Event::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("event")
        }
        NotificationHelper.createNotificationChannel(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }

        setContent {
            ISENSmartCompanionTheme {
                Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->
                    EventDetailScreen(event, innerPadding)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(event: Event?, innerPadding: PaddingValues) {
    val context = LocalContext.current

    if (event != null) {
        val eventId = event.id
        val notificationId = eventId.hashCode()

        var isNotificationEnabled by remember {
            mutableStateOf(PreferencesManager.isNotificationEnabled(context, eventId))
        }

        var handler: Handler? = null

        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineMedium
                )

                IconButton(onClick = {
                    isNotificationEnabled = !isNotificationEnabled
                    PreferencesManager.setNotificationEnabled(
                        context,
                        eventId,
                        isNotificationEnabled
                    )

                    if (isNotificationEnabled) {
                        handler = Handler(Looper.getMainLooper()).apply {
                            postDelayed({
                                if (isNotificationEnabled) {
                                    Toast.makeText(
                                        context,
                                        "Les notifications pour l'événement ${event.title} ont été activées.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    NotificationHelper.showEventNotification(
                                        context,
                                        "Notification activée : ${event.title}",
                                        "Vous recevrez des notifications pour cet événement.",
                                        notificationId
                                    )
                                }
                            }, 10_000)
                        }
                    } else {
                        handler?.removeCallbacksAndMessages(null)
                        NotificationHelper.cancelNotification(context, notificationId)
                    }
                }) {
                    Icon(
                        imageVector = if (isNotificationEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                        contentDescription = if (isNotificationEnabled) "Désactiver la notification" else "Activer la notification",
                        tint = if (isNotificationEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.Companion.padding(bottom = 8.dp)
            )
        }
    } else {
        Text(text = "Événement non trouvé")
    }
}