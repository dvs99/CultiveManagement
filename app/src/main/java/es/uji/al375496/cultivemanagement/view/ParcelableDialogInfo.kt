package es.uji.al375496.cultivemanagement.view

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import java.util.*

class ParcelableDialogInfo(val title: String, val text: String, var image: Bitmap?, var reminder: Date?): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(Bitmap::class.java.classLoader)!!,
        if (Date(parcel.readLong()) != Date(0L))
            Date(parcel.readLong())
        else
            null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(text)
        parcel.writeParcelable(image, flags)
        if (reminder != null)
            parcel.writeLong(reminder!!.time)
        if (reminder != null)
            parcel.writeLong(reminder!!.time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParcelableDialogInfo> {
        override fun createFromParcel(parcel: Parcel): ParcelableDialogInfo {
            return ParcelableDialogInfo(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableDialogInfo?> {
            return arrayOfNulls(size)
        }
    }

}