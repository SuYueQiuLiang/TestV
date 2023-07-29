package com.dnbjkewbwqe.testv.ui

import android.view.View
import android.view.View.OnClickListener
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.dnbjkewbwqe.testv.R
import com.dnbjkewbwqe.testv.ad.AdManager
import com.dnbjkewbwqe.testv.ad.BaseAdLoader
import com.dnbjkewbwqe.testv.ad.ElectricType
import com.dnbjkewbwqe.testv.databinding.ActivityResultBinding
import com.dnbjkewbwqe.testv.ui.viewmodel.ResultActivityViewModel
import com.dnbjkewbwqe.testv.utils.ActivityManager
import com.dnbjkewbwqe.testv.utils.ServerManager
import com.github.shadowsocks.bg.BaseService
import com.gyf.immersionbar.ImmersionBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ResultActivity : BaseActivity<ActivityResultBinding, ResultActivityViewModel>(), OnClickListener {
    override val binding: ActivityResultBinding by lazy { ActivityResultBinding.inflate(layoutInflater) }
    override val viewModel: ResultActivityViewModel by viewModels()
    override fun setView() {
        ImmersionBar.with(this)
            .transparentBar()
            .init()

        val state = BaseService.State.valueOf(intent.getStringExtra("state") ?: "Idle")
        val server = intent.getParcelableExtra("server") ?: ServerManager.selectServer
        binding.flagImg.setImageResource(server.flagResourceId())
        binding.countryName.text = server.testtry
        when (state) {
            BaseService.State.Stopped, BaseService.State.Idle -> onStopped()
            else -> onConnected()
        }
        binding.backBtn.setOnClickListener(this)

        AdManager.cre_cious.showHolder(this, binding.adContainer, ElectricType.Big)
        loadElectric()
        loadHomely()
    }

    private fun onStopped() {
        binding.catImg.setImageResource(R.drawable.cat_inactive)
        binding.connectState.text = getString(R.string.disconnected_succeeded)
    }

    private fun onConnected() {
        binding.catImg.setImageResource(R.drawable.cat_active)
        binding.connectState.text = getString(R.string.connected_succeeded)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.backBtn -> showHomely()
        }
    }

    override fun onBackPressed() {
        showHomely()
    }

    fun loadHomely() {
        AdManager.cre_pen.loadAd(object : BaseAdLoader.OnLoadAdCallBack {
            override fun onLoadFailed() {
                lifecycleScope.launch {
                    delay(1000L)
                    if (ActivityManager.isAvailable(this@ResultActivity))
                        loadHomely()
                }
            }
        })
    }

    private fun showHomely() {
        AdManager.cre_pen.showAd(this) {
            finish()
        }
    }

    fun loadElectric() {
        AdManager.cre_cious.loadAd(object : BaseAdLoader.OnLoadAdCallBack {
            override fun onLoadSuccess() {
                showElectric()
            }

            override fun onLoadFailed() {
                lifecycleScope.launch {
                    delay(1000L)
                    if (ActivityManager.isAvailable(this@ResultActivity))
                        loadElectric()
                }
            }

        })
    }

    fun showElectric() {
        lifecycleScope.launch {
            if (isActive)
                AdManager.cre_cious.showAd(this@ResultActivity, binding.adContainer, ElectricType.Big)
        }
    }

}