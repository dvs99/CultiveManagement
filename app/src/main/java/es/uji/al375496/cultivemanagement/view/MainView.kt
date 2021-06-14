package es.uji.al375496.cultivemanagement.view

import android.view.View
import es.uji.al375496.cultivemanagement.model.MainModel

interface MainView {
    fun onButton(view: View)
    fun enableButton()
    fun launchSectorSelectionActivity()
    fun startLoading()
    fun endLoading(sectors: String, subsectors: String)
    fun disableButton()
}