package com.app.rivisio.utils

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.app.rivisio.R
import java.util.Calendar
import java.util.regex.Matcher
import java.util.regex.Pattern

object CommonUtils {

    fun showLoadingDialog(context: Context): Dialog {
        val progressDialog = Dialog(context)
        progressDialog.show()
        if (progressDialog.window != null) {
            progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        progressDialog.setContentView(R.layout.progress_dialog)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        return progressDialog
    }

    fun isEmailValid(email: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val EMAIL_PATTERN = ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
        pattern = Pattern.compile(EMAIL_PATTERN)
        matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun isValidCharacter(symbol: Char): Boolean {
        if (Character.isLetter(symbol))
            return true

        if (Character.isSpaceChar(symbol))
            return true

        return false
    }

    fun getOrdinal(n: Int): String {
        var ord = "th"

        if (n % 10 == 1 && n % 100 != 11) {
            ord = "st"
        } else if (n % 10 == 2 && n % 100 != 12) {
            ord = "nd"
        } else if (n % 10 == 3 && n % 100 != 13) {
            ord = "rd"
        }

        return ord
    }

    fun getColorForRevision(revStatus: String?): ColorStateList {
        return when (revStatus) {
            "stop" -> {
                ColorStateList.valueOf(Color.parseColor("#F69032"))
            }

            "wait" -> {
                ColorStateList.valueOf(Color.parseColor("#FFB904"))
            }

            "done" -> {
                ColorStateList.valueOf(Color.parseColor("#0E965E"))
            }

            else -> {
                ColorStateList.valueOf(Color.parseColor("#E8ECEF"))
            }
        }
    }

    fun getGreetingMessage():String{
        val c = Calendar.getInstance()
        val timeOfDay = c.get(Calendar.HOUR_OF_DAY)

        return when (timeOfDay) {
            in 0..11 -> "Good Morning"
            in 12..15 -> "Good Afternoon"
            in 16..20 -> "Good Evening"
            in 21..23 -> "Good Night"
            else -> "Hello"
        }
    }


}