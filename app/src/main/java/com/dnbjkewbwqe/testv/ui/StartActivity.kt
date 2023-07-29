package com.dnbjkewbwqe.testv.ui

import android.content.Intent
import androidx.activity.viewModels
import com.dnbjkewbwqe.testv.databinding.ActivityStartBinding
import com.dnbjkewbwqe.testv.ui.viewmodel.StartActivityViewModel
import com.dnbjkewbwqe.testv.utils.ActivityManager
import com.dnbjkewbwqe.testv.utils.Point
import com.gyf.immersionbar.ImmersionBar


class StartActivity : BaseActivity<ActivityStartBinding, StartActivityViewModel>() {
    override val binding: ActivityStartBinding by lazy { ActivityStartBinding.inflate(layoutInflater) }
    override val viewModel: StartActivityViewModel by viewModels()
    override fun setView() {
        ImmersionBar.with(this)
            .transparentBar()
            .statusBarDarkFont(true)
            .init()
        ActivityManager.reLoadAd = true
    }

    override fun onBackPressed() {

    }

    override fun bindingData() {
        viewModel.progress.observe(this) {
            binding.progressBar.progress = it
        }
    }

    override fun onResume() {
        super.onResume()
        Point.point("cre_zippy")
        viewModel.startProgress(this)
    }

    override fun onStop() {
        super.onStop()
        viewModel.pauseProgress()
    }

    override fun init() {
        if (isTaskRoot.not() && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN == intent.action) {
            finish()
            return
        }
    }
}