package es.uji.al375496.cultivemanagement.model.database

import androidx.room.*
import es.uji.al375496.cultivemanagement.model.database.entities.Note
import es.uji.al375496.cultivemanagement.model.database.entities.Sector
import es.uji.al375496.cultivemanagement.model.database.entities.Subsector

@Dao
interface MyDAO {
    @Insert
    fun insertSectors(sectors: List<Sector>)

    @Insert
    fun insertSubsectors(subsectors: List<Subsector>)

    @Insert
    fun insertNote(note: Note) : Long

    @Query
    ("SELECT * FROM sectors ORDER BY id")
    fun getSectors(): List<Sector>

    @Query
    ("SELECT * FROM subsectors WHERE sector_id =:sectorId ORDER BY id")
    fun getSubsectors(sectorId: Int): List<Subsector>

    @Query
    ("SELECT * FROM notes WHERE sector_id =:sectorId AND subsector_id=:subsectorId ORDER BY creation_timestamp DESC")
    fun getNotes(sectorId: Int, subsectorId: Int): List<Note>

    @Query
    ("SELECT * FROM notes WHERE sector_id =:sectorId ORDER BY creation_timestamp DESC")
    fun getNotes(sectorId: Int): List<Note>

    @Query
    ("SELECT * FROM notes ORDER BY creation_timestamp DESC")
    fun getNotes(): List<Note>

    @Update
    fun updateSector(sector: Sector)

    @Update
    fun updateSubsector(subsector: Subsector)

    @Update
    fun updateNote(note: Note)

    @Delete
    fun deleteNote(note: Note)
}