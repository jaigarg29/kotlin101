package com.app.rivisio.ui.home.fragments.home_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rivisio.R
import com.app.rivisio.databinding.FragmentHomeBinding
import com.app.rivisio.ui.base.BaseFragment
import com.app.rivisio.ui.refer.ReferActivity
import com.app.rivisio.ui.subscribe.SubscribeActivity
import com.app.rivisio.ui.topic_details.TopicDetailsActivity
import com.app.rivisio.utils.CommonUtils
import com.app.rivisio.utils.NetworkResult
import com.app.rivisio.utils.makeGone
import com.app.rivisio.utils.makeVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class HomeFragment : BaseFragment(), TopicsAdapter.Callback {

    private var _binding: FragmentHomeBinding? = null
    private val homeViewModel: HomeViewModel by viewModels()

    private var topicsAdapter = TopicsAdapter()

    private val binding
        get() = _binding!!

    //topicsMissedList

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setUp(view: View) {

        binding.goodMorningText.text = CommonUtils.getGreetingMessage()

        binding.referButton.setOnClickListener {
            startActivity(ReferActivity.getStartIntent(requireContext()))
        }

        binding.subscribe.setOnClickListener {
            startActivity(SubscribeActivity.getStartIntent(requireContext()))
        }

        binding.statsButton.setOnClickListener {
            homeViewModel.getUserStats()
        }

        homeViewModel.userStats.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
                    showStatsBottomSheet(it.data)
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

        homeViewModel.userName.observe(this, Observer {
            binding.userName.text = it
        })

        homeViewModel.getUserDetails()

        val homeTabs = HomeTabs(binding)

        binding.topicsList.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.topicsList.addItemDecoration(
            VerticalSpaceItemDecoration(
                requireContext().resources.getDimension(R.dimen.vertical_offset)
                    .toInt()
            )
        )
        binding.topicsList.adapter = topicsAdapter
        topicsAdapter.setCallback(this)

        homeViewModel.update.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()

                    homeViewModel.getTopicsData()

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

        homeViewModel.topics.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
                    try {

                        val myType = object : TypeToken<ArrayList<TopicFromServer>>() {}.type

                        val topicsMissedList = Gson().fromJson<ArrayList<TopicFromServer>>(
                            it.data.asJsonObject["topicsMissedList"].asJsonArray,
                            myType
                        )

                        val topicsTodayList = Gson().fromJson<ArrayList<TopicFromServer>>(
                            it.data.asJsonObject["topicsTodayList"].asJsonArray,
                            myType
                        )

                        val topicsUpcomingList = Gson().fromJson<ArrayList<TopicFromServer>>(
                            it.data.asJsonObject["topicsUpcomingList"].asJsonArray,
                            myType
                        )

                        if (topicsTodayList.isEmpty() && topicsMissedList.isEmpty() && topicsUpcomingList.isEmpty()) {
                            binding.topicsList.visibility = View.GONE
                            binding.homeTabs.visibility = View.GONE
                            binding.homeIllustrationContainer.visibility = View.VISIBLE
                            binding.homeIllustration.setImageResource(R.drawable.start_journey)
                            binding.homeIllustrationMessage.text = "Start Your Journey"
                            binding.homeIllustrationText.text =
                                "Every big step start with small step. Add your first topic and start your journey!"
                        } else {
                            binding.homeTabs.visibility = View.VISIBLE
                            homeTabs.setTabClickListener { v ->
                                when (v.id) {
                                    R.id.tab_today -> {
                                        homeTabs.setTabSelected(0)
                                        showTopicsList(topicsTodayList, true)
                                        if (topicsTodayList.isEmpty()) {
                                            renderEmptyListIllustration()
                                        } else {
                                            renderList()
                                        }
                                    }

                                    R.id.tab_missed -> {
                                        homeTabs.setTabSelected(1)
                                        showTopicsList(topicsMissedList)
                                        if (topicsMissedList.isEmpty()) {
                                            renderEmptyListIllustration()

                                        } else {
                                            renderList()
                                        }
                                    }

                                    R.id.tab_upcoming -> {
                                        homeTabs.setTabSelected(2)
                                        showTopicsList(topicsUpcomingList)
                                        if (topicsUpcomingList.isEmpty()) {
                                            renderEmptyListIllustration()

                                        } else {
                                            renderList()
                                        }
                                    }
                                }
                            }


                            homeTabs.setTabSelected(0)
                            showTopicsList(topicsTodayList, true)
                            if (topicsTodayList.isEmpty()) {
                                renderEmptyListIllustration()
                            } else {
                                renderList()
                            }
                        }

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

        homeViewModel.getTopicsData()
    }

    private fun showStatsBottomSheet(data: JsonElement) {
        try {
            val referralView = layoutInflater.inflate(R.layout.stats_dialog_layout, null)
            referralView.findViewById<AppCompatTextView>(R.id.total_topic).text =
                data.asJsonObject["totalCount"].asString
            referralView.findViewById<AppCompatTextView>(R.id.missed_topic).text =
                data.asJsonObject["missedCount"].asString
            referralView.findViewById<AppCompatTextView>(R.id.ontrack_topic).text =
                data.asJsonObject["inprogressCount"].asString
            referralView.findViewById<AppCompatTextView>(R.id.completed_topic).text =
                data.asJsonObject["completedCount"].asString
            val dialog = BottomSheetDialog(requireContext())
            dialog.setContentView(referralView)
            dialog.setCancelable(true)
            dialog.show()
        } catch (e: Exception) {
            Timber.e("Json parsing issue: ")
            Timber.e(e)
            showError("Something went wrong")
        }
    }

    private fun renderList() {
        binding.topicsList.makeVisible()
        binding.homeTabs.makeVisible()
        binding.homeIllustrationContainer.makeGone()
    }

    private fun renderEmptyListIllustration() {
        binding.topicsList.makeGone()
        binding.homeTabs.makeVisible()
        binding.homeIllustrationContainer.makeVisible()
        binding.homeIllustration.setImageResource(R.drawable.meditation)
        binding.homeIllustrationMessage.text = "No topics here"
        binding.homeIllustrationText.text =
            "You have revised all the topics, add new to create more."
    }

    private fun showTopicsList(topics: ArrayList<TopicFromServer>, isTodaysTopic: Boolean = false) {
        topicsAdapter.updateItems(topics, isTodaysTopic)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onTopicClick(topicFromServer: TopicFromServer) {
        startActivity(TopicDetailsActivity.getStartIntent(requireContext(), topicFromServer.id))
    }

    override fun onTopicReviseButtonClick(topicFromServer: TopicFromServer) {

        var revision: Map<String, String>

        if (topicFromServer.rev1Status == "wait") {
            revision = mapOf(
                "rev1" to LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            )

            showRevisePrompt(topicFromServer, revision)
            return
        }

        if (topicFromServer.rev2Status == "wait") {
            revision = mapOf(
                "rev2" to LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            )

            showRevisePrompt(topicFromServer, revision)
            return
        }

        if (topicFromServer.rev3Status == "wait") {
            revision = mapOf(
                "rev3" to LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            )

            showRevisePrompt(topicFromServer, revision)
            return
        }

        if (topicFromServer.rev4Status == "wait") {
            revision = mapOf(
                "rev4" to LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            )

            showRevisePrompt(topicFromServer, revision)
            return
        }
    }

    private fun showRevisePrompt(topicFromServer: TopicFromServer, revision: Map<String, String>) {
        var dialog: AlertDialog? = null

        val view =
            LayoutInflater.from(requireContext()).inflate(R.layout.revise_dialog, null)

        val dialogBuilder =
            MaterialAlertDialogBuilder(
                requireContext(),
                R.style.ThemeOverlay_App_MaterialAlertDialog
            )
                .setCancelable(true)
                .setView(view)


        view.findViewById<AppCompatButton>(R.id.revise)?.setOnClickListener {
            dialog?.dismiss()
            homeViewModel.reviseTopic(topicFromServer.id, revision)
        }

        view.findViewById<AppCompatButton>(R.id.cancel)?.setOnClickListener {
            dialog?.dismiss()
        }

        dialog = dialogBuilder.show()
    }
}