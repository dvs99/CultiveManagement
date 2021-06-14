package es.uji.al375496.cultivemanagement.model.database.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "subsectors",
    indices = [Index(value = ["sector_id"])],
    foreignKeys = [ForeignKey(entity = Sector::class, parentColumns = ["id"], childColumns = ["sector_id"])],
    primaryKeys = ["id", "sector_id"])
data class Subsector (val id: Int, var name: String, val sector_id: Int): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt()) {
    }

    override fun toString(): String {
        return if (name != "")
            "$id: $name"
        else
            id.toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(sector_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Subsector> {
        override fun createFromParcel(parcel: Parcel): Subsector {
            return Subsector(parcel)
        }

        override fun newArray(size: Int): Array<Subsector?> {
            return arrayOfNulls(size)
        }
    }
}
