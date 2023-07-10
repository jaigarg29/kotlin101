package com.app.rivisio.ui.refer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.app.rivisio.BuildConfig
import com.app.rivisio.R
import com.app.rivisio.data.network.REFERRALCODE
import com.app.rivisio.databinding.ActivityReferBinding
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.utils.NetworkResult
import com.app.rivisio.utils.makeGone
import com.app.rivisio.utils.makeVisible
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import timber.log.Timber


@AndroidEntryPoint
class ReferActivity : BaseActivity() {

    private val referViewModel: ReferViewModel by viewModels()

    private lateinit var binding: ActivityReferBinding

    companion object {
        fun getStartIntent(context: Context) = Intent(context, ReferActivity::class.java)
    }

    override fun getViewModel(): BaseViewModel = referViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        binding.faqLayout.webViewClient = WebViewClient()
        binding.faqLayout.settings.javaScriptEnabled = true
        binding.faqLayout.settings.domStorageEnabled = true
        binding.faqLayout.settings.builtInZoomControls = true
        binding.faqLayout.settings.useWideViewPort = true
        binding.faqLayout.settings.loadWithOverviewMode = true
//        binding.faqLayout.loadUrl("https://thorn-jupiter-cc6.notion.site/FAQs-Revu-513498a6de624decab58c5434676dc65")
        binding.faqLayout.loadUrl("https://snapdragon-consonant-027.notion.site/FAQs-Revu-879b2cc69b3b45dd9e16158c7ca11e83?pvs=4")

        binding.tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    findViewById<ScrollView>(R.id.refer_layout).makeVisible()
                    findViewById<WebView>(R.id.faq_layout).makeGone()
                }

                if (tab.position == 1) {
                    findViewById<ScrollView>(R.id.refer_layout).makeGone()
                    findViewById<WebView>(R.id.faq_layout).makeVisible()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        binding.refer1.setOnClickListener { shareApp() }
        binding.refer2.setOnClickListener { shareApp() }
        binding.refer3.setOnClickListener { shareApp() }
        binding.refer4.setOnClickListener { shareApp() }
        binding.refer5.setOnClickListener { shareApp() }

        referViewModel.limitData.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
                    try {
                        val additionalTopics = it.data.asJsonObject["addtionalTopics"].asInt
                        binding.additionalTopicEarned.text =
                            "$additionalTopics Additional Topics Earned"
                        binding.topicSlider.value = (20 + additionalTopics).toFloat()
                    } catch (e: Exception) {
                        Timber.e("Json parsing issue: ")
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

        referViewModel.userData.observe(this, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoading()
                    try {
                        setReferralCode(it.data.asJsonObject[REFERRALCODE].asString)
                    } catch (e: Exception) {
                        Timber.e("Json parsing issue: ")
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

        referViewModel.getUserDetails()
        referViewModel.limitcheck()
    }

    private fun setReferralCode(code: String) {

        binding.referralCode.text = code

        binding.copyReferralCode.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Referral code", code)
            clipboard.setPrimaryClip(clip)

            Toasty.custom(
                this,
                "Referral code coped to clipboard",
                R.drawable.ic_info,
                es.dmoral.toasty.R.color.infoColor,
                Toast.LENGTH_SHORT,
                true,
                true
            ).show()
        }
    }

    private fun shareApp() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }
}