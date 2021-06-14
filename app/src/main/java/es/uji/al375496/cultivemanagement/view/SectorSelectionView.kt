package es.uji.al375496.cultivemanagement.view

import android.view.View
import es.uji.al375496.cultivemanagement.model.database.entities.Sector
import es.uji.al375496.cultivemanagement.model.database.entities.Subsector

interface SectorSelectionView {
    var fixSectorName: Boolean
    var fixSubsectorName: Boolean
    var enabledSectorButton: Boolean
    var enabledSubsectorButton: Boolean
    var visibleSubsector: Boolean
    var loading: Boolean

    fun onGoButton(view: View)

    fun populateSectors(sectors: List<Sector>)
    fun populateSubsectors(subsectors: List<Subsector>)
    fun setSectorText(s: String)
    fun setSubsectorText(s: String)
    fun launchShowNotesActivity(sector: Sector?, subsector: Subsector?)
}