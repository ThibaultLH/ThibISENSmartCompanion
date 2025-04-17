package fr.isen.lheritier.isensmartcompanion.Api

import androidx.room.*
import fr.isen.lheritier.isensmartcompanion.data.Event
import kotlinx.coroutines.flow.Flow

// DAO pour les interactions
@Dao
interface InteractionDao {

    // Retourne un Flow de toutes les interactions, triées par timestamp décroissant
    @Query("SELECT * FROM interactions ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<Interaction>>

    // Retourne toutes les interactions sous forme de liste bloquante
    @Query("SELECT * FROM interactions ORDER BY timestamp DESC")
    suspend fun getAll(): List<Interaction>

    // Méthode redondante, pourrait être supprimée si "getAll()" suffit
    @Query("SELECT * FROM interactions")
    fun getAllInteractions(): List<Interaction>

    // Insère une nouvelle interaction dans la base
    @Insert
    suspend fun insert(interaction: Interaction)

    // Supprime une interaction
    @Delete
    suspend fun delete(interaction: Interaction)

    // Supprime toutes les interactions
    @Query("DELETE FROM interactions")
    suspend fun deleteAll()
}

@Dao
interface EventDao {

    // Retourne tous les événements sous forme de liste bloquante
    @Query("SELECT * FROM events")
    suspend fun getAllEvents(): List<Event>

    // Retourne un Flow qui permet de suivre les événements en temps réel
    @Query("SELECT * FROM events")
    fun getAllEventsFlow(): Flow<List<Event>>

    // Insère un événement dans la base de données
    @Insert(onConflict = OnConflictStrategy.REPLACE)  // Utilisation d'un conflit de remplacement pour éviter les duplications
    suspend fun insertEvent(event: Event)

    // Supprime un événement
    @Delete
    suspend fun delete(event: Event)

    // Retourne un événement spécifique par son ID
    @Query("SELECT * FROM events WHERE id = :eventId LIMIT 1")
    suspend fun getEventById(eventId: String): Event?

}

