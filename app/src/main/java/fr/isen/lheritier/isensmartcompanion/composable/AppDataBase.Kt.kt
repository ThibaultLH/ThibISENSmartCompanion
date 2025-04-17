package fr.isen.lheritier.isensmartcompanion.composable

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fr.isen.lheritier.isensmartcompanion.Api.Interaction
import fr.isen.lheritier.isensmartcompanion.Api.InteractionDao
import fr.isen.lheritier.isensmartcompanion.Api.User
import fr.isen.lheritier.isensmartcompanion.Api.UserDao

@Database(entities = [Interaction::class, User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun interactionDao(): InteractionDao
}

object DatabaseManager {

    private var db: AppDatabase? = null

    // Fonction pour obtenir une instance de la base de données
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

