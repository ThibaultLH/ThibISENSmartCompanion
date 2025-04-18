package fr.isen.lheritier.isensmartcompanion.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import fr.isen.lheritier.isensmartcompanion.composable.NotificationHelper
import fr.isen.lheritier.isensmartcompanion.composable.PreferencesManager
import fr.isen.lheritier.isensmartcompanion.data.Event
import fr.isen.lheritier.isensmartcompanion.ui.theme.ISENSmartCompanionTheme

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

        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { padding ->
            // Dégradé de fond
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Icône Notifications
                        IconButton(onClick = {
                            isNotificationEnabled = !isNotificationEnabled
                            PreferencesManager.setNotificationEnabled(context, eventId, isNotificationEnabled)

                            if (isNotificationEnabled) {
                                handler = Handler(Looper.getMainLooper()).apply {
                                    postDelayed({
                                        if (isNotificationEnabled) {
                                            Toast.makeText(
                                                context,
                                                "Notifications activées pour ${event.title}.",
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
                                contentDescription = "Gestion notifications",
                                tint = if (isNotificationEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        // Titre
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Sous-titre
                        Text(
                            text = "Description de l'événement",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        // Description
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    } else {
        // Cas Event null
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Événement non trouvé",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
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


