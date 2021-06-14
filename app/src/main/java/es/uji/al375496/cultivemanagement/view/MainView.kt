package es.uji.al375496.cultivemanagement.view

import android.view.View

interface MainView {
    fun onButton(view: View)
    fun enableButton()
    fun disableButton()
    fun startLoading()
    fun endLoading(sectors: String, subsectors: String)
    fun launchSectorSelectionActivity()
}