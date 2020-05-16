package com.rungo.speedball.features.statistics

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.rungo.speedball.R
import com.rungo.speedball.databinding.ActivityStatisticBinding
import com.rungo.speedball.features.base.BaseActivity
import com.rungo.speedball.features.main.MainViewModel
import com.rungo.speedball.utils.setStatusBarColor
import org.koin.android.viewmodel.ext.android.getViewModel

class StatisticActivity : BaseActivity() {

    private val binding : ActivityStatisticBinding by binding(R.layout.activity_statistic)

    private val adapter by lazy { StatisticAdapter() }

    private val viewModel by lazy { getViewModel<StatisticViewModel>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initComponents()
        setupListeners()
        setupObservers()
    }

    private fun initComponents() {
        setStatusBarColor(android.R.color.black)
        binding.rvHistory.adapter = adapter
        adapter.setSpeedUnit(viewModel.getSpeedUnit())
    }

    private fun setupListeners() {
        binding.imgBack.setOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.resultList.observe(this, Observer {
            it?.let {
                if (it.isNotEmpty()) {
                    adapter.setList(it)
                    binding.tvEmptyTitle.visibility = View.GONE
                } else {
                    binding.tvEmptyTitle.visibility = View.VISIBLE
                }
            }
        })
    }
}