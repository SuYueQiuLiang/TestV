package com.dnbjkewbwqe.testv

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process.myPid
import android.webkit.WebView
import androidx.lifecycle.MutableLiveData
import com.dnbjkewbwqe.testv.ad.AdManager
import com.dnbjkewbwqe.testv.ui.MainActivity
import com.dnbjkewbwqe.testv.utils.IpUtil
import com.dnbjkewbwqe.testv.utils.ReferrerUtil
import com.dnbjkewbwqe.testv.utils.ServerManager
import com.github.shadowsocks.Core
import com.google.android.gms.ads.MobileAds
import com.tencent.mmkv.MMKV


lateinit var application : MyApplication
val MMKVInstance by lazy { MMKV.defaultMMKV() }
var firstUse = MutableLiveData(true)
var gotFirebase = false
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            val process = getProcessName()
            if(packageName != process)
                WebView.setDataDirectorySuffix(process)
        }

        Core.init(this, MainActivity::class)
        if(isMainThread().not())
            return

        application = this

        registerActivityLifecycleCallbacks(com.dnbjkewbwqe.testv.utils.ActivityManager)

        MobileAds.initialize(this)
        MMKV.initialize(this)

        ServerManager.updateConfig("")

        AdManager.updateConfig("")
        ReferrerUtil.updateConfig("")

        IpUtil.preLoadCountryCode()

        ReferrerUtil.getReferrer()
    }

    private fun isMainThread() : Boolean{
        val processName = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            getProcessName()
        else{
            (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).runningAppProcesses.firstOrNull { it.pid == myPid() }?.processName
        }
        return processName == BuildConfig.APPLICATION_ID
    }
}