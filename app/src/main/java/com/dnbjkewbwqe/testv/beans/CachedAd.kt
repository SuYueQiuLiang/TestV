package com.dnbjkewbwqe.testv.beans

data class CachedAd<T>(
    val ad : T,
    val cacheTime : Long = System.currentTimeMillis()
){
    fun isCacheAvailable() : Boolean{
        val currentTimeStamp = System.currentTimeMillis()
        return currentTimeStamp <= (cacheTime + 3600000)
    }
}
