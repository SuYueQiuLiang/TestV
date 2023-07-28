package com.dnbjkewbwqe.testv.beans

import android.os.Parcel
import android.os.Parcelable

data class Referrer(
    val referrer: String
) : Parcelable {

    val payed get() = referrer != "Organic"

    val FB get() = (referrer == "fb4a") || (referrer == "facebook")

    constructor(parcel: Parcel) : this(parcel.readString() ?: "Organic") {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(referrer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Referrer> {
        override fun createFromParcel(parcel: Parcel): Referrer {
            return Referrer(parcel)
        }

        override fun newArray(size: Int): Array<Referrer?> {
            return arrayOfNulls(size)
        }
    }

}
