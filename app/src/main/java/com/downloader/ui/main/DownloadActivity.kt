package com.downloader.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.downloader.DownloaderAdapter
import com.downloader.R
import com.downloader.data.api.ApiHelper
import com.downloader.data.api.RetrofitBuilder
import com.downloader.ui.base.ViewModelFactory
import com.downloader.ui.main.viewmodel.MainViewModel
import com.downloader.utils.Status
import kotlinx.android.synthetic.main.activity_download.*
import kotlinx.android.synthetic.main.item_loading.*
import javax.inject.Inject

//@AndroidEntryPoint
class DownloadActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: MainViewModel

    private val downloaderAdapter by lazy {
        DownloaderAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DownloadActivity)
            adapter = downloaderAdapter
        }

        setupViewModel()
        setupObservers()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
                this,
                ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(MainViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.getUsers().observe(this, Observer {
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
