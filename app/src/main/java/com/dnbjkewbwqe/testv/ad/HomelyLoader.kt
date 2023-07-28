package com.dnbjkewbwqe.testv.ad

import android.app.Activity
import com.dnbjkewbwqe.testv.application
import com.dnbjkewbwqe.testv.beans.CrePlain
import com.dnbjkewbwqe.testv.utils.ActivityManager
import com.dnbjkewbwqe.testv.utils.ReferrerUtil
import com.dnbjkewbwqe.testv.utils.d
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class HomelyLoader(adPlace: String, foryu: MutableList<CrePlain.Foryu> = mutableListOf()) : BaseAdLoader<InterstitialAd>(adPlace, foryu) {
    override fun loadAd(position: Int) {
        if (position >= foryu.size) {
            onLoadFailed()
            return
        }
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

    fun showAd(activity: Activity, onDismissAdCallback: () -> Unit) {
        if (AdManager.isUpToTimes()) {
            d("$adPlace show ad failed,touched limit")
            onDismissAdCallback.invoke()
            return
        }

        if (adPlace == "cre_pen")
            when (ReferrerUtil.creSmall.cre_purs) {
                "2" -> if (ReferrerUtil.referrer?.payed != true) {
                    onDismissAdCallback.invoke()
                    return
                }

                "3" -> if (ReferrerUtil.referrer?.FB != true) {
                    onDismissAdCallback.invoke()
                    return
                }

                "4" -> {
                    onDismissAdCallback.invoke()
                    return
                }
            }

        val ad = getAvailableCachedAd()
        if (ad == null) {
            d("$adPlace show ad failed,there is no available cached ad")
            onDismissAdCallback.invoke()
            return
        }
        val openAd = ad.ad
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