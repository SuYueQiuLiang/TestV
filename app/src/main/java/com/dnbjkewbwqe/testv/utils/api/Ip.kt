package com.dnbjkewbwqe.testv.utils.api

import com.dnbjkewbwqe.testv.beans.IpInfo
import retrofit2.http.GET

interface Ip {
    @GET("https://ip.seeip.org/geoip/")
    suspend fun getIpInfo() : IpInfo
}