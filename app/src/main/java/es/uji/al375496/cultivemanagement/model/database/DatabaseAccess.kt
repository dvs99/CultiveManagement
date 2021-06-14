package es.uji.al375496.cultivemanagement.model.database

import android.content.Context
import androidx.room.*
import es.uji.al375496.cultivemanagement.model.database.entities.Note
import es.uji.al375496.cultivemanagement.model.database.entities.Sector
import es.uji.al375496.cultivemanagement.model.database.entities.Subsector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DatabaseAccess private constructor(context: Context)
{
    companion object: SingletonHolder<DatabaseAccess, Context>(::DatabaseAccess)

    private val dao: MyDAO

    init
    {
        val db = Room.databaseBuilder(context, AbstractDatabase::class.java,"database").build()
        dao = db.getDAO()
    }

    fun insertSectors(callback: () -> Unit, sectors: List<Sector>){
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                dao.insertSectors(sectors)
            }
            callback()
        }
    }

    fun insertSubsectors(callback: () -> Unit, subsectors: List<Subsector>){
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                dao.insertSubsectors(subsectors)
            }
            callback()
        }
    }

    fun insertNote(callback: (Long) -> Unit, note: Note){
        GlobalScope.launch(Dispatchers.Main) {
            var id: Long
            withContext(Dispatchers.IO) {
                id = dao.insertNote(note)
            }
            callback(id)
        }
    }

    fun getSectors(callback: (List<Sector>) -> Unit){
        GlobalScope.launch(Dispatchers.Main) {
            val sectors = withContext(Dispatchers.IO) {
                dao.getSectors()
            }
            callback(sectors)
        }
    }

    fun getSubsectors(callback: (List<Subsector>) -> Unit, sectorId: Int){
        GlobalScope.launch(Dispatchers.Main) {
            val subsectors = withContext(Dispatchers.IO) {
                dao.getSubsectors(sectorId)
            }
            callback(subsectors)
        }
    }

    fun getNotes(callback: (List<Note>) -> Unit, sectorId: Int, subsectorId: Int){
        GlobalScope.launch(Dispatchers.Main) {
            val notes = withContext(Dispatchers.IO) {
                dao.getNotes(sectorId, subsectorId)
            }
            callback(notes)
        }
    }

    fun getNotes(callback: (List<Note>) -> Unit, sectorId: Int){
        GlobalScope.launch(Dispatchers.Main) {
            val notes = withContext(Dispatchers.IO) {
                dao.getNotes(sectorId)
            }
            callback(notes)
        }
    }

    fun getNotes(callback: (List<Note>) -> Unit){
        GlobalScope.launch(Dispatchers.Main) {
            val notes = withContext(Dispatchers.IO) {
                dao.getNotes()
            }
            callback(notes)
        }
    }


    fun updateSector(callback: () -> Unit, sector: Sector){
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                dao.updateSector(sector)
            }
            callback()
        }
    }

    fun updateSubsector(callback: () -> Unit, subsector: Subsector){
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                dao.updateSubsector(subsector)
            }
            callback()
        }
    }

    fun updateNote(callback: () -> Unit, note: Note){
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                dao.updateNote(note)
            }
            callback()
        }
    }

    fun deleteNote(callback: () -> Unit, note: Note){
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                dao.deleteNote(note)
            }
            callback()
        }
    }
}
