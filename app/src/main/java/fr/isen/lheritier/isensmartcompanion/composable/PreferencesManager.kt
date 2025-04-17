package fr.isen.lheritier.isensmartcompanion.composable

import android.content.Context
import android.content.SharedPreferences
import fr.isen.lheritier.isensmartcompanion.data.Event
import androidx.core.content.edit

object PreferencesManager {
    private const val PREFS_NAME = "event_notifications"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isNotificationEnabled(context: Context, eventId: String): Boolean {
        return getPrefs(context).getBoolean(eventId, false)
    }

    fun setNotificationEnabled(context: Context, eventId: String, enabled: Boolean) {
        getPrefs(context).edit { putBoolean(eventId, enabled) }
    }

    fun getEnabledEventIds(context: Context): List<String> {
        return getPrefs(context).all
            .filter { it.value == true }
            .keys.toList()
    }

    fun getEnabledEvents(context: Context, allEvents: List<Event>): List<Event> {
        val enabledIds = getEnabledEventIds(context)
        //Log.d("PreferencesManager", "Pref Événements activés: $enabledIds")
        //Log.d("PreferencesManager", "Pref Tous les événements: $allEvents")
        return allEvents.filter { it.id in enabledIds }
    }
}
