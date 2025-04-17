package fr.isen.lheritier.isensmartcompanion.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fr.isen.lheritier.isensmartcompanion.Api.EventDao
import fr.isen.lheritier.isensmartcompanion.data.Interaction
import fr.isen.lheritier.isensmartcompanion.Api.InteractionDao
import fr.isen.lheritier.isensmartcompanion.Api.User
import fr.isen.lheritier.isensmartcompanion.Api.UserDao
import fr.isen.lheritier.isensmartcompanion.data.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Database(entities = [Interaction::class, User::class, Event::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun interactionDao(): InteractionDao
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "events-db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

suspend fun insertEventsToDatabase(context: Context, events: List<Event>) {
    val database = AppDatabase.getInstance(context)
    val eventDao = database.eventDao()

    withContext(Dispatchers.IO) {
        events.forEach { event ->
            val existingEvent = eventDao.getEventById(event.id)

            if (existingEvent == null) {
                eventDao.insertEvent(event)
            } else {
                eventDao.insertEvent(event)
            }
        }
    }
}
object DatabaseManager {

    private var db: AppDatabase? = null
    fun getDatabase(context: Context): AppDatabase {
        if (db == null) {
            db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).build()
        }
        return db!!
    }
}
