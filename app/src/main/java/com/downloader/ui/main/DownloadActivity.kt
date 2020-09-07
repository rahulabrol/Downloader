package com.downloader.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.downloader.DownloaderAdapter
import com.downloader.R
import com.downloader.ui.main.viewmodel.MainViewModel
import com.downloader.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_download.*
import kotlinx.android.synthetic.main.item_loading.*

@AndroidEntryPoint
class DownloadActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val downloaderAdapter by lazy {
        DownloaderAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        recyclerView.apply {
            val manager = LinearLayoutManager(this@DownloadActivity)
            layoutManager = manager
            addItemDecoration(DividerItemDecoration(recyclerView.context, manager.orientation))
            adapter = downloaderAdapter
        }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.getUsers().observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        recyclerView.visibility = View.VISIBLE
                        view_loading.visibility = View.GONE
                        resource.data?.let {
                            downloaderAdapter.urlList = it
                        }
                    }
                    Status.ERROR -> {
                        recyclerView.visibility = View.VISIBLE
                        view_loading.visibility = View.GONE
                        //empty list shown
                        downloaderAdapter.urlList = arrayListOf()
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        view_loading.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                }
            }
        })
    }
}
