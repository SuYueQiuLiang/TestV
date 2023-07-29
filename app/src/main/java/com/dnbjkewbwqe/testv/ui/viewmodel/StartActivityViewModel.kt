package com.dnbjkewbwqe.testv.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnbjkewbwqe.testv.ad.AdManager
import com.dnbjkewbwqe.testv.ad.BaseAdLoader
import com.dnbjkewbwqe.testv.gotFirebase
import com.dnbjkewbwqe.testv.ui.MainActivity
import com.dnbjkewbwqe.testv.ui.StartActivity
import com.dnbjkewbwqe.testv.utils.ActivityManager
import com.dnbjkewbwqe.testv.utils.startActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class StartActivityViewModel : ViewModel() {
    val progress : MutableLiveData<Int> = MutableLiveData()
    private lateinit var activity : WeakReference<StartActivity>
    private var progressJob : Job? = null
    private var queryJob : Job? = null

    private var delayTime = 100L

    fun pauseProgress(){
        progressJob?.cancel()
        queryJob?.cancel()
        progressJob = null
        queryJob = null
        progress.postValue(0)
        delayTime = 100
    }



    fun startProgress(activity : StartActivity){
        this.activity = WeakReference(activity)
        progressJob = viewModelScope.launch{
            while ((progress.value ?: 0) < 100){
                progress.postValue((progress.value ?: 0)+1)
                delay(delayTime)
            }
            if(isActive)
                onProgressEnd()
        }
        queryJob = viewModelScope.launch {
            val startTimeStamp = System.currentTimeMillis()
            while ((System.currentTimeMillis() - startTimeStamp) < 4000 && !gotFirebase)
                delay(100)
            if(isActive)
                loadAd()
        }
    }

    private fun loadAd(){
        AdManager.preLoadCache()
        AdManager.cre_drab.loadAd(object : BaseAdLoader.OnLoadAdCallBack{
            override fun onLoadSuccess() {
                endLoad()
            }
            override fun onLoadFailed() {
                viewModelScope.launch {
                    delay(500)
                    if(isActive)
                        loadAd()
                }
            }
            override fun onLoadCanceled() {
                endLoad()
            }
        })
    }

    private fun showAd(){
        activity.get()?.let { AdManager.cre_drab.showAd(it){
            jump()
        } }
    }

    private fun endLoad(){
        delayTime = 10L
    }

    private fun onProgressEnd(){
        if(AdManager.cre_drab.hasAvailableCachedAd())
            showAd()
        else
            jump()
    }

    private fun jump(){
        activity.get()?.apply {
            if(!ActivityManager.isAvailable(this))
                return
            startActivity(MainActivity::class.java)
            finish()
        }

    }

}