package com.app.rivisio.ui.text_note

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.StyleSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.app.rivisio.R
import com.app.rivisio.databinding.ActivityAddTopicBinding
import com.app.rivisio.databinding.ActivityTextNoteBinding

const val HEADING = "heading"
const val CONTENT = "content"

class TextNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTextNoteBinding

    companion object {
        fun getStartIntent(
            context: Context,
            heading: String?,
            content: String?
        ): Intent {
            val intent = Intent(context, TextNoteActivity::class.java)
            intent.putExtra(HEADING, heading)
            intent.putExtra(CONTENT, content)
            return intent
        }

        fun getStartIntent(context: Context) = Intent(context, TextNoteActivity::class.java)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val heading = intent.getStringExtra(HEADING)
        val content = intent.getStringExtra(CONTENT)

        heading?.let {
            binding.heading.setText(it)
        }

        content?.let {
            binding.content.setText(it)
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.floatingActionButton.setOnClickListener {
            if (TextUtils.isEmpty(binding.heading.text)) {
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(binding.content.text)) {
                return@setOnClickListener
            }

            val intent = Intent()
            intent.putExtra(HEADING, binding.heading.text.toString())
            intent.putExtra(CONTENT, binding.content.text.toString())
            setResult(RESULT_OK, intent)
            finish()
        }

    }
}