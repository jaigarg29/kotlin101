package com.app.rivisio.ui.home.fragments.topics

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.ListPopupWindow
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rivisio.R
import com.app.rivisio.databinding.FragmentTopicsBinding
import com.app.rivisio.ui.add_notes.TextNoteOptionsAdapter
import com.app.rivisio.ui.base.BaseFragment
import com.app.rivisio.ui.home.fragments.home_fragment.TopicFromServer
import com.app.rivisio.ui.home.fragments.home_fragment.VerticalSpaceItemDecoration
import com.app.rivisio.ui.topic_details.TopicDetailsActivity
import com.app.rivisio.utils.NetworkResult
import com.app.rivisio.utils.getPopupMenu
import com.app.rivisio.utils.getPopupMenuTopic
import com.app.rivisio.utils.makeGone
import com.app.rivisio.utils.makeVisible
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TopicsFragment : BaseFragment(), TopicsAdapterNew.Callback {

    private var _binding: FragmentTopicsBinding? = null
    private val topicViewModel: TopicViewModel by viewModels()

    private var topicsAdapter = TopicsAdapterNew()
    private var totalTopicsCreated: Int = 0
    private lateinit var topicFromServer: TopicFromServer
    private var deleteTopicPosition = -1


    private val binding
        get() = _binding!!

    companion object {
        @JvmStatic
        private var instance: TopicsFragment? = null

        @JvmStatic
        fun newInstance(): TopicsFragment {
            if (instance == null) {
                instance = TopicsFragment()
            }
            return instance!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTopicsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setUp(view: View) {
        binding.topicList.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.topicList.addItemDecoration(
            VerticalSpaceItemDecoration(
                requireContext().resources.getDimension(R.dimen.vertical_offset)
                    .toInt()
            )
        )
        binding.topicList.adapter = topicsAdapter
        topicsAdapter.setCallback(this)

        binding.searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                topicsAdapter.filterList(s.toString())
            }
        })

        val totalTopicsAllowed = 3
        val totalTopicsCountTextView = binding.totalTopicsCount

        topicViewModel.topics.observe(this, Observer {
            Log.d("TAG", "observe topic")
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
                    try {
                        val myType = object : TypeToken<ArrayList<TopicFromServer>>() {}.type
                        val topics = Gson().fromJson<ArrayList<TopicFromServer>>(
                            it.data.asJsonArray,
                            myType
                        )

                        if (topics.isNotEmpty()) {
                            binding.topicsIllustrationImage.makeGone()
                            binding.topicsIllustrationMessage.makeGone()
                            binding.topicsIllustrationText.makeGone()
                            binding.topicList.makeVisible()
                            topicsAdapter.addItems(topics)
                        } else {
                            binding.topicsIllustrationImage.makeVisible()
                            binding.topicsIllustrationMessage.makeVisible()
                            binding.topicsIllustrationText.makeVisible()
                            binding.topicList.makeGone()
                        }

                        totalTopicsCreated = topics.size
                        val topicsCountText = "$totalTopicsCreated/$totalTopicsAllowed Free Topics"
                        totalTopicsCountTextView.text = topicsCountText
                    } catch (e: Exception) {
                        Timber.e("Json parsing issue: ")
                        Timber.e(e)
                        showError("Something went wrong")
                    }
                }

                is NetworkResult.Loading -> {
                    hideLoading()
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

        topicViewModel.getTopicsData()

        topicViewModel.deleteTopic.observe(this, Observer {
            Log.d("TAG", "observe delete")
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
                    topicViewModel.getTopicsData()
                }

                is NetworkResult.Loading -> {
                    hideLoading()
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

        topicViewModel.getTopicsData()
    }


    override fun onTopicClick(topicFromServer: TopicFromServer) {
        startActivity(TopicDetailsActivity.getStartIntent(requireContext(), topicFromServer.id))
    }

    override fun onTopicReviseButtonClick(topicFromServer: TopicFromServer) {
        // Do nothing here
    }

    override fun onMenuIconClick(
        anchorView: AppCompatImageView,
        position: Int,
        topicFromServer: TopicFromServer
    ) {

        deleteTopicPosition = position
        var popup: ListPopupWindow? = null
        val context: Context = requireContext()
        val adapter = TextNoteOptionsAdapter(arrayListOf("Delete"))
        val listener = AdapterView.OnItemClickListener { _, _, position, _ ->
            topicFromServer.id?.let {
                topicViewModel.deleteTopic(it)
            }
            popup?.dismiss()
        }

        popup = getPopupMenu(context, anchorView, adapter, listener, 0, 0)

        popup.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    fun getTotalTopicsCreated(): Int {
//        return totalTopicsCreated
//    }


}
