package com.dnbjkewbwqe.testv.beans

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep
import com.dnbjkewbwqe.testv.application
import com.dnbjkewbwqe.testv.utils.ServerManager


@Keep
data class TestServer(
    val testip: String = "",
    val testiy: String = "",
    val testme: String = "",
    val testord: String = "",
    val testpo: Int = 0,
    val testtry: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: ""
    ) {
    }

    override fun equals(other: Any?): Boolean {
        if (other == null)
            return false
        if ((other is TestServer).not())
            return false
        val another = other as TestServer
        return (another.testip == this.testip && another.testiy == this.testiy && another.testme == this.testme && another.testord == this.testord
                && another.testpo == this.testpo && another.testtry == this.testtry)
    }

    fun flagResourceId() : Int{
        return ServerManager.getFlagImgByServer(application,this)
    }

    fun fullName() : String {
        return if(this.testiy!="")
            "${this.testtry}-${this.testiy}"
        else this.testtry
    }

    override fun hashCode(): Int {
        var result = testip.hashCode()
        result = 31 * result + testiy.hashCode()
        result = 31 * result + testme.hashCode()
        result = 31 * result + testord.hashCode()
        result = 31 * result + testpo
        result = 31 * result + testtry.hashCode()
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(testip)
        parcel.writeString(testiy)
        parcel.writeString(testme)
        parcel.writeString(testord)
        parcel.writeInt(testpo)
        parcel.writeString(testtry)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TestServer> {
        override fun createFromParcel(parcel: Parcel): TestServer {
            return TestServer(parcel)
        }

        override fun newArray(size: Int): Array<TestServer?> {
            return arrayOfNulls(size)
        }
    }
}