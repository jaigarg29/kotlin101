package com.app.rivisio.ui.onboarding

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.app.rivisio.R


class OnboardingFragment : Fragment() {

    private var description: String? = null
    private var image: Int? = null
    private var background: Int? = null
    private var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_onboarding, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            description = it.getString("description")
            image = it.getInt("image")
            title = it.getString("title")
            background = it.getInt("background")
            view.findViewById<AppCompatImageView>(R.id.onboard_image).setImageResource(image!!)
            view.findViewById<AppCompatTextView>(R.id.onboard_desc).text = description
            view.findViewById<AppCompatTextView>(R.id.onboard_title).text = title
            val drawable: GradientDrawable = view.findViewById<LinearLayout>(R.id.root).background as GradientDrawable
            drawable.setColor(Color.parseColor(getString(background!!)))

        }

    }

    companion object {

        @JvmStatic
        fun newInstance(
            title: String,
            description: String,
            @DrawableRes image: Int,
            @DrawableRes background: Int
        ) =
            OnboardingFragment().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                    putString("description", description)
                    putInt("image", image)
                    putInt("background", background)
                }
            }
    }
}