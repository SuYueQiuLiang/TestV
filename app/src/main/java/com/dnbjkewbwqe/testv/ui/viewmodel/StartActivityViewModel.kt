package com.dnbjkewbwqe.testv.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnbjkewbwqe.testv.ui.MainActivity
import com.dnbjkewbwqe.testv.ui.StartActivity
import com.dnbjkewbwqe.testv.utils.startActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class StartActivityViewModel : ViewModel() {
    val progress : MutableLiveData<Int> = MutableLiveData()
    private var progressJob : Job? = null
    private lateinit var activity : WeakReference<StartActivity>

    private val delayTime = 30L

    fun pauseProgress(){
        progressJob?.cancel()
        progress.postValue(0)
    }

    fun startProgress(activity : StartActivity){
        this.activity = WeakReference(activity)
        progressJob?.cancel()
        progressJob = viewModelScope.launch{
            while ((progress.value ?: 0) < 100){
                progress.postValue((progress.value ?: 0)+1)
                delay(delayTime)
            }
            if(isActive)
                onProgressEnd()
        }
    }

    private fun onProgressEnd(){
        jump()
    }

    private fun jump(){
        activity.get()?.startActivity(MainActivity::class.java)
        activity.get()?.finish()
    }
}