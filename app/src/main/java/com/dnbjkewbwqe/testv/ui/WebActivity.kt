package com.dnbjkewbwqe.testv.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.dnbjkewbwqe.testv.R
import com.dnbjkewbwqe.testv.databinding.ActivityWebBinding
import com.gyf.immersionbar.ImmersionBar

class WebActivity : AppCompatActivity() {
    private val url = "https://sites.google.com/view/creativeapp/home"
    val binding : ActivityWebBinding by lazy { ActivityWebBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ImmersionBar.with(this)
            .transparentBar()
            .statusBarDarkFont(true)
            .init()
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.loadWithOverviewMode = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient(){
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = false
            }
            loadUrl(this@WebActivity.url)
        }

    }
}