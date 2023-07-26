package com.dnbjkewbwqe.testv.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.dnbjkewbwqe.testv.BuildConfig
import com.dnbjkewbwqe.testv.R
import com.dnbjkewbwqe.testv.ui.WebActivity

object SettingUtil {
    fun privacy(activity : Activity){
        activity.startActivity(WebActivity::class.java)
    }
    fun shareApp(activity: Activity){
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
        }
        activity.startActivity(Intent.createChooser(intent,"Share ${activity.getString(R.string.app_name)}"))
    }
    fun updateApp(activity: Activity){
        try{
            var intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
                setPackage("com.android.vending")
            }
            if(intent.resolveActivity(activity.packageManager) != null)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            else intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}") }
            activity.startActivity(intent)
        }catch (e : Exception){
            e.printStackTrace()
        }
    }
}