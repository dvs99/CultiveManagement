package es.uji.al375496.cultivemanagement.model

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import es.uji.al375496.cultivemanagement.*
import es.uji.al375496.cultivemanagement.model.database.DatabaseAccess
import es.uji.al375496.cultivemanagement.model.database.entities.Note
import es.uji.al375496.cultivemanagement.model.database.entities.Sector
import es.uji.al375496.cultivemanagement.model.database.entities.Subsector
import java.util.*

class ShowNotesModel() : Parcelable
{
    var sector: Sector? = null
    var subsector: Subsector? = null
    var showPending = true

    constructor(parcel: Parcel) : this() {
        showPending = parcel.readInt() == 1
        sector = parcel.readParcelable(Sector::class.java.classLoader)
        subsector = parcel.readParcelable(Subsector::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(if(showPending) 1 else 0)
        parcel.writeParcelable(sector, flags)
        parcel.writeParcelable(subsector, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun rename(string: String, context: Context, callback: (String) -> Unit) {
        val databaseAccess = DatabaseAccess.getInstance(context)
        when {
            subsector != null -> {
                subsector!!.name = string
                databaseAccess.updateSubsector({
                    callback("Subsector $subsector (Sector $sector)")
                }, subsector!!)
            }
            sector != null -> {
                sector!!.name = string
                databaseAccess.updateSector({
                    callback("Sector $sector")
                },sector!!)
            }
            else -> callback("")
        }
    }

    fun getNotes(context: Context, callback: (List<Note>) -> Unit) {
        val databaseAccess = DatabaseAccess.getInstance(context)
        when {
            subsector != null &&  sector != null-> databaseAccess.getNotes(callback, sector!!.id, subsector!!.id)
            sector != null -> databaseAccess.getNotes(callback, sector!!.id)
            else -> databaseAccess.getNotes(callback)
        }
    }

    fun addNote(note: Note, context: Context, callback: () -> Unit) {
        DatabaseAccess.getInstance(context).insertNote({
            if (note.reminder_timestamp != null) {
                //Schedule the Intent that shows the notification as an Alarm
                val alarmMgr: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, AlarmReceiver::class.java)
                intent.putExtra(TITLE, if(note.title!="") note.title else "untitled")
                intent.putExtra(TEXT, note.text)
                intent.putExtra(ID, it.toInt())
                val pendingIntent = PendingIntent.getBroadcast(context, it.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

                // Set the alarm to start at reminder time
                val calendar: Calendar = Calendar.getInstance().apply { time = note.reminder_timestamp }
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
            callback()
        } , note)
    }

    fun delete(note: Note, context: Context, callback: () -> Unit) {
        //delete possible scheduled Alarm by substituting it using a new one with the same id and cancelling it
        val alarmMgr: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, note.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val calendar: Calendar = Calendar.getInstance().apply { time = Date(Calendar.getInstance().timeInMillis + 100000) }
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        alarmMgr.cancel(pendingIntent)

        DatabaseAccess.getInstance(context).deleteNote(callback, note)
    }

    fun complete(note: Note, context: Context, callback: () -> Unit) {
        note.completed = true
        DatabaseAccess.getInstance(context).updateNote(callback, note)
    }


    companion object CREATOR : Parcelable.Creator<ShowNotesModel> {
        override fun createFromParcel(parcel: Parcel): ShowNotesModel {
            return ShowNotesModel(parcel)
        }

        override fun newArray(size: Int): Array<ShowNotesModel?> {
            return arrayOfNulls(size)
        }
    }
}