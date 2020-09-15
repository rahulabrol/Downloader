package com.downloader.ui.main.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.downloader.DownloaderService
import com.downloader.R
import com.downloader.ui.main.adapter.DownloaderAdapter
import com.downloader.ui.main.viewmodel.MainViewModel
import com.downloader.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_download.*
import kotlinx.android.synthetic.main.item_loading.*


/**
 * Example of binding and unbinding to the local service.
 * bind to, receiving an object through which it can communicate with the service.
 *
 * Note that this is implemented as an inner class only keep the sample
 * all together; typically this code would appear in some separate class.
 */
@AndroidEntryPoint
class DownloadActivity : AppCompatActivity() {
    // Don't attempt to unbind from the service unless the client has received some
    // information about the service's state.
    private var mShouldUnbind = false

    // To invoke the bound service, first make sure that this value
    // is not null.
    private var mBoundService: DownloaderService? = null

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = (service as DownloaderService.LocalBinder).service

            // Tell the user about this for our demo.
            Toast.makeText(this@DownloadActivity, R.string.local_service_connected,
                    Toast.LENGTH_SHORT).show()
        }

        override fun onServiceDisconnected(className: ComponentName) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null
            Toast.makeText(this@DownloadActivity, getString(R.string.local_service_disconnected),
                    Toast.LENGTH_SHORT).show()
        }
    }

    private fun doBindService() {
        // Attempts to establish a connection with the service.  We use an
        // explicit class name because we want a specific service
        // implementation that we know will be running in our own process
        // (and thus won't be supporting component replacement by other
        // applications).
        if (bindService(Intent(this, DownloaderService::class.java),
                        mConnection, Context.BIND_AUTO_CREATE)) {
            mShouldUnbind = true
        } else {
            Log.e("MY_APP_TAG", "Error: The requested service doesn't " +
                    "exist, or this client isn't allowed access to it.")
        }
    }

    private fun doUnbindService() {
        if (mShouldUnbind) {
            // Release information about the service's state.
            unbindService(mConnection)
            mShouldUnbind = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        doUnbindService()
    }

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
