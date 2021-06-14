package es.uji.al375496.cultivemanagement.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import es.uji.al375496.cultivemanagement.model.database.entities.Note
import es.uji.al375496.cultivemanagement.model.database.entities.Sector
import es.uji.al375496.cultivemanagement.model.database.entities.Subsector

@Database (entities = [Sector::class, Subsector::class, Note::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AbstractDatabase: RoomDatabase()
{
    abstract fun getDAO(): MyDAO
}