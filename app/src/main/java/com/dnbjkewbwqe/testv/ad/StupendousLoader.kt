package com.dnbjkewbwqe.testv.ad

import android.app.Activity
import com.dnbjkewbwqe.testv.application
import com.dnbjkewbwqe.testv.beans.CachedAd
import com.dnbjkewbwqe.testv.beans.CrePlain
import com.dnbjkewbwqe.testv.utils.ActivityManager
import com.dnbjkewbwqe.testv.utils.d
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class StupendousLoader(adPlace: String, foryu: MutableList<CrePlain.Foryu> = mutableListOf()) : BaseAdLoader<Any>(adPlace, foryu) {
    override fun loadAd(position: Int) {
        if (position >= foryu.size) {
            onLoadFailed()
            return
        }
        d("$adPlace ${foryu[position]}")
        when (foryu[position].cre_kind) {
            "stupendous" -> loadStupendous(position)
            else -> loadHomely(position)
        }
    }

    private fun loadHomely(position: Int) {
        InterstitialAd.load(application, foryu[position].cre_remi, adRequest(), object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(e: LoadAdError) {
                d("$adPlace load ad with ${foryu[position].cre_remi} failed,try next")
                loadAd(position + 1)
            }

            override fun onAdLoaded(ad: InterstitialAd) {
                onLoadSuccess(ad)
            }
        })
    }

    private fun loadStupendous(position: Int) {
        AppOpenAd.load(application, foryu[position].cre_remi, adRequest(), object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdFailedToLoad(e: LoadAdError) {
                d("$adPlace load ad with ${foryu[position].cre_remi} failed,try next")
                loadAd(position + 1)
            }

            override fun onAdLoaded(ad: AppOpenAd) {
                onLoadSuccess(ad)
            }
        })
    }

    fun showAd(activity: Activity, onDismissAdCallback: () -> Unit) {
        if (AdManager.isUpToTimes()) {
            d("$adPlace show ad failed,touched limit")
            onDismissAdCallback.invoke()
            return
        }
        val ad = getAvailableCachedAd()
        if (ad == null) {
            d("$adPlace show ad failed,there is no available cached ad")
            onDismissAdCallback.invoke()
            return
        }
        when (ad.ad) {
            is AppOpenAd -> showStupendous(ad, activity, onDismissAdCallback)
            is InterstitialAd -> showHomely(ad, activity, onDismissAdCallback)
            else -> return
        }
    }

    private fun showStupendous(ad: CachedAd<Any>, activity: Activity, onDismissAdCallback: () -> Unit) {
        val openAd = (ad.ad as AppOpenAd)
        openAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                onCLicked()
            }

            override fun onAdDismissedFullScreenContent() {
                onDismissAdCallback.invoke()
            }

            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                cacheAd(ad)
                onDismissAdCallback.invoke()
            }

            override fun onAdShowedFullScreenContent() {
                onShowedAd()
            }
        }
        if (ActivityManager.isAvailable(activity))
            openAd.show(activity)
        else {
            cacheAd(ad)
            onDismissAdCallback.invoke()
        }
    }


    private fun showHomely(ad: CachedAd<Any>, activity: Activity, onDismissAdCallback: () -> Unit) {
        val openAd = (ad.ad as InterstitialAd)
        openAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                onCLicked()
            }

            override fun onAdDismissedFullScreenContent() {
                onDismissAdCallback.invoke()
            }

            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                cacheAd(ad)
                onDismissAdCallback.invoke()
            }

            override fun onAdShowedFullScreenContent() {
                onShowedAd()
            }
        }
        if (ActivityManager.isAvailable(activity))
            openAd.show(activity)
        else {
            cacheAd(ad)
            onDismissAdCallback.invoke()
        }
    }
}