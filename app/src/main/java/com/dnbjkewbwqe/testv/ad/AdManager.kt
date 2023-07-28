package com.dnbjkewbwqe.testv.ad

import com.dnbjkewbwqe.testv.MMKVInstance
import com.dnbjkewbwqe.testv.application
import com.dnbjkewbwqe.testv.beans.CrePlain
import com.google.gson.Gson
import okio.use
import java.io.BufferedReader
import java.util.Calendar

object AdManager {
    private var showAdLimit = 99
    private var clickAdLimit = 99

    private const val showAdTimeKey = "showAdTimeKey"
    private const val lastShowAdDayKey = "lastShowAdDayKey"
    private const val clickAdTimeKey = "clickAdTimeKey"
    private const val lastClickAdDayKey = "lastClickAdDayKey"

    val cre_drab = StupendousLoader("cre_drab")
    val cre_easy = ElectricLoader("cre_easy")
    val cre_cious = ElectricLoader("cre_cious")
    val cre_hesit = HomelyLoader("cre_hesit")
    val cre_pen = HomelyLoader("cre_pen")


    private var showAdTimes: Int
        get() = MMKVInstance.decodeInt(showAdTimeKey, 0)
        set(value) {
            MMKVInstance.encode(showAdTimeKey, value)
        }

    private var lastShowAdDay: Int
        get() = MMKVInstance.decodeInt(lastShowAdDayKey, -1)
        set(value) {
            MMKVInstance.encode(lastShowAdDayKey, value)
        }

    private var clickAdTimes: Int
        get() = MMKVInstance.decodeInt(clickAdTimeKey, 0)
        set(value) {
            MMKVInstance.encode(clickAdTimeKey, value)
        }

    private var lastClickAdDay: Int
        get() = MMKVInstance.decodeInt(lastClickAdDayKey, -1)
        set(value) {
            MMKVInstance.encode(lastClickAdDayKey, value)
        }

    private val day: Int get() = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

    private val localConfig: String
        get() =
            if (MMKVConfig != "")
                MMKVConfig
            else application.assets.open("cre_plain.json").bufferedReader().use(BufferedReader::readText)

    private var MMKVConfig: String
        get() = MMKVInstance.decodeString("ad_config") ?: ""
        set(value) {
            MMKVInstance.encode("ad_config", value)
        }

    fun updateConfig(cfg: String) {
        synchronized(this){
            var str = if (cfg != "") cfg else localConfig
            val crePlain: CrePlain = try {
                MMKVConfig = str
                Gson().fromJson(str, CrePlain::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                MMKVConfig = ""
                str = localConfig
                Gson().fromJson(str, CrePlain::class.java)
            }
            showAdLimit = crePlain.cre_thful
            clickAdLimit = crePlain.cre_exotic

            cre_drab.updateSource(crePlain.cre_drab)
            cre_easy.updateSource(crePlain.cre_easy)
            cre_cious.updateSource(crePlain.cre_cious)
            cre_hesit.updateSource(crePlain.cre_hesit)
            cre_pen.updateSource(crePlain.cre_pen)
        }
    }

    fun preLoadCache(){
        cre_easy.cleanCache()
        cre_cious.cleanCache()
    }

    fun clearCache(){
        cre_drab.cleanCache()
        cre_easy.cleanCache()
        cre_cious.cleanCache()
        cre_hesit.cleanCache()
        cre_pen.cleanCache()
    }

    fun isUpToTimes() =
        ((lastShowAdDay == day) && (showAdTimes >= showAdLimit)) || ((lastClickAdDay == day) && (clickAdTimes >= clickAdLimit))

    fun showedAd(){
        synchronized(this){
            if(lastShowAdDay == day)
                showAdTimes ++
            else{
                lastShowAdDay = day
                showAdTimes = 1
            }
        }
    }

    fun clickAd(){
        synchronized(this){
            if(lastClickAdDay == day)
                clickAdTimes ++
            else{
                lastClickAdDay = day
                clickAdTimes = 1
            }
        }
    }
}