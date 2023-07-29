package com.dnbjkewbwqe.testv.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.dnbjkewbwqe.testv.R
import com.dnbjkewbwqe.testv.ad.AdManager
import com.dnbjkewbwqe.testv.ad.BaseAdLoader
import com.dnbjkewbwqe.testv.ad.ElectricType
import com.dnbjkewbwqe.testv.beans.TestServer
import com.dnbjkewbwqe.testv.databinding.ActivityMainBinding
import com.dnbjkewbwqe.testv.firstUse
import com.dnbjkewbwqe.testv.ui.viewmodel.MainActivityViewModel
import com.dnbjkewbwqe.testv.utils.ActivityManager
import com.dnbjkewbwqe.testv.utils.IpUtil
import com.dnbjkewbwqe.testv.utils.Point
import com.dnbjkewbwqe.testv.utils.ReferrerUtil
import com.dnbjkewbwqe.testv.utils.ServerManager
import com.dnbjkewbwqe.testv.utils.SettingUtil
import com.dnbjkewbwqe.testv.utils.startActivity
import com.github.shadowsocks.bg.BaseService
import com.gyf.immersionbar.ImmersionBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : BaseActivity<ActivityMainBinding, MainActivityViewModel>(), OnClickListener {
    override val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override val viewModel: MainActivityViewModel by viewModels()
    private var refreshUIJob: Job? = null
    private var active = true
    val requestPermissionForResultB = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK)
            viewModel.connectVPNB()
        else active = true
    }
    val requestPermissionForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK)
            viewModel.connectVPN()
        else active = true
    }
    private val startServerListActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            active = false
            viewModel.switch()
        }
    }

    override fun onBackPressed() {
        if (firstUse.value == true) {
            firstUse.postValue(false)
            return
        }
        when (viewModel.tryInterruptConnect()) {
            true -> active = true
            false -> {}
            null -> super.onBackPressed()
        }
    }

    override fun setView() {
        ImmersionBar.with(this)
            .transparentBar()
            .statusBarDarkFont(true)
            .statusBarView(binding.statusBarHolder)
            .init()

        if (ImmersionBar.hasNavigationBar(this) && ImmersionBar.isNavigationAtBottom(this))
            binding.navigationBarHolder.layoutParams.height = ImmersionBar.getNavigationBarHeight(this)

        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        binding.settingBtn.setOnClickListener(this)
        binding.serverBtn.setOnClickListener(this)
        binding.robotImg.setOnClickListener(this)
        binding.connectBtn.setOnClickListener(this)

        binding.drawer.privacy.setOnClickListener(this)
        binding.drawer.share.setOnClickListener(this)
        binding.drawer.update.setOnClickListener(this)

        viewModel.connectService(this)

        if (IpUtil.inBlackList())
            viewModel.showPolicyAlert()

    }

    data class ConnectTime(val time: Long)

    private fun onStopped() {
        refreshUIJob?.cancel()
        binding.connectTime.text = getString(R.string.connect_time_default)
        binding.connectBtn.endLoading()
        binding.connectBtn.text = getString(R.string.connect)
        binding.robotImg.setImageResource(R.drawable.robot_idle)
        if (ServerManager.isConnect()) {
            if (active.not()) {
                Point.point("cre_lonel", ConnectTime(ServerManager.connectTimeLong))
                showHomely(BaseService.State.Stopped.name, ServerManager.connectServer)
            }
            ServerManager.onDisconnect()
        }
    }

    private fun onConnecting() {
        Point.point("cre_wiry")
        refreshUIJob?.cancel()
        binding.connectTime.text = getString(R.string.connect_time_default)
        binding.connectBtn.startLoading()
        binding.robotImg.setImageResource(R.drawable.robot_active)
    }

    private fun onConnected() {
        refreshUIJob?.cancel()
        binding.connectTime.text = getString(R.string.connect_time_default)
        binding.connectBtn.endLoading()
        binding.connectBtn.text = getString(R.string.connected)
        binding.robotImg.setImageResource(R.drawable.robot_idle)
        if (!ServerManager.isConnect()) {
            ServerManager.onConnect(ServerManager.selectServer)
            if (active.not()) {
                Point.point("cre_quiz")
                showHomely(BaseService.State.Connected.name, ServerManager.connectServer)
            }
        }
        refreshUIJob = lifecycleScope.launch {
            while (true) {
                binding.connectTime.text = ServerManager.connectTime
                delay(1000L)
            }
        }
    }

    private fun onStopping() {
        Point.point("cre_few")
        refreshUIJob?.cancel()
        binding.connectBtn.startLoading()
        binding.robotImg.setImageResource(R.drawable.robot_active)
    }

    override fun bindingData() {
        viewModel.state.observe(this) {
            when (it) {
                BaseService.State.Connecting -> onConnecting()
                BaseService.State.Stopping -> onStopping()
                BaseService.State.Connected -> onConnected()
                BaseService.State.Stopped, BaseService.State.Idle, null -> onStopped()
            }
            tryB()
        }
        firstUse.observe(this) {
            if (it) {
                Point.point("cre_half")
                binding.shapeFull.visibility = View.VISIBLE
                binding.handIcon.visibility = View.VISIBLE
            } else {
                binding.shapeFull.visibility = View.GONE
                binding.handIcon.visibility = View.GONE
            }
        }
    }

    private fun tryB() {
        if (ActivityManager.plainB.not())
            return
        ActivityManager.plainB = false
        if (ReferrerUtil.referrer?.payed != true)
            return
        if (ReferrerUtil.creSmall.cre_xtra == "3")
            return
        if (Random.nextInt(100) <= (ReferrerUtil.creSmall.cre_light.toInt() - 1)) {
            active = false
            firstUse.postValue(false)
            viewModel.switchB()
        }
    }

    override fun onClick(v: View?) {
        if (firstUse.value == true && v != binding.connectBtn)
            return
        firstUse.postValue(false)
        when (v) {
            binding.settingBtn -> if (viewModel.tryInterruptConnect() != false) {
                binding.drawerLayout.open()
            }

            binding.serverBtn -> if (viewModel.tryInterruptConnect() != false) {
                startServerListActivityForResult.launch(Intent(this, TestServerListActivity::class.java).apply {
                    putExtra("state", viewModel.state.value?.name)
                })
            }

            binding.connectBtn, binding.robotImg -> {
                if (active.not())
                    return
                active = false
                Point.point("cre_zical")
                if (firstUse.value == true)
                    Point.point("cre_arrow")
                viewModel.switch()
            }

            binding.drawer.privacy -> SettingUtil.privacy(this)
            binding.drawer.share -> SettingUtil.shareApp(this)
            binding.drawer.update -> SettingUtil.updateApp(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnectService(this)
    }

    override fun onResume() {
        super.onResume()
        Point.point("cre_male")
        binding.flagImg.setImageResource(ServerManager.getFlagImgByServer(this, ServerManager.connectServer ?: ServerManager.selectServer))
        if (ActivityManager.reLoadAd) {
            ActivityManager.reLoadAd = false
            AdManager.cre_easy.showHolder(this, binding.adContainer, ElectricType.Small)
            loadElectric()
        }
    }

    override fun onStop() {
        super.onStop()
        active = true
        viewModel.interruptConnect()
    }

    fun loadElectric() {
        AdManager.cre_easy.loadAd(object : BaseAdLoader.OnLoadAdCallBack {
            override fun onLoadSuccess() {
                showElectric()
            }

            override fun onLoadFailed() {
                lifecycleScope.launch {
                    delay(1000L)
                    if (ActivityManager.isAvailable(this@MainActivity))
                        loadElectric()
                }
            }

        })
    }

    fun showElectric() {
        lifecycleScope.launch {
            if (isActive)
                AdManager.cre_easy.showAd(this@MainActivity, binding.adContainer, ElectricType.Small)
        }
    }

    fun showHomely(state: String, server: TestServer?) {
        AdManager.cre_hesit.showAd(this) {
            startActivity(ResultActivity::class.java, Bundle().apply {
                putString("state", state)
                putParcelable("server", server)
            })
        }
    }

}