package fr.isen.lheritier.isensmartcompanion.Api

import androidx.room.*
import fr.isen.lheritier.isensmartcompanion.data.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface InteractionDao {

    // Retourne un Flow de List<Interaction> pour les observateurs
    @Query("SELECT * FROM interactions ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<Interaction>>

    // Retourne une List<Interaction> suspendue
    @Query("SELECT * FROM interactions ORDER BY timestamp DESC")
    suspend fun getAll(): List<Interaction>

    // Retourne une List<Interaction> sans coroutine
    @Query("SELECT * FROM interactions")
    fun getAllInteractions(): List<Interaction>

    // Insère une interaction dans la base de données
    @Insert
    suspend fun insert(interaction: Interaction)

    @Delete
    suspend fun delete(interaction: Interaction)

    @Query("DELETE FROM interactions")
    suspend fun deleteAll()
}
@Dao
interface EventDao {

    @Insert
    suspend fun insert(event: Event)

    @Query("SELECT * FROM events")
    fun getAllEvents(): Flow<List<Event>>
}