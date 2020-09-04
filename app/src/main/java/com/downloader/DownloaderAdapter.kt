package com.downloader

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.downloader.data.model.Example
import kotlinx.android.synthetic.main.item_image.view.*


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

    class PictureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(model: Example) {
            itemView.imageView.visibility = View.INVISIBLE
            //create a thumbnail from image url and then show here
            Glide.with(itemView.context)
                    .asBitmap()
                    .load(model.url)
//                    .load("http://images.ctfassets.net/yadj1kx9rmg0/wtrHxeu3zEoEce2MokCSi/cf6f68efdcf625fdc060607df0f3baef/quwowooybuqbl6ntboz3.jpg")
//                    .placeholder(android.R.drawable.btn_plus)
                    .into(object : BitmapImageViewTarget(itemView.imageView) {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
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
    }
}
