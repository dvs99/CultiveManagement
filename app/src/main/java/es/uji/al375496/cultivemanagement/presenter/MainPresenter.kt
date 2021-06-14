package es.uji.al375496.cultivemanagement.presenter

import android.content.Context
import es.uji.al375496.cultivemanagement.model.MainModel
import es.uji.al375496.cultivemanagement.view.MainView

class MainPresenter (private val view: MainView, val model: MainModel, private val context: Context)
{
    init {
        view.startLoading()
        model.testInitValues(::launchSectorSelectionActivity, ::endLoading, context)
    }

    fun setCurrentValues(sectors: String, subsectors: String){
        if (model.trySetCurrentValues(sectors.toIntOrNull(), subsectors.toIntOrNull()))
            view.enableButton()
        else
            view.disableButton()
    }

    fun setup(){
        view.startLoading()
        model.initDatabase(::launchSectorSelectionActivity, context)
    }

    private fun launchSectorSelectionActivity(){
        view.launchSectorSelectionActivity()
    }

    private fun endLoading(sectors: Int, subsectors: Int){
        view.endLoading(if (sectors>0) sectors.toString() else "", if (subsectors>0) subsectors.toString() else "")
    }
}