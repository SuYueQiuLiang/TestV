package com.dnbjkewbwqe.testv.beans

import androidx.annotation.Keep

@Keep
data class IpInfo(
    val asn: Int,
    val city: String,
    val continent_code: String,
    val country: String,
    val country_code: String,
    val country_code3: String,
    val ip: String,
    val latitude: Double,
    val longitude: Double,
    val offset: Int,
    val organization: String,
    val region: String,
    val region_code: String,
    val timezone: String
)