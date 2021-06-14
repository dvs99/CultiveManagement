package es.uji.al375496.cultivemanagement.presenter

import android.content.Context
import java.util.Calendar
import es.uji.al375496.cultivemanagement.model.ShowNotesModel
import es.uji.al375496.cultivemanagement.model.database.entities.Note
import es.uji.al375496.cultivemanagement.model.database.entities.Sector
import es.uji.al375496.cultivemanagement.model.database.entities.Subsector
import es.uji.al375496.cultivemanagement.view.ShowNotesView

class ShowNotesPresenter (private val view: ShowNotesView, val model: ShowNotesModel, private val context: Context)
{
    init {
        view.showPending = model.showPending
    }

    fun setup(){
        view.loading = true
        view.canAdd = false
        view.canRename = false
        model.sector = null
        model.subsector = null
        model.getNotes(context, ::lateSetup)
    }

    fun setup(sector: Sector){
        view.loading = true
        view.canAdd = false
        view.canRename = true
        model.sector = sector
        model.subsector = null
        view.setTitle("Sector $sector")
        model.getNotes(context, ::lateSetup)

    }

    fun setup(sector: Sector, subsector: Subsector){
        view.loading = true
        view.canAdd = true
        view.canRename = true
        model.sector = sector
        model.subsector = subsector
        view.setTitle("Subsector $subsector (Sector $sector)")
        model.getNotes(context, ::lateSetup)

    }

    private fun lateSetup(notes : List<Note>){
        val grouped = notes.groupBy { it.completed }.toMutableMap()

        if (grouped[true] == null)
            grouped[true] = listOf()
        if (grouped[false] == null)
            grouped[false] = listOf()

        view.populateRecycleViews(grouped[false]!!,grouped[true]!!)
        view.loading = false
        view.showPending = model.showPending
    }

    //renames current sector or subsector
    fun rename(string: String) {
        view.loading = true
        model.rename(string, context) {
            view.setTitle(it)
            view.loading = false
        }
    }

    fun deleteNote(note: Note) {
        view.loading = true
        model.delete(note, context) {
            model.getNotes(context, ::lateSetup)
        }
    }

    fun completeNote(note: Note) {
        view.loading = true
        model.complete(note, context) {
            model.getNotes(context, ::lateSetup)
        }
    }

    fun switchRecyclerView() {
        view.showPending = !view.showPending
        model.showPending = !model.showPending
    }

    fun addNote(title: String, text: String, image: String?) {
        view.loading = true
        if (model.sector != null && model.subsector != null)
        view.loading = true
        model.addNote(Note(model.subsector!!.id, model.sector!!.id, title, text, false, Calendar.getInstance().time,null, image), context){
            model.getNotes(context, ::lateSetup)
        }
    }
}