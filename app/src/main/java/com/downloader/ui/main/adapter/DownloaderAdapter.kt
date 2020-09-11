package com.downloader.ui.main.adapter

import android.app.ProgressDialog
import android.content.ContentValues
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.downloader.Downloader
import com.downloader.R
import com.downloader.data.model.Example
import com.downloader.utils.GlideApp
import kotlinx.android.synthetic.main.item_image.view.*
import timber.log.Timber


class DownloaderAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var urlList = listOf<Example>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_MAIN -> {
                PictureViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_image, parent, false))
            }
            VIEW_LOADING -> {
                LoadingViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_loading, parent, false))
            }
            else -> {
                EmptyViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_empty, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PictureViewHolder) {
            holder.bind(urlList[position])
        }
    }

    override fun getItemCount(): Int {
        return if (urlList.isEmpty()) {
            1
        } else {
            urlList.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (urlList.isEmpty()) {
            VIEW_EMPTY
        } else {
            VIEW_MAIN
        }
    }

    inner class PictureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var progressDialog: ProgressDialog? = null
        private var activityHandler: Handler? = null

        init {
            itemView.tvDownload.setOnClickListener {
                urlList[adapterPosition].downloadUrl?.let { it1 ->
                    Downloader(it1, "image",
                            itemView.context.getString(R.string.app_name), activityHandler)
                    startProgress()
                }
            }
        }

        fun bind(model: Example) {
            itemView.imageView.visibility = View.INVISIBLE
//            val bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(model.url), THUMB_SIZE, THUMB_SIZE)
            var totalSize = model.width?.times(model.height!!)?.times(4)?.div(1024)
            var sizeText = "$totalSize Kb"
            if (totalSize!! > 2048) {
                totalSize = totalSize.div(1024)
                sizeText = "$totalSize Mb"
            }
            itemView.tvDownload.text = sizeText
            //create a thumbnail from image url and then show here
            GlideApp.with(itemView.context)
                    .asBitmap()
                    .load(model.downloadUrl)
                    .into(itemView.imageView)
            /*.into(object : BitmapImageViewTarget() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    super.onResourceReady(resource, transition)
                    itemView.imageView.visibility = View.VISIBLE
                    itemView.progressBar.visibility = View.GONE
                }
            })*/
        }

        private fun startProgress() {
            Timber.tag("MSGGGG===nnnnnnn").d("==========")
            // Handler defined to received the messages from the thread and update the progress.
            activityHandler = object : Handler(Looper.myLooper()!!) {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        Downloader.MESSAGE_UPDATE_PROGRESS_BAR -> {
                            itemView.progressBar.progress = msg.arg1
                        }
                        Downloader.MESSAGE_CONNECTING_STARTED -> if (msg.obj is String) {
                            var url = msg.obj as String
                            if (url.length > 16) {
                                var tUrl = url.substring(0, 15)
                                tUrl += "..."
                                url = tUrl
                            }
                            val pdTitle = "Connecting..."
                            var pdMsg = "Connected."
                            pdMsg += " $url"
                            dismissCurrentProgressDialog()
                            progressDialog = ProgressDialog(itemView.context)
                            progressDialog?.setTitle(pdTitle)
                            progressDialog?.setMessage(pdMsg)
                            progressDialog?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                            progressDialog?.isIndeterminate = true
                            val newMsg: Message = Message.obtain(this, Downloader.MESSAGE_DOWNLOAD_CANCELED)
                            progressDialog?.setCancelMessage(newMsg)
                            progressDialog?.show()
                        }
                        Downloader.MESSAGE_DOWNLOAD_STARTED -> if (msg.obj is String) {
                            val maxValue = msg.arg1
                            val fileName = msg.obj as String
                            val pdTitle = "Downloading..."
                            var pdMsg = "Download."
                            pdMsg += " $fileName"
                            dismissCurrentProgressDialog()
                            progressDialog = ProgressDialog(itemView.context)
                            progressDialog?.setTitle(pdTitle)
                            progressDialog?.setMessage(pdMsg)
                            progressDialog?.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                            progressDialog?.progress = 0
                            progressDialog?.max = maxValue
                            // set the message to be sent when this dialog is canceled
                            val newMsg: Message = Message.obtain(this, Downloader.MESSAGE_DOWNLOAD_CANCELED)
                            progressDialog?.setCancelMessage(newMsg)
                            progressDialog?.setCancelable(true)
                            progressDialog?.show()
                        }
                        Downloader.MESSAGE_DOWNLOAD_COMPLETE -> {
                            dismissCurrentProgressDialog()
                            displayMessage("Download Complete")
                        }
                        Downloader.MESSAGE_DOWNLOAD_CANCELED -> {

                            dismissCurrentProgressDialog()
                            displayMessage("Download Canceled")
                        }
                        Downloader.MESSAGE_ENCOUNTERED_ERROR -> if (msg.obj is String) {
                            val errorMessage = msg.obj as String
                            dismissCurrentProgressDialog()
                            displayMessage(errorMessage)
//                            displayMessage("Error")
                        }
                        else -> {
                        }
                    }
                }
            }
        }

        private fun dismissCurrentProgressDialog() {
            progressDialog?.let {
                it.hide()
                it.dismiss()
            }
            progressDialog = null
        }

        private fun displayMessage(message: String?) {
            if (message != null) {
                Log.e(ContentValues.TAG, "displayMessage: -----------> $message")
                Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        const val VIEW_EMPTY = 0
        const val VIEW_MAIN = 1
        const val VIEW_LOADING = 2
        const val THUMB_SIZE = 64
    }
}
