package com.dnbjkewbwqe.testv.ui

import android.app.Activity
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.dnbjkewbwqe.testv.R
import com.dnbjkewbwqe.testv.ad.AdManager
import com.dnbjkewbwqe.testv.beans.TestServer
import com.dnbjkewbwqe.testv.databinding.ActivityTestServerListBinding
import com.dnbjkewbwqe.testv.ui.adapter.ServerRecyclerViewAdapter
import com.dnbjkewbwqe.testv.ui.viewmodel.TestServerListActivityViewModel
import com.dnbjkewbwqe.testv.utils.Point
import com.dnbjkewbwqe.testv.utils.ServerManager
import com.github.shadowsocks.bg.BaseService
import com.gyf.immersionbar.ImmersionBar

class TestServerListActivity : BaseActivity<ActivityTestServerListBinding, TestServerListActivityViewModel>(), View.OnClickListener {
    lateinit var state: BaseService.State
    override fun setView() {
        ImmersionBar.with(this)
            .transparentBar()
            .init()
        state = BaseService.State.valueOf(intent.getStringExtra("state") ?: "Idle")

        val adapter = ServerRecyclerViewAdapter(ServerManager.serverList) {
            if (it == ServerManager.selectServer)
                if(state == BaseService.State.Idle || state == BaseService.State.Stopped){
                    setResult(Activity.RESULT_OK)
                    finish()
                }else
                    return@ServerRecyclerViewAdapter
            when (state) {
                BaseService.State.Idle, BaseService.State.Stopped -> {
                    ServerManager.onSelectServer(it)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                else -> showDisconnectAlert(it)
            }
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.backBtn.setOnClickListener(this)
        viewModel.loadHomely()
    }

    override fun onBackPressed() {
        showHomely()
    }

    private fun showHomely(){
        AdManager.cre_pen.showAd(this){
            finish()
        }
    }
    private fun showDisconnectAlert(server: TestServer) {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage(R.string.change_vpn_dialog_message)
            .setPositiveButton(R.string.sure) { _, _ ->
                ServerManager.onSelectServer(server)
                setResult(Activity.RESULT_OK)
                finish()
            }
            .setNegativeButton(R.string.back) { _, _ -> }
            .create()
        alertDialog.show()
    }

    override val binding: ActivityTestServerListBinding by lazy { ActivityTestServerListBinding.inflate(layoutInflater) }
    override val viewModel: TestServerListActivityViewModel by viewModels()
    override fun onClick(v: View?) {
        when(v){
            binding.backBtn -> showHomely()
        }
    }

    override fun onResume() {
        super.onResume()
        Point.point("cre_recep")
    }
}