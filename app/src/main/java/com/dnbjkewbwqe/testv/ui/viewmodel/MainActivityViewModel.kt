package com.dnbjkewbwqe.testv.ui.viewmodel

import android.net.VpnService
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dnbjkewbwqe.testv.ui.MainActivity
import com.dnbjkewbwqe.testv.R
import com.dnbjkewbwqe.testv.utils.ActivityManager
import com.dnbjkewbwqe.testv.utils.IpUtil
import com.dnbjkewbwqe.testv.utils.ServerManager
import com.github.shadowsocks.Core
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager
import com.github.shadowsocks.preference.DataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class MainActivityViewModel : ViewModel(), ShadowsocksConnection.Callback {
    val state: MutableLiveData<BaseService.State> = MutableLiveData()
    val service by lazy { ShadowsocksConnection(true) }
    private lateinit var activity: WeakReference<MainActivity>
    private var connectJob : Job? = null

    fun connectVPN() {
        val startTimeStamp = System.currentTimeMillis()
        val delayTo = startTimeStamp + 2000
        connectJob = MainScope().launch(Dispatchers.IO) {
            if(!ServerManager.online()){
                showNetworkAlert()
                return@launch
            }
            state.postValue(BaseService.State.Connecting)
            if(IpUtil.isInBlackList()){
                withContext(Dispatchers.Main){ showPolicyAlert() }
                return@launch
            }
            while (System.currentTimeMillis() < delayTo)
                delay(200)

            val server = ServerManager.getFasterServer()
            val profile: Profile = with(server) {
                Profile(name = testiy, host = testip, remotePort = testpo, password = testord, method = testme)
            }
            ProfileManager.clear()
            DataStore.profileId = ProfileManager.createProfile(profile).id
            if(isActive)
                Core.startService()
        }

    }

    private fun disconnectVPN() {
        connectJob = MainScope().launch(Dispatchers.IO) {
            state.postValue(BaseService.State.Stopping)
            delay(2000L)
            if(isActive)
                Core.stopService()
        }
    }

    private fun showNetworkAlert(){
        activity.get()?.let {
            val alertDialog = AlertDialog.Builder(it)
                .setMessage(R.string.network_dialog_message)
                .setPositiveButton(R.string.ok){_,_-> }
                .create()
            alertDialog.show()
        }
    }

    fun showPolicyAlert(){
        activity.get()?.let {
            val alertDialog = AlertDialog.Builder(it)
                .setMessage(R.string.policy_dialog_message)
                .setPositiveButton(R.string.confirm){_,_->
                    ActivityManager.finishAPP()
                }
                .setCancelable(false)
                .create()
            alertDialog.show()
        }
    }


    fun disconnectService(activity: MainActivity) {
        service.disconnect(activity)
    }

    fun connectService(activity: MainActivity) {
        service.connect(activity, this)
        this.activity = WeakReference(activity)
    }

    var interceptState = false
    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        if (interceptState)
            return
        when (state) {
            BaseService.State.Stopping, BaseService.State.Connecting -> {}
            else -> this.state.postValue(state)
        }
    }

    override fun onServiceConnected(service: IShadowsocksService) {
        state.postValue(BaseService.State.values()[service.state])
    }

    private fun checkPermission() : Boolean {
        VpnService.prepare(activity.get()).let { return it == null }
    }


    private fun requestPermission(){
        VpnService.prepare(activity.get()).let {
            activity.get()?.requestPermissionForResult?.launch(it)
        }
    }

    fun switch() {
        when (state.value) {
            BaseService.State.Idle, BaseService.State.Stopped -> if(checkPermission()) connectVPN() else requestPermission()
            BaseService.State.Connected -> disconnectVPN()
            else -> {}
        }
    }

    fun interruptConnect() {
        connectJob?.cancel()
        state.postValue(BaseService.State.values()[service.service?.state ?: 0])
    }
    fun tryInterruptConnect() : Boolean?{
        if(state.value == BaseService.State.Stopping){
            interruptConnect()
            return true
        }
        if(state.value == BaseService.State.Connecting){
            return false
        }
        return null
    }
}