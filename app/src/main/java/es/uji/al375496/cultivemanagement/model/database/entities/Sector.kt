package es.uji.al375496.cultivemanagement.model.database.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sectors")
data class Sector (@PrimaryKey val id: Int, var name: String): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!
    )

    override fun toString(): String {
        return if (name != "")
            "$id: $name"
        else
            id.toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Sector> {
        override fun createFromParcel(parcel: Parcel): Sector {
            return Sector(parcel)
        }

        override fun newArray(size: Int): Array<Sector?> {
            return arrayOfNulls(size)
        }
    }
}
