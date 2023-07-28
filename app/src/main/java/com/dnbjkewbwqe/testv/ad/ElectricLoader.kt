package com.dnbjkewbwqe.testv.ad

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.dnbjkewbwqe.testv.R
import com.dnbjkewbwqe.testv.application
import com.dnbjkewbwqe.testv.beans.CrePlain
import com.dnbjkewbwqe.testv.utils.d
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class ElectricLoader(adPlace: String, foryu: MutableList<CrePlain.Foryu> = mutableListOf()) : BaseAdLoader<NativeAd>(adPlace, foryu) {
    override fun loadAd(position: Int) {
        if (position >= foryu.size) {
            onLoadFailed()
            return
        }
        AdLoader.Builder(application, foryu[position].cre_remi)
            .forNativeAd {
                onLoadSuccess(it)
            }
            .withAdListener(object : AdListener() {
                override fun onAdClicked() {
                    onCLicked()
                }

                override fun onAdFailedToLoad(e: LoadAdError) {
                    d("$adPlace load ad with ${foryu[position].cre_remi} failed,try next")
                    loadAd(position + 1)
                }
            })
            .build().loadAd(adRequest())
    }

    fun showAd(activity: Activity, parent: ViewGroup, type: ElectricType) {
        if (AdManager.isUpToTimes()) {
            d("$adPlace show ad failed,touched limit")
            return
        }
        val ad = getAvailableCachedAd()
        if (ad == null) {
            d("$adPlace show ad failed,there is no available cached ad")
            return
        }
        val nativeAd = ad.ad
        val layoutId = when(type){
            ElectricType.Small -> R.layout.layout_electric_small
            ElectricType.Big -> R.layout.layout_electric_big
        }
        val layout = LayoutInflater.from(activity).inflate(layoutId,parent,false) as NativeAdView
        layout.apply {
            headlineView = findViewById(R.id.title)
            bodyView = findViewById(R.id.message)
            iconView = findViewById(R.id.icon_img)
            callToActionView = findViewById(R.id.call_to_action)
            mediaView = findViewById(R.id.media_view)

            mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
            nativeAd.mediaContent?.let { mediaView?.mediaContent = it }

            (iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
            (headlineView as TextView).text = nativeAd.headline
            (bodyView as TextView).text = nativeAd.body

            if(nativeAd.callToAction == null)
                callToActionView?.visibility = View.GONE
            else (callToActionView as Button).text = nativeAd.callToAction
            setNativeAd(nativeAd)
            parent.removeAllViews()
            parent.addView(this)
            onShowedAd()
            loadAd()
        }
    }

    fun showHolder(activity: Activity, parent: ViewGroup, type: ElectricType) {
        val layoutId = when(type){
            ElectricType.Small -> R.layout.layout_electric_small
            ElectricType.Big -> R.layout.layout_electric_big
        }
        val layout = LayoutInflater.from(activity).inflate(layoutId,parent,false) as NativeAdView
        layout.apply {
            headlineView = findViewById(R.id.title)
            bodyView = findViewById(R.id.message)
            iconView = findViewById(R.id.icon_img)
            callToActionView = findViewById(R.id.call_to_action)
            mediaView = findViewById(R.id.media_view)

            mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
            mediaView?.setBackgroundColor(activity.getColor(R.color.white_grey))

            iconView?.setBackgroundResource(R.drawable.shape_place_holder)
            headlineView?.setBackgroundResource(R.drawable.shape_place_holder)
            bodyView?.setBackgroundResource(R.drawable.shape_place_holder)

            callToActionView?.setBackgroundResource(R.drawable.shape_place_holder)

            findViewById<TextView>(R.id.ad_tag).visibility = View.GONE
            parent.removeAllViews()
            parent.addView(this)
        }
    }
}

enum class ElectricType {
    Big, Small
}