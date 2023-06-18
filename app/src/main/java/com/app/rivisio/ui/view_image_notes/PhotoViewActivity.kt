package com.app.rivisio.ui.view_image_notes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.app.rivisio.BuildConfig
import com.app.rivisio.R
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView

class PhotoViewActivity : AppCompatActivity() {

    companion object {

        const val IMG_URL = "imgUrl"

        fun getStartIntent(context: Context, imgUrl: String): Intent {
            val intent = Intent(context, PhotoViewActivity::class.java)
            intent.putExtra(IMG_URL, imgUrl)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)

        Glide
            .with(this)
            .asBitmap()
            .load(BuildConfig.BASE_URL + "/users/getfile?awsUrl=" + intent.getStringExtra(IMG_URL))
            .into(findViewById<PhotoView>(R.id.photo_view))
    }
}