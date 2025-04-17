package fr.isen.lheritier.isensmartcompanion

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import fr.isen.lheritier.isensmartcompanion.data.Event
import fr.isen.lheritier.isensmartcompanion.notifications.NotificationHelper
import fr.isen.lheritier.isensmartcompanion.ui.theme.ISENSmartCompanionTheme
import fr.isen.lheritier.isensmartcompanion.composable.PreferencesManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

class EventDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val event: Event? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("event", Event::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("event")
        }
        NotificationHelper.createNotificationChannel(this)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }

        setContent {
            ISENSmartCompanionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineMedium
                )

                IconButton(onClick = {
                    isNotificationEnabled = !isNotificationEnabled
                    PreferencesManager.setNotificationEnabled(context, eventId, isNotificationEnabled)

                    if (isNotificationEnabled) {
                        handler = Handler(Looper.getMainLooper()).apply {
                            postDelayed({
                                if (isNotificationEnabled) {
                                    Toast.makeText(
                                        context,
                                        "Les notifications pour l'événement ${event.title} ont été activées.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // Afficher la notification confirmant que l'utilisateur recevra des notifications
                                    NotificationHelper.showEventNotification(
                                        context,
                                        "Notification activée : ${event.title}",
                                        "Vous recevrez des notifications pour cet événement.",
                                        notificationId
                                    )
                                }
                            }, 10_000) // 10 secondes d'attente
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

            // Description de l'événement
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    } else {
        Text(text = "Événement non trouvé")
    }
}
