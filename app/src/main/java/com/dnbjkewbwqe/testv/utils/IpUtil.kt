package com.dnbjkewbwqe.testv.utils

import com.dnbjkewbwqe.testv.utils.api.Ip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object IpUtil {
    private val retrofit by lazy { RetrofitManager.getRetrofit("https://ip.seeip.org",false) }
    private val api by lazy { retrofit.create(Ip::class.java) }
    private var countryCode : String? = null
    private val blackList = arrayListOf("CN","HK","MO","IR")
    fun inBlackList() : Boolean = blackList.contains(countryCode)
    suspend fun isInBlackList() : Boolean{
        if(blackList.contains(countryCode))
            return true
        return try {
            blackList.contains(api.getIpInfo().country_code)
        }catch (e : Exception){
            e.printStackTrace()
            false
        }
    }
    fun preLoadCountryCode() {
        MainScope().launch(Dispatchers.IO) {
            try {
                countryCode = api.getIpInfo().country_code
            }catch (e : Exception){
                e.printStackTrace()
            }
        }

    }
}