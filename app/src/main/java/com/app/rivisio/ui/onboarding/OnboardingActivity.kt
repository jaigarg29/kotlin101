package com.app.rivisio.ui.onboarding


import android.content.Context
import android.content.Intent
import com.app.rivisio.ui.base.BaseActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.viewpager2.widget.ViewPager2
import com.app.rivisio.R
import com.app.rivisio.data.prefs.UserState
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.login.LoginActivity
import com.app.rivisio.ui.splash.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : BaseActivity() {

    private val onboardingViewModel: OnboardingViewModel by viewModels()

    private lateinit var dotsArray: Array<AppCompatImageView?>
    private lateinit var dots: LinearLayout
    private lateinit var getStartedButton: AppCompatButton

    private lateinit var viewPager: ViewPager2

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, OnboardingActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.onboard_pager)
        viewPager.adapter = OnboardingAdapter(this)

        dots = findViewById(R.id.dots)
        dotsArray = arrayOfNulls(3)
        for (i in 0 until 3) {
            dotsArray[i] = AppCompatImageView(this)
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(5, 0, 5, 0)
            dotsArray[i]?.layoutParams = params
            dotsArray[i]?.setImageResource(R.drawable.nonselected_drawable)
            dots.addView(dotsArray[i])
            //dots.bringToFront()
        }

        dotsArray[0]?.setImageResource(R.drawable.selected_drawable)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                for (i in 0 until (dotsArray.size ?: 0))
                    dotsArray[i]?.setImageResource(R.drawable.nonselected_drawable)
                dotsArray[position]?.setImageResource(R.drawable.selected_drawable)
            }
        })

        findViewById<AppCompatButton>(R.id.try_button).setOnClickListener {
            onboardingViewModel.setUserState(UserState.ONBOARDED)
            startActivity(LoginActivity.getStartIntent(this@OnboardingActivity))
            finish()
        }

    }

    override fun getViewModel(): BaseViewModel {
        return onboardingViewModel
    }
}