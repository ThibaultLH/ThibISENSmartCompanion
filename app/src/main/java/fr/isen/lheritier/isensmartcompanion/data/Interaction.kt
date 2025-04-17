package fr.isen.lheritier.isensmartcompanion.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "interactions")
data class Interaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "question") val question: String,
    @ColumnInfo(name = "response") val response: String,
    @ColumnInfo(name = "timestamp") val date: String  // ou Long, selon ton besoin
)