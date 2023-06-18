package com.app.rivisio.data.prefs

enum class UserState(val value: Int) {
    EMPTY(0),
    ONBOARDED(1),
    LOGGED_IN(2),
    LOGGED_OUT(3);

    companion object {
        fun getByValue(value: Int) = values().firstOrNull { it.value == value }
    }
}