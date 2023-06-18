package com.app.rivisio

import android.app.Application
import android.content.res.Resources
import android.graphics.Color
import android.os.Environment
import android.util.TypedValue
import android.view.Gravity
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.ImagePickerMode
import com.esafirm.imagepicker.features.ImagePickerSavePath
import com.esafirm.imagepicker.features.ReturnMode
import dagger.hilt.android.HiltAndroidApp
import es.dmoral.toasty.Toasty
import timber.log.Timber

@HiltAndroidApp
class RivisioApp : Application() {

    companion object {
        lateinit var config: ImagePickerConfig
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        val dip = 100f
        val r: Resources = resources
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            r.displayMetrics
        )

        Toasty.Config.getInstance()
            .setGravity(
                Gravity.BOTTOM,
                0,
                px.toInt()
            ) // optional (set toast gravity, offsets are optional)
            .apply()

        config = ImagePickerConfig {
            mode = ImagePickerMode.MULTIPLE // default is multi image mode
            language = "in" // Set image picker language
            theme = R.style.Theme_Rivisio

            // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
            returnMode = if (false) ReturnMode.ALL else ReturnMode.NONE
            arrowColor = Color.WHITE // set toolbar arrow up color
            folderTitle = "Folder" // folder selection title
            imageTitle = "Tap to select" // image selection title
            doneButtonText = "DONE" // done button text
            limit = 10 // max images can be selected (99 by default)
            isShowCamera = true // show camera or not (true by default)
            savePath =
                ImagePickerSavePath("Camera") // captured image directory name ("Camera" folder by default)
            savePath =
                ImagePickerSavePath(
                    Environment.getExternalStorageDirectory().path,
                    isRelative = false
                ) // can be a full path
        }
    }
}