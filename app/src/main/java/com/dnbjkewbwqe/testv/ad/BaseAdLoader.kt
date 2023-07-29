package com.dnbjkewbwqe.testv.ad

import com.dnbjkewbwqe.testv.beans.CachedAd
import com.dnbjkewbwqe.testv.beans.CrePlain
import com.dnbjkewbwqe.testv.utils.d
import com.google.android.gms.ads.AdRequest

abstract class BaseAdLoader<T>(protected val adPlace: String, protected val foryu: MutableList<CrePlain.Foryu>) {
    var isLoading = false
    private var cacheSource: List<CrePlain.Foryu>? = null
    private val cachePool: MutableList<CachedAd<T>> = mutableListOf()
    private var onLoadAdCallBack: OnLoadAdCallBack? = null

    protected fun adRequest() = AdRequest.Builder().build()

    fun cleanCache() = cachePool.clear()

    fun updateSource(foryu: List<CrePlain.Foryu>) {
        if (isLoading)
            cacheSource = foryu
        else loadSourceIn(foryu)
    }

    private fun loadSourceIn(foryu: List<CrePlain.Foryu>) {
        this.foryu.clear()
        this.foryu.addAll(foryu)
        this.foryu.sortByDescending { it.cre_violent }
    }

    fun loadAd(onLoadAdCallBack: OnLoadAdCallBack? = null) {
        synchronized(this) {
            if (AdManager.isUpToTimes()) {
                d("$adPlace load ad canceled,touched limit")
                onLoadAdCallBack?.onLoadCanceled()
                return
            }
            this.onLoadAdCallBack = onLoadAdCallBack
            if (hasAvailableCachedAd()) {
                d("$adPlace already has available cached ad,call LoadSuccess callback")
                onLoadAdCallBack?.onLoadSuccess()
                return
            }
            if (isLoading) {
                d("$adPlace is loading ad,replace LoadAdCallBack,call LoadReject callback")
                onLoadAdCallBack?.onLoadReject()
                return
            }
            isLoading = true
            cacheSource?.let {
                loadSourceIn(it)
                cacheSource = null
            }
            d("$adPlace start load ad")
            loadAd(0)
        }
    }

    protected abstract fun loadAd(position: Int)

    fun getAvailableCachedAd(): CachedAd<T>? {
        synchronized(this) {
            val iterator = cachePool.iterator()
            while (iterator.hasNext()) {
                val cachedAd = iterator.next()
                iterator.remove()
                if (cachedAd.isCacheAvailable()) {
                    return cachedAd
                }
            }
            return null
        }
    }

    fun hasAvailableCachedAd(): Boolean {
        synchronized(this) {
            val iterator = cachePool.iterator()
            while (iterator.hasNext()) {
                val cachedAd = iterator.next()
                if (cachedAd.isCacheAvailable())
                    return true
                else
                    iterator.remove()
            }
            return false
        }
    }

    protected fun cacheAd(ad: T, timestamp: Long? = null) {
        cachePool.add(timestamp?.let { CachedAd(ad, timestamp) } ?: CachedAd(ad))
    }

    protected fun cacheAd(cachedAd: CachedAd<T>) {
        cachePool.add(cachedAd)
    }

    protected fun onCLicked() {
        AdManager.clickAd()
    }

    protected fun onShowedAd() {
        d("$adPlace showed ad")
        AdManager.showedAd()
    }

    protected fun onLoadSuccess(ad: T) {
        synchronized(this) {
            d("$adPlace load ad success,cache ad")
            cacheAd(ad)
            onLoadAdCallBack?.onLoadSuccess()
            isLoading = false
        }
    }

    protected fun onLoadFailed() {
        synchronized(this) {
            d("$adPlace load ad failed")
            onLoadAdCallBack?.onLoadFailed()
            isLoading = false
        }
    }

    interface OnLoadAdCallBack {
        fun onLoadSuccess() {}
        fun onLoadFailed() {}
        fun onLoadReject() {}
        fun onLoadCanceled() {}
    }
}