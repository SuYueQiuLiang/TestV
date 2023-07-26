package com.dnbjkewbwqe.testv.ui

import android.view.View
import android.view.View.OnClickListener
import androidx.activity.viewModels
import com.dnbjkewbwqe.testv.R
import com.dnbjkewbwqe.testv.beans.TestServer
import com.dnbjkewbwqe.testv.databinding.ActivityResultBinding
import com.dnbjkewbwqe.testv.ui.viewmodel.ResultActivityViewModel
import com.dnbjkewbwqe.testv.utils.ServerManager
import com.github.shadowsocks.bg.BaseService
import com.gyf.immersionbar.ImmersionBar

class ResultActivity : BaseActivity<ActivityResultBinding, ResultActivityViewModel>(),OnClickListener {
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
        when(state){
            BaseService.State.Stopped,BaseService.State.Idle -> onStopped()
            else -> onConnected()
        }
        binding.backBtn.setOnClickListener(this)
    }
    private fun onStopped(){
        binding.catImg.setImageResource(R.drawable.cat_inactive)
        binding.connectState.text = getString(R.string.disconnected_succeeded)
    }
    private fun onConnected(){
        binding.catImg.setImageResource(R.drawable.cat_active)
        binding.connectState.text = getString(R.string.connected_succeeded)
    }

    override fun onClick(v: View?) {
        when(v){
            binding.backBtn -> finish()
        }
    }
}