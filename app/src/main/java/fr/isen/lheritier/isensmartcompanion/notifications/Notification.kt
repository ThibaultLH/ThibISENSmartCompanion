package fr.isen.lheritier.isensmartcompanion.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.isensmartcompanion.R // Assure-toi que l'icône ic_notification existe bien dans ton projet

object NotificationHelper {
    private const val CHANNEL_ID = "event_notifications"
    private const val CHANNEL_NAME = "Événements"
    private const val CHANNEL_DESCRIPTION = "Notifications pour les événements"

    // Crée un canal de notification (nécessaire pour Android 8.0 et supérieur)
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Affiche une notification
    fun showEventNotification(context: Context, title: String, content: String, notificationId: Int) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Assure-toi que l'icône existe
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Permet de fermer la notification quand l'utilisateur la touche

        // Envoie la notification
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    // Annule une notification en utilisant son ID
    fun cancelNotification(context: Context, notificationId: Int) {
        // Utilise NotificationManagerCompat pour plus de compatibilité
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
}
