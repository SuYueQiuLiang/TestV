package com.dnbjkewbwqe.testv.utils

import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.dnbjkewbwqe.testv.MMKVInstance
import com.dnbjkewbwqe.testv.application
import com.dnbjkewbwqe.testv.beans.CreSmall
import com.dnbjkewbwqe.testv.beans.Referrer
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.use
import java.io.BufferedReader

object ReferrerUtil {
    private lateinit var installReferrerClient: InstallReferrerClient
    private val knownReferrer = arrayListOf("fb4a", "facebook", "gclid", "not%20set", "youtubeads", "%7B%22")
    private const val delay = 10000L

    private val localConfig: String
        get() =
            if (MMKVConfig != "")
                MMKVConfig
            else application.assets.open("cre_small.json").bufferedReader().use(BufferedReader::readText)

    private var MMKVConfig: String
        get() = MMKVInstance.decodeString("small_config") ?: ""
        set(value) {
            MMKVInstance.encode("small_config", value)
        }

    lateinit var creSmall: CreSmall

    fun updateConfig(cfg: String) {
        synchronized(this) {
            var str = if (cfg != "") cfg else localConfig
            creSmall = try {
                MMKVConfig = str
                Gson().fromJson(str, CreSmall::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                MMKVConfig = ""
                str = localConfig
                Gson().fromJson(str, CreSmall::class.java)
            }

        }
    }


    var referrer: Referrer?
        //get() = Referrer("fb4a")
        get() = MMKVInstance.decodeParcelable("referrer", Referrer::class.java)
        set(value) {
            MMKVInstance.encode("referrer", value)
        }

    private var retryTimes = 0

    fun getReferrer() {
        if (referrer != null)
            return
        retryTimes = 0
        installReferrerClient = InstallReferrerClient.newBuilder(application).build()
        installReferrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(code: Int) {
                when (code) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        val response = installReferrerClient.installReferrer
                        val url = response.installReferrer
                        for (eachReferrer in knownReferrer) {
                            if (url.contains(eachReferrer)) {
                                referrer = Referrer(eachReferrer)
                                installReferrerClient.endConnection()
                                return
                            }
                        }
                        referrer = Referrer("Organic")
                        installReferrerClient.endConnection()
                    }

                    else -> {
                        MainScope().launch {
                            if (retryTimes < 3) {
                                retryTimes++
                                delay(delay)
                                getReferrer()
                            }
                        }
                    }
                }
            }

            override fun onInstallReferrerServiceDisconnected() {

            }
        })
    }
}