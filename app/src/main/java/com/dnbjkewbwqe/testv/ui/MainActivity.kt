package com.dnbjkewbwqe.testv.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout
import com.dnbjkewbwqe.testv.R
import com.dnbjkewbwqe.testv.databinding.ActivityMainBinding
import com.dnbjkewbwqe.testv.firstUse
import com.dnbjkewbwqe.testv.ui.viewmodel.MainActivityViewModel
import com.dnbjkewbwqe.testv.utils.IpUtil
import com.dnbjkewbwqe.testv.utils.ServerManager
import com.dnbjkewbwqe.testv.utils.SettingUtil
import com.dnbjkewbwqe.testv.utils.startActivity
import com.github.shadowsocks.bg.BaseService
import com.gyf.immersionbar.ImmersionBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding, MainActivityViewModel>(),OnClickListener {
    override val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override val viewModel: MainActivityViewModel by viewModels()
    private var refreshUIJob : Job? = null
    private var active = false

    val requestPermissionForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK)
            viewModel.connectVPN()
    }
    private val startServerListActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK) {
            active = false
            viewModel.switch()
        }
    }
    override fun onBackPressed() {
        if(firstUse.value == true){
            firstUse.postValue(false)
            return
        }
        when(viewModel.tryInterruptConnect()){
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

        if(ImmersionBar.hasNavigationBar(this)&&ImmersionBar.isNavigationAtBottom(this))
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

        if(IpUtil.inBlackList())
            viewModel.showPolicyAlert()
    }

    private fun onStopped(){
        refreshUIJob?.cancel()
        binding.connectTime.text = getString(R.string.connect_time_default)
        binding.connectBtn.endLoading()
        binding.connectBtn.text = getString(R.string.connect)
        binding.robotImg.setImageResource(R.drawable.robot_idle)
        if(ServerManager.isConnect()) {
            startActivity(ResultActivity::class.java, Bundle().apply {
                putString("state",BaseService.State.Stopped.name)
                putParcelable("server",ServerManager.connectServer)
            })
            ServerManager.onDisconnect()
        }
    }

    private fun onConnecting(){
        refreshUIJob?.cancel()
        binding.connectTime.text = getString(R.string.connect_time_default)
        binding.connectBtn.startLoading()
        binding.robotImg.setImageResource(R.drawable.robot_active)
    }

    private fun onConnected(){
        refreshUIJob?.cancel()
        binding.connectTime.text = getString(R.string.connect_time_default)
        binding.connectBtn.endLoading()
        binding.connectBtn.text = getString(R.string.connected)
        binding.robotImg.setImageResource(R.drawable.robot_idle)
        if(!ServerManager.isConnect()){
            ServerManager.onConnect(ServerManager.selectServer)
            startActivity(ResultActivity::class.java, Bundle().apply {
                putString("state",BaseService.State.Connected.name)
                putParcelable("server",ServerManager.connectServer)
            })
        }
        refreshUIJob = MainScope().launch {
            while (true){
                binding.connectTime.text = ServerManager.connectTime
                delay(1000L)
            }
        }
    }

    private fun onStopping(){
        refreshUIJob?.cancel()
        binding.connectBtn.startLoading()
        binding.robotImg.setImageResource(R.drawable.robot_active)
    }

    override fun bindingData() {
        viewModel.state.observe(this) {
            when(it){
                BaseService.State.Connecting -> onConnecting()
                BaseService.State.Stopping -> onStopping()
                BaseService.State.Connected -> onConnected()
                BaseService.State.Stopped,BaseService.State.Idle,null -> onStopped()
            }
        }
        firstUse.observe(this){
            if(it){
                binding.shapeFull.visibility = View.VISIBLE
                binding.handIcon.visibility = View.VISIBLE
            }else{
                binding.shapeFull.visibility = View.GONE
                binding.handIcon.visibility = View.GONE
            }
        }
    }

    override fun onClick(v: View?) {
        if(firstUse.value == true && v != binding.connectBtn)
            return
        firstUse.postValue(false)
        when(v){
            binding.settingBtn -> if(viewModel.tryInterruptConnect() != false){ binding.drawerLayout.open() }
            binding.serverBtn -> if(viewModel.tryInterruptConnect() != false) {
                startServerListActivityForResult.launch(Intent(this, TestServerListActivity::class.java).apply {
                    putExtra("state", viewModel.state.value?.name)
                })
            }
            binding.connectBtn,binding.robotImg -> {
                if(active.not())
                    return
                active = false
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
        active = true
        binding.flagImg.setImageResource(ServerManager.getFlagImgByServer(this,ServerManager.connectServer ?: ServerManager.selectServer))
    }

    override fun onStop() {
        super.onStop()
        viewModel.interruptConnect()
    }
}