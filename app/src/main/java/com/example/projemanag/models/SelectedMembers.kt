package com.example.projemanag.models

import android.os.Parcel
import android.os.Parcelable

data class SelectedMembers(
    val id : String = "",
    val image: String = ""
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        writeString(id)
        writeString(image)
    }

    override fun describeContents() = 0

    /*
    companion object CREATOR : Parcelable.Creator<SelectedMembers> {
        override fun createFromParcel(parcel: Parcel): SelectedMembers {
            return SelectedMembers(parcel)
        }

        override fun newArray(size: Int): Array<SelectedMembers?> {
            return arrayOfNulls(size)
        }
    }
    */

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SelectedMembers> =
            object : Parcelable.Creator<SelectedMembers> {
                override fun createFromParcel(source: Parcel): SelectedMembers =
                    SelectedMembers(source)

                override fun newArray(size: Int): Array<SelectedMembers?> = arrayOfNulls(size)
            }
    }
}
