package com.app.rivisio.ui.home.fragments.calendar

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rivisio.R
import com.app.rivisio.databinding.Example7CalendarDayBinding
import com.app.rivisio.databinding.FragmentCalendarBinding
import com.app.rivisio.ui.base.BaseFragment
import com.app.rivisio.ui.home.fragments.home_fragment.TopicFromServer
import com.app.rivisio.ui.home.fragments.home_fragment.TopicsAdapter
import com.app.rivisio.ui.home.fragments.home_fragment.VerticalSpaceItemDecoration
import com.app.rivisio.ui.topic_details.TopicDetailsActivity
import com.app.rivisio.utils.NetworkResult
import com.app.rivisio.utils.getWeekPageTitle
import com.app.rivisio.utils.makeGone
import com.app.rivisio.utils.makeVisible
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@AndroidEntryPoint
class CalendarFragment : BaseFragment(), TopicsAdapter.Callback {

    private var selectedDate = LocalDate.now()

    private val dateFormatter = DateTimeFormatter.ofPattern("dd")

    private var topicsAdapter = TopicsAdapter()

    private var _binding: FragmentCalendarBinding? = null
    private val calendarViewModel: CalendarViewModel by viewModels()

    private val binding
        get() = _binding!!

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setUp(view: View) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            val bind = Example7CalendarDayBinding.bind(view)
            lateinit var day: WeekDay

            init {
                view.setOnClickListener {

//                    if (day.date > LocalDate.now()) {
//                        //don't allow clicks on future dates
//                        return@setOnClickListener
//                    }

                    if (selectedDate != day.date) {
                        val oldDate = selectedDate
                        selectedDate = day.date
                        binding.exSevenCalendar.notifyDateChanged(day.date)
                        oldDate?.let { binding.exSevenCalendar.notifyDateChanged(it) }
                        Timber.e("My log ................")
                    }

                    if (selectedDate == LocalDate.now()) {
                        //binding.calendarImage.setImageResource(R.drawable.daily_log_calendar)
                        //binding.calendarText.text = "Please log in your daily health log"
                        //calendarViewModel.getTodaysRecord()
                    }

                    if (selectedDate < LocalDate.now()) {
                        //binding.calendarImage.setImageResource(R.drawable.no_record)
                        //binding.calendarText.text = "No Data\n" + " Record Found !"
                        //calendarViewModel.getRecord(selectedDate)
                    }

                    calendarViewModel.getTopicsForDate(selectedDate)

                }
            }

            fun bind(day: WeekDay) {
                this.day = day
                bind.exSevenDateText.text = dateFormatter.format(day.date)
                bind.exSevenDayText.text =
                    day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                if (day.date == selectedDate) {
                    bind.exSevenDateText.setTextColor(Color.WHITE)
                    bind.exSevenDayText.setTextColor(Color.WHITE)
                } else {
                    bind.exSevenDateText.setTextColor(Color.BLACK)
                    bind.exSevenDayText.setTextColor(Color.GRAY)
                }

                bind.blueEnclosure.isVisible = day.date == selectedDate

                bind.exSevenDayText.alpha = if (day.date <= LocalDate.now()) 1f else 0.3f
                bind.exSevenDateText.alpha = if (day.date <= LocalDate.now()) 1f else 0.3f

            }
        }

        binding.exSevenCalendar.dayBinder = object : WeekDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: WeekDay) = container.bind(data)
        }

        binding.exSevenCalendar.weekScrollListener = { weekDays ->
            binding.tvDate.text = getWeekPageTitle(weekDays)
        }

        val currentMonth = YearMonth.now()
        binding.exSevenCalendar.setup(
            currentMonth.minusMonths(5).atStartOfMonth(),
            currentMonth.plusMonths(5).atEndOfMonth(),
            firstDayOfWeekFromLocale(),
        )

        binding.exSevenCalendar.scrollToDate(LocalDate.now())

        binding.btnRight.setOnClickListener {
            binding.exSevenCalendar.scrollToDate(
                binding.exSevenCalendar.findFirstVisibleDay()?.date?.plusDays(
                    7
                )!!
            )
        }

        binding.btnLeft.setOnClickListener {
            binding.exSevenCalendar.scrollToDate(
                binding.exSevenCalendar.findFirstVisibleDay()?.date?.minusDays(
                    7
                )!!
            )
        }

        binding.topicsDateList.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.topicsDateList.addItemDecoration(
            VerticalSpaceItemDecoration(
                requireContext().resources.getDimension(R.dimen.vertical_offset)
                    .toInt()
            )
        )
        binding.topicsDateList.adapter = topicsAdapter
        topicsAdapter.setCallback(this)

        calendarViewModel.topics.observe(this, Observer {
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
                            binding.calendarIllustrationContainer.makeGone()
                            binding.topicsDateList.makeVisible()
                            topicsAdapter.updateItems(topics, false)
                        } else{
                            topicsAdapter.updateItems(arrayListOf(), false)//to clear old items
                            binding.calendarIllustrationContainer.makeVisible()
                            binding.topicsDateList.makeGone()
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

        calendarViewModel.getTopicsForDate(LocalDate.now())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onTopicClick(topicFromServer: TopicFromServer) {
        startActivity(TopicDetailsActivity.getStartIntent(requireContext(), topicFromServer.id))
    }

    override fun onTopicReviseButtonClick(topicFromServer: TopicFromServer) {
        //nothing
    }
}