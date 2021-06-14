package es.uji.al375496.cultivemanagement.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import es.uji.al375496.cultivemanagement.model.database.DatabaseAccess
import es.uji.al375496.cultivemanagement.model.database.entities.Sector
import es.uji.al375496.cultivemanagement.model.database.entities.Subsector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainModel(): Parcelable
{
    var numberOfSectors: Int = 0
    var numberOfSubsectors: Int = 0
    private var performedInitTest: Boolean = false

    constructor(parcel: Parcel) : this() {
        numberOfSectors = parcel.readInt()
        numberOfSubsectors = parcel.readInt()
        performedInitTest = parcel.readInt() == 1
    }

    fun testInitValues(foundCallback: () -> Unit, notFoundCallback: (Int, Int) -> Unit, context: Context) {
        if (performedInitTest)
            notFoundCallback(numberOfSectors, numberOfSubsectors)
        else {
            DatabaseAccess.getInstance(context).getSectors {
                if (it.isNotEmpty())
                    foundCallback()
                else {
                    performedInitTest = true
                    notFoundCallback(numberOfSectors, numberOfSubsectors)
                }
            }
        }
    }

    fun trySetCurrentValues(sectors: Int?, subsectors: Int?) : Boolean {
        return if (sectors == null || sectors <= 0)
            false
        else {
            this.numberOfSectors = sectors
            this.numberOfSubsectors = subsectors ?: 0
            true
        }
    }

    fun initDatabase(callback: ()-> Unit, context: Context){
        val sectors = mutableListOf<Sector>()
        val subsectors = mutableListOf<Subsector>()
        GlobalScope.launch(Dispatchers.Main) {
            for (i in 1..numberOfSectors)
            {
                for (j in 1..numberOfSubsectors)
                    subsectors.add(Subsector(j,"", i))
                sectors.add(Sector(i, ""))
            }

            DatabaseAccess.getInstance(context).insertSectors({
                DatabaseAccess.getInstance(context).insertSubsectors(callback, subsectors)
            },sectors)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(numberOfSectors)
        parcel.writeInt(numberOfSubsectors)
        parcel.writeInt(if(performedInitTest) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainModel> {
        override fun createFromParcel(parcel: Parcel): MainModel {
            return MainModel(parcel)
        }

        override fun newArray(size: Int): Array<MainModel?> {
            return arrayOfNulls(size)
        }
    }
}