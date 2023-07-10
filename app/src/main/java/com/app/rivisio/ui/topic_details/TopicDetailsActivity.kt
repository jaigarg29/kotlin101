package com.app.rivisio.ui.topic_details

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.ListPopupWindow
import androidx.lifecycle.Observer
import com.app.rivisio.BuildConfig
import com.app.rivisio.R
import com.app.rivisio.databinding.ActivityTopicDetailsBinding
import com.app.rivisio.ui.add_notes.NoteTypeAdapter
import com.app.rivisio.ui.add_notes.TextNoteOptionsAdapter
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.edit_image_note.EditImageNoteActivity
import com.app.rivisio.ui.home.fragments.home_fragment.TopicFromServer
import com.app.rivisio.ui.text_note.CONTENT
import com.app.rivisio.ui.text_note.HEADING
import com.app.rivisio.ui.text_note.TextNoteActivity
import com.app.rivisio.ui.view_image_notes.ViewImageNoteActivity
import com.app.rivisio.utils.CommonUtils
import com.app.rivisio.utils.NetworkResult
import com.app.rivisio.utils.RevisionInterval
import com.app.rivisio.utils.getPopupMenu
import com.app.rivisio.utils.makeGone
import com.app.rivisio.utils.makeVisible
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class TopicDetailsActivity : BaseActivity() {

    companion object {
        const val TOPIC_ID = "topic_id"
        fun getStartIntent(context: Context, id: Int?): Intent {
            val intent = Intent(context, TopicDetailsActivity::class.java)
            intent.putExtra(TOPIC_ID, id)
            return intent
        }
    }

    private val topicDetailsViewModel: TopicDetailsViewModel by viewModels()

    private lateinit var binding: ActivityTopicDetailsBinding
    private lateinit var topicFromServer: TopicFromServer

    override fun getViewModel(): BaseViewModel {
        return topicDetailsViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopicDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        binding.addButton.setOnClickListener {
            var popup: ListPopupWindow? = null

            val adapter = NoteTypeAdapter(arrayListOf("Text Note", "Image Group"))
            val listener = AdapterView.OnItemClickListener { _, _, position, _ ->
                if (position == 0) {
                    val noteJsonObject = JsonParser().parse(topicFromServer.notes).asJsonObject

                    if (noteJsonObject["title"].asString.isEmpty() && noteJsonObject["body"].asString.isEmpty()) {
                        addTextNoteLauncher.launch(TextNoteActivity.getStartIntent(this@TopicDetailsActivity))
                    } else {
                        showError("Not allowed")
                    }
                } else {
                    if (topicFromServer.imageUrls.size == 0) {
                        startActivity(
                            EditImageNoteActivity.getStartIntent(
                                this@TopicDetailsActivity,
                                topicFromServer.id
                            )
                        )
                    } else {
                        showError("Not allowed")
                    }
                }
                popup?.dismiss()
            }
            popup = getPopupMenu(
                this@TopicDetailsActivity,
                it,
                adapter,
                listener,
                resources.getDimension(R.dimen.popup_vertical_offset).toInt(),
                resources.getDimension(R.dimen.popup_horizontal_offset).toInt()
            )

            popup?.show()
        }

        topicDetailsViewModel.topicData.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
//                    showMessage("Network call is successful")
                    try {
                        topicFromServer = Gson().fromJson(
                            it.data.asJsonObject,
                            TopicFromServer::class.java
                        )

                        binding.topicName.text = topicFromServer.name

                        renderRevisionTimeLine(topicFromServer)

                        binding.topicDetailIllustration.makeVisible()

                        renderTextNote(topicFromServer)

                        renderImageGroup(topicFromServer)

                        renderReviseButton(topicFromServer)

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

        topicDetailsViewModel.update.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
                    val id = intent.getIntExtra(TOPIC_ID, -1)

                    if (id != -1)
                        topicDetailsViewModel.getTopicDetails(id)
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

        val id = intent.getIntExtra(TOPIC_ID, -1)

        if (id != -1)
            topicDetailsViewModel.getTopicDetails(id)

    }

    private fun renderReviseButton(topicFromServer: TopicFromServer) {

        binding.horizontalRule.makeGone()
        binding.markRevised.makeGone()

        if (topicFromServer.rev1Status == "wait") {
            binding.horizontalRule.makeVisible()
            binding.markRevised.makeVisible()
            val revsion = mapOf(
                "rev1" to LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            )
            binding.markRevised.setOnClickListener {
                topicDetailsViewModel.reviseTopic(topicFromServer.id, revsion)
            }
            return
        }

        if (topicFromServer.rev2Status == "wait") {
            binding.horizontalRule.makeVisible()
            binding.markRevised.makeVisible()
            val revsion = mapOf(
                "rev2" to LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            )
            binding.markRevised.setOnClickListener {
                topicDetailsViewModel.reviseTopic(topicFromServer.id, revsion)
            }
            return
        }

        if (topicFromServer.rev3Status == "wait") {
            binding.horizontalRule.makeVisible()
            binding.markRevised.makeVisible()
            val revsion = mapOf(
                "rev3" to LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            )
            binding.markRevised.setOnClickListener {
                topicDetailsViewModel.reviseTopic(topicFromServer.id, revsion)
            }
            return
        }

        if (topicFromServer.rev4Status == "wait") {
            binding.horizontalRule.makeVisible()
            binding.markRevised.makeVisible()
            val revsion = mapOf(
                "rev4" to LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            )
            binding.markRevised.setOnClickListener {
                topicDetailsViewModel.reviseTopic(topicFromServer.id, revsion)
            }
            return
        }
    }

    private fun renderImageGroup(topicFromServer: TopicFromServer) {

        if (topicFromServer.imageUrls.size == 0) {
            binding.addButton.makeVisible()
            binding.imageNoteContainer.makeGone()
            return
        }

        binding.topicDetailIllustration.makeGone()
        binding.imageNoteContainer.makeVisible()

        binding.imageGroupName.text = "Image Notes"

        for (i in 0 until topicFromServer.imageUrls.size) {
            when (i) {
                0 -> {
                    Glide.with(this@TopicDetailsActivity)
                        .asBitmap()
                        .load(BuildConfig.BASE_URL + "/users/getfile?awsUrl=" + topicFromServer.imageUrls[0])
                        .centerCrop()
                        .into(binding.image1)
                }

                1 -> {
                    Glide.with(this@TopicDetailsActivity)
                        .asBitmap()
                        .load(BuildConfig.BASE_URL + "/users/getfile?awsUrl=" + topicFromServer.imageUrls[1])
                        .centerCrop()
                        .into(binding.image2)
                }

                2 -> {
                    Glide.with(this@TopicDetailsActivity)
                        .asBitmap()
                        .load(BuildConfig.BASE_URL + "/users/getfile?awsUrl=" + topicFromServer.imageUrls[2])
                        .centerCrop()
                        .into(binding.image3)
                }

                3 -> {
                    Glide.with(this@TopicDetailsActivity)
                        .asBitmap()
                        .load(BuildConfig.BASE_URL + "/users/getfile?awsUrl=" + topicFromServer.imageUrls[3])
                        .centerCrop()
                        .into(binding.image4)
                }
            }
        }

        binding.editImages.setOnClickListener {
            var popup: ListPopupWindow? = null

            val adapter = TextNoteOptionsAdapter(arrayListOf("Edit", "Delete"))
            val listener = AdapterView.OnItemClickListener { _, _, position, _ ->
                if (position == 0) {
                    startActivity(
                        EditImageNoteActivity.getStartIntent(
                            this@TopicDetailsActivity,
                            topicFromServer.id
                        )
                    )

                } else {
                    val id = intent.getIntExtra(TOPIC_ID, -1)

                    topicFromServer.imageUrls = arrayListOf()

                    topicDetailsViewModel.updateTextNote(
                        id,
                        topicFromServer
                    )

                }
                popup?.dismiss()
            }
            popup = getPopupMenu(this@TopicDetailsActivity, it, adapter, listener, 0, 0)

            popup.show()

        }

        binding.imageNoteContainer.setOnClickListener {
            startActivity(
                ViewImageNoteActivity.getStartIntent(
                    this@TopicDetailsActivity,
                    topicFromServer.id
                )
            )
        }

    }

    private fun renderTextNote(topicFromServer: TopicFromServer) {
        val noteJsonObject = JsonParser().parse(topicFromServer.notes).asJsonObject

        if (noteJsonObject["title"].asString.isEmpty() && noteJsonObject["body"].asString.isEmpty()) {
            binding.addButton.makeVisible()
            binding.textNoteContainer.makeGone()
            return
        }

        binding.topicDetailIllustration.makeGone()
        binding.textNoteContainer.makeVisible()

        binding.textNoteHeading.text = noteJsonObject["title"].asString
        binding.textNoteContent.text = noteJsonObject["body"].asString


        binding.editNote.setOnClickListener {

            var popup: ListPopupWindow? = null
            val context: Context = this
            val adapter = TextNoteOptionsAdapter(arrayListOf("Edit", "Delete"))
            val listener = AdapterView.OnItemClickListener { _, _, position, _ ->
                if (position == 0) {
                    addTextNoteLauncher.launch(
                        TextNoteActivity.getStartIntent(
                            this@TopicDetailsActivity,
                            noteJsonObject["title"].asString,
                            noteJsonObject["body"].asString
                        )
                    )
                } else {

                    val id = intent.getIntExtra(TOPIC_ID, -1)

                    val jsonNote = JsonObject()
                    jsonNote.addProperty("title", "")
                    jsonNote.addProperty("body", "")
                    topicFromServer.notes = jsonNote.toString()

                    topicDetailsViewModel.updateTextNote(
                        id,
                        topicFromServer
                    )
                }
                popup?.dismiss()
            }

            popup = getPopupMenu(context, it, adapter, listener, 0, 0)

            popup.show()

        }

        binding.textNoteContainer.setOnClickListener {
            BottomSheetDialogs.showTextNote(
                this@TopicDetailsActivity,
                noteJsonObject["title"].asString,
                noteJsonObject["body"].asString
            )
        }
    }

    override fun onRestart() {
        super.onRestart()

        val id = intent.getIntExtra(TOPIC_ID, -1)

        if (id != -1)
            topicDetailsViewModel.getTopicDetails(id)
    }

    private fun renderRevisionTimeLine(topicFromServer: TopicFromServer) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dateTime = LocalDateTime.parse(
            topicFromServer.studiedOn,
            formatter
        )

        binding.date1.text = getFormattedDate(dateTime.plusDays(RevisionInterval.ONE.days))
        binding.date2.text = getFormattedDate(dateTime.plusDays(RevisionInterval.SEVEN.days))
        binding.date3.text = getFormattedDate(dateTime.plusDays(RevisionInterval.THIRTY.days))
        binding.date4.text = getFormattedDate(dateTime.plusDays(RevisionInterval.NINETY.days))

        binding.circle1.backgroundTintList =
            CommonUtils.getColorForRevision(topicFromServer.rev1Status)

        binding.circle2.backgroundTintList =
            CommonUtils.getColorForRevision(topicFromServer.rev2Status)

        binding.circle3.backgroundTintList =
            CommonUtils.getColorForRevision(topicFromServer.rev3Status)

        binding.circle4.backgroundTintList =
            CommonUtils.getColorForRevision(topicFromServer.rev4Status)
    }

    private fun getFormattedDate(dateTime: LocalDateTime): String {
        val formatter2 = DateTimeFormatter.ofPattern("dd/MM")
        return dateTime.format(formatter2)
    }

    private var addTextNoteLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.let {
                    Timber.e(it.getStringExtra(HEADING))
                    Timber.e(it.getStringExtra(CONTENT))

                    val id = intent.getIntExtra(TOPIC_ID, -1)

                    val jsonNote = JsonObject()
                    jsonNote.addProperty("title", it.getStringExtra(HEADING))
                    jsonNote.addProperty("body", it.getStringExtra(CONTENT))
                    topicFromServer.notes = jsonNote.toString()

                    topicDetailsViewModel.updateTextNote(
                        id,
                        topicFromServer
                    )
                }
            }
        }
}