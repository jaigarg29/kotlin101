package com.app.rivisio.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentTransaction
import com.app.rivisio.R
import com.app.rivisio.databinding.ActivityHomeBinding
import com.app.rivisio.reminder.RemindersManager
import com.app.rivisio.ui.add_topic.AddTopicActivity
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.home.fragments.account.AccountFragment
import com.app.rivisio.ui.home.fragments.calendar.CalendarFragment
import com.app.rivisio.ui.home.fragments.home_fragment.HomeFragment
import com.app.rivisio.ui.home.fragments.topics.TopicsFragment
import com.app.rivisio.ui.topic_details.TopicDetailsActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    val PERMISSION_REQUEST_CODE = 800

    private val homeActivityViewModel: HomeActivityViewModel by viewModels()

    private lateinit var binding: ActivityHomeBinding

    @Inject
    lateinit var remindersManager: RemindersManager

    private var pressedTime: Long = 0

    companion object {

        const val TOPIC_ID = "topic_id"
        fun getStartIntent(context: Context) = Intent(context, HomeActivity::class.java)

        fun getStartIntentNewTask(context: Context, topicId: Int): Intent {
            val intent = Intent(context, HomeActivity::class.java)
            intent.putExtra(TOPIC_ID, topicId)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }

    override fun getViewModel(): BaseViewModel = homeActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.floatingButton.setOnClickListener {
            startActivity(AddTopicActivity.getStartIntent(this@HomeActivity))
        }

        setUpFragments()
        //This is to navigate the user after successful topic creation.
        //The topicId is passed from the AddNotesActivity and we check it here and redirect to teh topic details
        if (intent != null && intent.getIntExtra(TOPIC_ID, -1) != -1) {
            startActivity(
                TopicDetailsActivity.getStartIntent(
                    this@HomeActivity,
                    intent.getIntExtra(TOPIC_ID, -1)
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()

        homeActivityViewModel.syncPurchases()

    }

    private fun setUpFragments() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_item -> {
                    val fragment = HomeFragment.newInstance()
                    val fragmentTransaction: FragmentTransaction =
                        supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.home_container, fragment, "")
                    fragmentTransaction.commit()
                }

                R.id.topics_item -> {
                    val fragment = TopicsFragment.newInstance()
                    val fragmentTransaction: FragmentTransaction =
                        supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.home_container, fragment, "")
                    fragmentTransaction.commit()
                }

                R.id.calendar_item -> {
                    val fragment = CalendarFragment.newInstance()
                    val fragmentTransaction: FragmentTransaction =
                        supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.home_container, fragment, "")
                    fragmentTransaction.commit()
                }

                R.id.account_item -> {
                    val fragment = AccountFragment.newInstance()
                    val fragmentTransaction: FragmentTransaction =
                        supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.home_container, fragment, "")
                    fragmentTransaction.commit()
                }
            }
            true
        }

        binding.bottomNav.selectedItemId = R.id.home_item

        getPermissions()
    }

    private fun getPermissions() {
        if (Build.VERSION.SDK_INT >= 31) {

            var permissionsArray = arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM)

            if (Build.VERSION.SDK_INT >= 33) {
                permissionsArray = arrayOf(
                    Manifest.permission.SCHEDULE_EXACT_ALARM,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }

            if (!hasPermissions(permissionsArray))
                requestPermissionsSafely(permissionsArray, PERMISSION_REQUEST_CODE)
            else
                remindersManager.startReminder(applicationContext)

        } else {
            remindersManager.startReminder(applicationContext)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    var allPermissionsGranted = true
                    grantResults.forEach {
                        if (it == PackageManager.PERMISSION_DENIED) {
                            allPermissionsGranted = false
                        }
                    }
                    if (allPermissionsGranted) {
                        showMessage("Permissions granted")
                        remindersManager.startReminder(applicationContext)
                    } else
                        showError("Permissions not granted, some features will not work")

                } else {
                    showError("Permissions not granted, some features will not work")
                }
                return
            }

            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            showMessage("Press back again to exit")
        }
        pressedTime = System.currentTimeMillis()
    }
}