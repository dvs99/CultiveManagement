package es.uji.al375496.cultivemanagement.presenter

import android.content.Context
import es.uji.al375496.cultivemanagement.model.SectorSelectionModel
import es.uji.al375496.cultivemanagement.model.database.entities.Sector
import es.uji.al375496.cultivemanagement.model.database.entities.Subsector
import es.uji.al375496.cultivemanagement.view.SectorSelectionView
import kotlinx.coroutines.*

class SectorSelectionPresenter (private val view: SectorSelectionView, val model: SectorSelectionModel, private val context: Context)
{
    fun setup(sectorText: String?, subsectorText: String?) {
        //add the list to the sector autocomplete view and fill up any fields if they are setup
        view.loading = true

        val modelSectorText = model.selectedSector
        val modelSubsectorText = model.selectedSubsector

        model.getSectors ({ sectors ->
            view.populateSectors(sectors)
            view.loading = false

            sectorText?.let { sectorString ->
                //find the string for the selected sector just in case it has changed
                if (modelSectorText != null)
                    view.fixSectorName = true
                view.setSectorText(sectorString)

                //busy wait in a coroutine until the subsector can be input
                GlobalScope.launch(Dispatchers.Main){
                    val value = GlobalScope.async { // creates worker thread
                        withContext(Dispatchers.Default) {
                            var done = false
                            while (!done)
                                done = view.visibleSubsector
                        }
                    }
                    value.await()
                    subsectorText?.let { subsectorString ->
                        if (modelSubsectorText != null)
                            view.fixSubsectorName = true
                        view.setSubsectorText(subsectorString)
                    }
                }
            }

        }, context)
    }

    fun onSectorSelected(sector: Sector) {
        view.loading = true
        model.selectedSector = sector
        view.enabledSectorButton = true

        //add the list to the subsector autocomplete view
        model.getSubsectors({ subsectors->
            view.loading = false
            if (subsectors.isNotEmpty()){
                view.populateSubsectors(subsectors)
                view.visibleSubsector = true
            }
        }, sector, context)
    }

    fun onSectorDeselected() {
        view.setSubsectorText("")
        model.selectedSector = null
        model.selectedSubsector = null
        view.visibleSubsector = false
        view.enabledSectorButton = false
        view.enabledSubsectorButton = false
    }

    fun onSubsectorSelected(subsector: Subsector) {
        model.selectedSubsector = subsector
        view.enabledSubsectorButton = true
    }

    fun onSubsectorDeselected() {
        model.selectedSubsector = null
        view.enabledSubsectorButton = false
    }

    fun onAllButton() {
        view.launchShowNotesActivity(null, null)
    }

    fun onSectorButton() {
        if (model.selectedSector != null) {
            view.launchShowNotesActivity(model.selectedSector, null)
        }
    }

    fun onSubsectorButton() {
        if (model.selectedSector != null && model.selectedSubsector != null) {
            view.launchShowNotesActivity(model.selectedSector, model.selectedSubsector)
        }
    }
}