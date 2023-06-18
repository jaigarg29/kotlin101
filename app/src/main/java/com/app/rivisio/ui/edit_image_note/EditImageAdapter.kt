package com.app.rivisio.ui.edit_image_note

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.rivisio.BuildConfig
import com.app.rivisio.R
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.model.Image
import javax.inject.Inject

class EditImageAdapter @Inject constructor() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var imageUrls: List<String>

    interface Callback {
        fun onDeleteImageClick(imageUrl: String)
    }

    private lateinit var callback: Callback

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun updateItems(imageUrls: List<String>) {
        this.imageUrls = imageUrls
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val serverImageViewHolder = ServerImageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        )

        serverImageViewHolder.itemView.findViewById<AppCompatImageView>(R.id.delete_image)
            .setOnClickListener {
                val position = serverImageViewHolder.bindingAdapterPosition
                if (this::callback.isInitialized) {
                    callback.onDeleteImageClick(imageUrls[position])
                }
            }

        return serverImageViewHolder
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ServerImageViewHolder).onBind(imageUrls[position])
    }

    inner class ServerImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBind(imageUrl: String) {
            Glide
                .with(itemView.context)
                .asBitmap()
                .load(BuildConfig.BASE_URL + "/users/getfile?awsUrl=" + imageUrl)
                .centerCrop()
                .into(itemView.findViewById<AppCompatImageView>(R.id.image_note))

        }
    }
}