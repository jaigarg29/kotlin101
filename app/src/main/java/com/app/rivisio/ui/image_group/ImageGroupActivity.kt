package com.app.rivisio.ui.image_group

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.app.rivisio.RivisioApp
import com.app.rivisio.databinding.ActivityImageGroupBinding
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.esafirm.imagepicker.features.registerImagePicker
import com.esafirm.imagepicker.model.Image
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

const val IMAGE_GROUP_NAME = "image_group_name"
const val IMAGE_LIST = "image_list"

@AndroidEntryPoint
class ImageGroupActivity : BaseActivity(), ImagesAdapter.Callback {

    @Inject
    lateinit var imagesAdapter: ImagesAdapter

    private val imageGroupViewModel: ImageGroupViewModel by viewModels()

    private lateinit var binding: ActivityImageGroupBinding

    override fun getViewModel(): BaseViewModel = imageGroupViewModel

    private var selectedImages = arrayListOf<Image>()

    private val launcher = registerImagePicker { result: List<Image> ->
        result.forEach { image ->
            Timber.e(image.path)
            selectedImages.add(image)
        }

        if (selectedImages.isEmpty()) {

        } else {
            binding.imageGroupIllustration.visibility = View.GONE
            binding.imageGrid.visibility = View.VISIBLE
            imagesAdapter.updateItems(selectedImages)
        }
    }

    companion object {
        fun getStartIntent(context: Context, imageGroupName: String): Intent {
            val intent = Intent(context, ImageGroupActivity::class.java)
            intent.putExtra(IMAGE_GROUP_NAME, imageGroupName)
            return intent
        }

        fun getStartIntent(
            context: Context,
            imageGroupName: String,
            images: ArrayList<Image>
        ): Intent {
            val intent = Intent(context, ImageGroupActivity::class.java)
            intent.putExtra(IMAGE_GROUP_NAME, imageGroupName)
            intent.putParcelableArrayListExtra(IMAGE_LIST, images)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageGroupName = intent.getStringExtra(IMAGE_GROUP_NAME)
        binding.imageGroupName.text = imageGroupName

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.selectImages.setOnClickListener {
            launcher.launch(RivisioApp.config)
        }

        binding.addImageButton.setOnClickListener {
            launcher.launch(RivisioApp.config)
        }

        binding.saveImages.setOnClickListener {
            if (selectedImages.isEmpty()) {
                showError("Select s few images")
                return@setOnClickListener
            }

            val intent = Intent()
            intent.putParcelableArrayListExtra(IMAGE_LIST, selectedImages)
            intent.putExtra(IMAGE_GROUP_NAME, imageGroupName)
            setResult(RESULT_OK, intent)
            finish()
        }

        binding.imageGrid.layoutManager = GridLayoutManager(this, 2)
        binding.imageGrid.adapter = imagesAdapter
        //binding.imageGrid.addItemDecoration(ItemOffsetDecoration(R.dimen.image_grid_spacing))
        imagesAdapter.setCallback(this)

        if (intent.getParcelableArrayListExtra<Image>(IMAGE_LIST) != null) {
            selectedImages = intent.getParcelableArrayListExtra(IMAGE_LIST)!!
            binding.imageGroupIllustration.visibility = View.GONE
            binding.imageGrid.visibility = View.VISIBLE
            imagesAdapter.updateItems(selectedImages)
        }
    }

    override fun onDeleteImageClick(image: Image) {
        Timber.e("Image delete")
        selectedImages.remove(image)
        imagesAdapter.notifyDataSetChanged()
    }
}