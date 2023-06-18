package com.app.rivisio.ui.view_image_notes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.app.rivisio.data.network.AWS_URL
import com.app.rivisio.databinding.ActivityViewImageNoteBinding
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.home.fragments.home_fragment.TopicFromServer
import com.app.rivisio.ui.topic_details.TopicDetailsActivity
import com.app.rivisio.utils.NetworkResult
import com.app.rivisio.utils.makeGone
import com.app.rivisio.utils.makeVisible
import com.esafirm.imagepicker.model.Image
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ViewImageNoteActivity : BaseActivity(), ViewImageAdapter.Callback {

    @Inject
    lateinit var editImageAdapter: ViewImageAdapter

    private val viewImageNoteViewModel: ViewImageNoteViewModel by viewModels()

    private lateinit var binding: ActivityViewImageNoteBinding

    private lateinit var topicFromServer: TopicFromServer
    private var uploadCount = 0
    private var uploadedImages = ArrayList<String>()
    private var selectedImages = arrayListOf<Image>()


    override fun getViewModel() = viewImageNoteViewModel

    companion object {
        fun getStartIntent(context: Context, id: Int?): Intent {
            val intent = Intent(context, ViewImageNoteActivity::class.java)
            intent.putExtra(TopicDetailsActivity.TOPIC_ID, id)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewImageNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.imageGrid.layoutManager = GridLayoutManager(this, 2)
        binding.imageGrid.adapter = editImageAdapter
        //binding.imageGrid.addItemDecoration(ItemOffsetDecoration(R.dimen.image_grid_spacing))
        editImageAdapter.setCallback(this)


        viewImageNoteViewModel.topicData.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
                    showMessage("Network call is successful")
                    try {

                        topicFromServer = Gson().fromJson(
                            it.data.asJsonObject,
                            TopicFromServer::class.java
                        )

                        if (topicFromServer.imageUrls.size > 0) {
                            binding.imageGrid.makeVisible()

                            editImageAdapter.updateItems(topicFromServer.imageUrls)
                            editImageAdapter.notifyDataSetChanged()
                        } else {
                            binding.imageGrid.makeGone()
                        }
                    } catch (e: Exception) {
                        Timber.e("Json Parsing Error:")
                        Timber.e(e)
                        showError("Something went wrong")
                    }
                }

                is NetworkResult.Loading -> {
                    showLoading()
                }

                is NetworkResult.Error -> {
                    hideLoading()
                    showError(it.message)
                }

                is NetworkResult.Exception -> {
                    hideLoading()
                    showError(it.e.message)
                }

                else -> {
                    hideLoading()
                    Timber.e(it.toString())
                }
            }
        })

        viewImageNoteViewModel.update.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
                    val id = intent.getIntExtra(TopicDetailsActivity.TOPIC_ID, -1)

                    if (id != -1)
                        viewImageNoteViewModel.getTopicDetails(id)
                }

                is NetworkResult.Loading -> {
                    showLoading()
                }

                is NetworkResult.Error -> {
                    hideLoading()
                    showError(it.message)
                }

                is NetworkResult.Exception -> {
                    hideLoading()
                    showError(it.e.message)
                }

                else -> {
                    hideLoading()
                    Timber.e(it.toString())
                }
            }
        })

        viewImageNoteViewModel.imageUploaded.observe(this, Observer { it ->
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        uploadedImages.add(it.data.asJsonObject[AWS_URL].asString)
                        uploadCount++
                        if (uploadCount < selectedImages.size) {
                            viewImageNoteViewModel.uploadImage(selectedImages[uploadCount])
                        } else {

                            hideLoading()
                            showMessage("Images uploaded successfully")

                            val id = intent.getIntExtra(TopicDetailsActivity.TOPIC_ID, -1)

                            uploadedImages.forEach { uploadedImagePath: String ->
                                topicFromServer.imageUrls.add(uploadedImagePath)
                            }

                            viewImageNoteViewModel.updateImageNote(
                                id,
                                topicFromServer
                            )

                        }
                    } catch (e: Exception) {
                        Timber.e("Json parsing issue: ")
                        Timber.e(e)
                        showError("Image upload FAILED....")
                    }
                }

                is NetworkResult.Loading -> {
                    showLoading()
                }

                is NetworkResult.Error -> {
                    hideLoading()
                    showError(it.message)
                    uploadCount = 0
                    uploadedImages.clear()
                }

                is NetworkResult.Exception -> {
                    hideLoading()
                    showError(it.e.message)
                    uploadCount = 0
                    uploadedImages.clear()
                }

                else -> {
                    hideLoading()
                    Timber.e(it.toString())
                    uploadCount = 0
                    uploadedImages.clear()
                }
            }
        })

        val id = intent.getIntExtra(TopicDetailsActivity.TOPIC_ID, -1)

        if (id != -1)
            viewImageNoteViewModel.getTopicDetails(id)
    }

    override fun onImageClick(imageUrl: String) {
        startActivity(PhotoViewActivity.getStartIntent(this@ViewImageNoteActivity, imageUrl))
    }
}