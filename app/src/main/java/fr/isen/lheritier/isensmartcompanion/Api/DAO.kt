package fr.isen.lheritier.isensmartcompanion.Api

import androidx.room.*
import fr.isen.lheritier.isensmartcompanion.data.Event
import fr.isen.lheritier.isensmartcompanion.data.Interaction
import kotlinx.coroutines.flow.Flow

@Dao
interface InteractionDao {

    @Query("SELECT * FROM interactions ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<Interaction>>

    @Query("SELECT * FROM interactions ORDER BY timestamp DESC")
    suspend fun getAll(): List<Interaction>

    @Query("SELECT * FROM interactions")
    fun getAllInteractions(): List<Interaction>

    @Insert
    suspend fun insert(interaction: Interaction)

    @Delete
    suspend fun delete(interaction: Interaction)

    @Query("DELETE FROM interactions")
    suspend fun deleteAll()
}
@Dao
interface EventDao {

    @Query("SELECT * FROM events")
    suspend fun getAllEvents(): List<Event>

    @Query("SELECT * FROM events")
    fun getAllEventsFlow(): Flow<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)  // Utilisation d'un conflit de remplacement pour éviter les duplications
    suspend fun insertEvent(event: Event)

    @Delete
    suspend fun delete(event: Event)

    @Query("SELECT * FROM events WHERE id = :eventId LIMIT 1")
    suspend fun getEventById(eventId: String): Event?

}

