package com.dnbjkewbwqe.testv.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dnbjkewbwqe.testv.ad.AdManager
import com.dnbjkewbwqe.testv.ad.BaseAdLoader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TestServerListActivityViewModel : ViewModel() {
    fun loadHomely() {
        AdManager.cre_pen.loadAd(object : BaseAdLoader.OnLoadAdCallBack {
            override fun onLoadFailed() {
                viewModelScope.launch {
                    delay(1000L)
                    loadHomely()
                }
            }
        })
    }
}