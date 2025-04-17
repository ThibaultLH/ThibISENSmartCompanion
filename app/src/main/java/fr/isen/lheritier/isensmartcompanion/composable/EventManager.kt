package fr.isen.lheritier.isensmartcompanion.composable

import fr.isen.lheritier.isensmartcompanion.data.Event

object EventManager {
    private val _events = mutableListOf<Event>()
    val events: List<Event> get() = _events

    fun addEvent(event: Event) {
        if (!_events.any { it.id == event.id }) {
            _events.add(event)
        }
    }
}