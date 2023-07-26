package com.dnbjkewbwqe.testv.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding


abstract class BaseActivity<VB : ViewBinding,VM : ViewModel> : AppCompatActivity() {
    abstract val binding : VB
    abstract val viewModel : VM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setContentView(binding.root)
        setView()
        bindingData()
    }
    protected open fun init(){}
    protected open fun setView(){}
    protected open fun bindingData(){}

}