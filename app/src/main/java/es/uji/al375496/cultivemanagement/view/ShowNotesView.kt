package es.uji.al375496.cultivemanagement.view

import android.view.View
import es.uji.al375496.cultivemanagement.model.MainModel
import es.uji.al375496.cultivemanagement.model.database.entities.Note

interface ShowNotesView {
    var loading: Boolean
    var canRename: Boolean
    var canAdd: Boolean
    var showPending: Boolean

    fun onRename(view : View)
    fun onAdd(view : View)
    fun onSwitchRecyclerView(view : View)
    fun populateRecycleViews(pendingNotes: List<Note>, completeNotes: List<Note>)
    fun setTitle(string: String)

    fun restoreDialog(info: ParcelableDialogInfo)
}