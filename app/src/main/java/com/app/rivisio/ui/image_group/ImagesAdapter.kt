package com.app.rivisio.ui.image_group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.rivisio.R
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.model.Image
import javax.inject.Inject

class ImagesAdapter @Inject constructor() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var images: List<Image>

    interface Callback {
        fun onDeleteImageClick(image: Image)
    }

    private lateinit var callback: Callback

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun updateItems(images: List<Image>) {
        this.images = images
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val imageViewHolder = ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        )

        imageViewHolder.itemView.findViewById<AppCompatImageView>(R.id.delete_image)
            .setOnClickListener {
                val position = imageViewHolder.bindingAdapterPosition
                if (this::callback.isInitialized) {
                    callback.onDeleteImageClick(images[position])
                }
            }

        return imageViewHolder
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ImageViewHolder).onBind(images[position])
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBind(image: Image) {
            Glide
                .with(itemView.context)
                .load(image.path)
                .centerCrop()
                .into(itemView.findViewById<AppCompatImageView>(R.id.image_note))

        }
    }
}