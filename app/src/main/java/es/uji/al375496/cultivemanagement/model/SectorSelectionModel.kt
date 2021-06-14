package es.uji.al375496.cultivemanagement.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import es.uji.al375496.cultivemanagement.model.database.DatabaseAccess
import es.uji.al375496.cultivemanagement.model.database.entities.Sector
import es.uji.al375496.cultivemanagement.model.database.entities.Subsector

class SectorSelectionModel(): Parcelable
{
    var selectedSector: Sector? = null
    var selectedSubsector: Subsector? = null

    constructor(parcel: Parcel) : this() {
        selectedSector = parcel.readParcelable(Sector::class.java.classLoader)
        selectedSubsector = parcel.readParcelable(Subsector::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(selectedSector, flags)
        parcel.writeParcelable(selectedSubsector, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun getSectors(callback: (List<Sector>) -> Unit, context: Context) {
        DatabaseAccess.getInstance(context).getSectors(callback)
    }

    fun getSubsectors(callback: (List<Subsector>) -> Unit, sector: Sector, context: Context) {
        DatabaseAccess.getInstance(context).getSubsectors(callback, sector.id)
    }

    companion object CREATOR : Parcelable.Creator<SectorSelectionModel> {
        override fun createFromParcel(parcel: Parcel): SectorSelectionModel {
            return SectorSelectionModel(parcel)
        }

        override fun newArray(size: Int): Array<SectorSelectionModel?> {
            return arrayOfNulls(size)
        }
    }
}