package com.dnbjkewbwqe.testv.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.dnbjkewbwqe.testv.MMKVInstance
import com.dnbjkewbwqe.testv.R
import com.dnbjkewbwqe.testv.application
import com.dnbjkewbwqe.testv.beans.PingInfo
import com.dnbjkewbwqe.testv.beans.TestServer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.text.DecimalFormat
import java.util.LinkedList
import java.util.Random
import kotlin.math.roundToInt

object ServerManager {
    val serverList = LinkedList<TestServer>()
    val smartServer by lazy { TestServer(testtry = "Faster Server") }
    var selectServer: TestServer = smartServer
    private const val selectServerKey = "selectServerKey"

    private const val connectTimeKey = "connectTimeKey"
    private const val connectServerKey = "connectServerKey"

    val connectServer
        get() = MMKVInstance.decodeParcelable(connectServerKey,TestServer::class.java)
    val connectTime
        get() = run {
            val currentTIme = System.currentTimeMillis()
            val startTime = MMKVInstance.decodeLong(connectTimeKey,System.currentTimeMillis())
            val after = (currentTIme - startTime)/1000
            val hours = after / 60 / 60 % 100
            val minutes = (after / 60) % 60
            val seconds = after % 60
            val decimalFormat = DecimalFormat("00")
            application.getString(R.string.connect_time_format).format(decimalFormat.format(hours),decimalFormat.format(minutes),decimalFormat.format(seconds))
        }

    fun updateConfig(server: String) {
        synchronized(serverList){
            val json = if (server != "") server else readConfig()
            val serverList: ArrayList<TestServer> = Gson().fromJson(json, object : TypeToken<ArrayList<TestServer>>() {}.type)
            this.serverList.clear()
            this.serverList.add(smartServer)
            this.serverList.addAll(serverList)
        }

    }

    @SuppressLint("DiscouragedApi")
    fun getFlagImgByServer(context : Context, server: TestServer) : Int{
        var resourceId = context.resources.getIdentifier(server.testtry.lowercase().replace(" ",""),"drawable",context.packageName)
        if(resourceId == 0)
            resourceId = context.resources.getIdentifier(smartServer.testtry.lowercase().replace(" ",""),"drawable",context.packageName)
        return resourceId
    }

    fun getFasterServer() : TestServer{
        if(serverList.contains(selectServer)&& selectServer != smartServer)
            return selectServer
        val list = LinkedList<TestServer>().apply {
            addAll(serverList)
            remove(smartServer)
        }
        val iterator = list.iterator()
        val pingInfo = LinkedList<PingInfo>()
        while (iterator.hasNext()){
            val next = iterator.next()
            val ping = ping(next.testip)
            d("ping ip ${next.testip} delay $ping")
            if(ping != Int.MAX_VALUE)
                pingInfo.add(PingInfo(next,ping))
        }
        return if(pingInfo.isEmpty())
            list.random()
        else {
            pingInfo.sortBy { it.delay }
            val random = Random()
            return if(pingInfo.size > 3)
                pingInfo[random.nextInt(3)].server
            else pingInfo.random().server
        }
    }

    fun onSelectServer(server : TestServer){
        selectServer = if(serverList.contains(server))
            server
        else smartServer
        MMKVInstance.encode(selectServerKey, selectServer)
    }

    fun isConnect() = MMKVInstance.containsKey(connectTimeKey)

    fun onDisconnect(){
        MMKVInstance.remove(connectTimeKey)
        MMKVInstance.remove(connectServerKey)
    }

    fun onConnect(server : TestServer){
        MMKVInstance.encode(connectTimeKey,System.currentTimeMillis())
        MMKVInstance.encode(connectServerKey, server)
    }

    private fun readConfig() = application.assets.open("TV.json").bufferedReader().use(BufferedReader::readText)

    private fun ping(ip: String): Int {
        return try {
            val command = "/system/bin/ping -c 1 -w 1 $ip"
            val re = Runtime.getRuntime().exec(command).inputStream.bufferedReader().use(BufferedReader::readText)
            re.let {
                if (re.contains("min/avg/max/mdev").not())
                    return Int.MAX_VALUE
                val info = re.substring(it.indexOf("min/avg/max/mdev") + 19).split("/").toTypedArray()
                return info[1].toFloat().roundToInt()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Int.MAX_VALUE
        }
    }

    fun online(): Boolean {
        val connectivityManager = application.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.let {
            val activeNetwork = it.activeNetwork ?: return false
            val cap = it.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                cap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                cap.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                cap.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        }
    }
}