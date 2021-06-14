package es.uji.al375496.cultivemanagement.model.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "notes",
        foreignKeys = [ForeignKey(entity = Subsector::class, parentColumns = ["id", "sector_id"], childColumns = ["subsector_id","sector_id"])])
data class Note (val subsector_id: Int, val sector_id: Int, val title: String, val text: String, var completed: Boolean, val creation_timestamp: Date, val reminder_timestamp: Date?, val imgUri: String?){
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0
}