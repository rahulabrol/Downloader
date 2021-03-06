package com.downloader.ui.main.adapter

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.downloader.Downloader
import com.downloader.R
import com.downloader.data.model.Example
import com.downloader.utils.Utils
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
        private var activityHandler: Handler? = null
        private var downloader: Downloader? = null

        init {
            itemView.tvDownload.setOnClickListener {
                urlList[adapterPosition].downloadUrl?.let { it1 ->
                    if (!Utils.isFileExistsInLocal(it1, itemView.context.getString(R.string.app_name))) {
                        startProgress()
                        downloader = Downloader("$it1.jpg", "image",
                                itemView.context.getString(R.string.app_name), activityHandler)
                    } else {
                        Toast.makeText(itemView.context, "Image exists already", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        private fun startProgress() {
            // Handler defined to received the messages from the thread and update the progress.
            activityHandler = object : Handler(Looper.myLooper()!!) {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        Downloader.MESSAGE_UPDATE_PROGRESS_BAR -> {
                            itemView.tvDownload.visibility = View.GONE
                            itemView.progressBar.visibility = View.VISIBLE
                            itemView.progressBar.progress = msg.arg1
                        }
                        Downloader.MESSAGE_CONNECTING_STARTED -> {
                        }
                        Downloader.MESSAGE_DOWNLOAD_STARTED -> {
                            // downloading is started
                            itemView.progressBar.visibility = View.VISIBLE
                            itemView.progressBar.progress = 0
                            itemView.progressBar.max = msg.arg1
                        }
                        Downloader.MESSAGE_DOWNLOAD_COMPLETE -> {
                            urlList[adapterPosition].isDownloaded = true
                            itemView.progressBar.visibility = View.GONE
                            displayMessage("Download Complete")
                            notifyDataSetChanged()
                        }
                        Downloader.MESSAGE_DOWNLOAD_CANCELED -> {
                            downloader?.interrupt()
                            urlList[adapterPosition].isDownloaded = false
                            itemView.progressBar.visibility = View.GONE
                            itemView.tvDownload.visibility = View.VISIBLE
                            displayMessage("Download Canceled")
                            notifyDataSetChanged()
                        }
                        Downloader.MESSAGE_ENCOUNTERED_ERROR -> if (msg.obj is String) {
                            urlList[adapterPosition].isDownloaded = false
                            itemView.progressBar.visibility = View.GONE
                            itemView.tvDownload.visibility = View.VISIBLE
                            val errorMessage = msg.obj as String
                            displayMessage(errorMessage)
                            notifyDataSetChanged()
                        }
                        else -> {
                        }
                    }
                }
            }
        }

        private fun displayMessage(message: String?) {
            if (message != null) {
                Timber.tag("DownloaderAdapter").e(message)
                Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
            }
        }

        fun bind(model: Example) {
            itemView.imageView.visibility = View.INVISIBLE
            var totalSize = model.width?.times(model.height!!)?.times(4)?.div(1024)
            var sizeText = "$totalSize Kb"
            if (totalSize!! > 2048) {
                totalSize = totalSize.div(1024)
                sizeText = "$totalSize Mb"
            }
            itemView.progressBar.visibility = View.GONE
            itemView.tvDownload.visibility = if (model.isDownloaded || Utils.isFileExistsInLocal(model.downloadUrl!!, itemView.context.getString(R.string.app_name))) {
                View.GONE
            } else {
                itemView.tvDownload.text = sizeText
                View.VISIBLE
            }
            Glide.with(itemView.context)
                    .asBitmap()
                    .load(model.downloadUrl)
//                    .into(itemView.imageView)
                    .into(object : BitmapImageViewTarget(itemView.imageView) {
                        override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                            super.onResourceReady(resource, transition)
                            itemView.imageView.visibility = View.VISIBLE
                            itemView.progressBar.visibility = View.GONE
                        }
                    })
        }
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        const val VIEW_EMPTY = 0
        const val VIEW_MAIN = 1
        const val VIEW_LOADING = 2
        const val THUMBSIZE = 64
    }
}
