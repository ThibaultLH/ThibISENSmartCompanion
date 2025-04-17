package fr.isen.lheritier.isensmartcompanion.composable

import android.content.Context
import android.content.SharedPreferences

object PreferencesManager {
    private const val PREFS_NAME = "event_notifications"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isNotificationEnabled(context: Context, eventId: String): Boolean {
        return getPrefs(context).getBoolean(eventId, false)
    }

    fun setNotificationEnabled(context: Context, eventId: String, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(eventId, enabled).apply()
    }

    fun getEnabledEventIds(context: Context): List<String> {
        // Récupère toutes les clés dans les préférences qui ont une valeur "true"
        return getPrefs(context).all
            .filter { it.value == true }  // Filtre pour ne garder que celles où la notification est activée
            .keys.toList()  // Liste des IDs des événements
    }
}
