package com.dnbjkewbwqe.testv.ui

import androidx.activity.viewModels
import com.dnbjkewbwqe.testv.databinding.ActivityStartBinding
import com.dnbjkewbwqe.testv.ui.viewmodel.StartActivityViewModel
import com.gyf.immersionbar.ImmersionBar


class StartActivity : BaseActivity<ActivityStartBinding, StartActivityViewModel>() {
    override val binding: ActivityStartBinding by lazy { ActivityStartBinding.inflate(layoutInflater) }
    override val viewModel: StartActivityViewModel by viewModels()
    override fun setView(){
        ImmersionBar.with(this)
            .transparentBar()
            .statusBarDarkFont(true)
            .init()

    }

    override fun bindingData() {
        viewModel.progress.observe(this){
            binding.progressBar.progress = it
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startProgress(this)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pauseProgress()
    }
}